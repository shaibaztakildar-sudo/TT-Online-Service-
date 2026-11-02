const { onRequest, onCall, HttpsError } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const admin = require("firebase-admin");
const crypto = require("crypto");
const Razorpay = require("razorpay");

// Initialize Firebase Admin
if (!admin.apps.length) {
  admin.initializeApp();
}

const db = admin.firestore();

// Define Secrets in Firebase Secret Manager
const razorpayKeyId = defineSecret("RAZORPAY_KEY_ID");
const razorpayKeySecret = defineSecret("RAZORPAY_KEY_SECRET");
const razorpayWebhookSecret = defineSecret("RAZORPAY_WEBHOOK_SECRET");

// Official TT Online Services Catalog with verified INR prices
const SERVICE_PRICES = {
  "pan-service": { id: "pan-service", title: "PAN Card Assistance", feeInr: 299 },
  "voter-service": { id: "voter-service", title: "Voter ID Assistance", feeInr: 199 },
  "aadhaar-service": { id: "aadhaar-service", title: "Aadhaar Official Guidance", feeInr: 149 },
  "dl-service": { id: "dl-service", title: "Driving Licence Assistance", feeInr: 499 },
  "passport-service": { id: "passport-service", title: "Passport Application Assistance", feeInr: 899 },
  "birth-service": { id: "birth-service", title: "Birth Certificate Assistance", feeInr: 349 },
  "income-service": { id: "income-service", title: "Income Certificate Assistance", feeInr: 249 },
  "caste-service": { id: "caste-service", title: "Caste Certificate Assistance", feeInr: 249 },
  "domicile-service": { id: "domicile-service", title: "Domicile Certificate Assistance", feeInr: 249 }
};

/**
 * 1. createRazorpayOrder
 * Callable Cloud Function: Securely creates an order on Razorpay using live credentials.
 * Secrets: RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET
 */
exports.createRazorpayOrder = onCall(
  {
    secrets: [razorpayKeyId, razorpayKeySecret],
    cors: true
  },
  async (request) => {
    // 1. Authentication check
    if (!request.auth) {
      throw new HttpsError(
        "unauthenticated",
        "User must be authenticated with Google to initiate payment."
      );
    }

    const { serviceId, applicationType, applicantName } = request.data || {};
    if (!serviceId || !SERVICE_PRICES[serviceId]) {
      throw new HttpsError(
        "invalid-argument",
        `Invalid or missing serviceId: ${serviceId}`
      );
    }

    const service = SERVICE_PRICES[serviceId];
    const amountInPaise = Math.round(service.feeInr * 100);

    const keyId = razorpayKeyId.value();
    const keySecret = razorpayKeySecret.value();

    if (!keyId || !keySecret) {
      throw new HttpsError(
        "failed-precondition",
        "Razorpay credentials are not configured in Firebase Secrets."
      );
    }

    const razorpay = new Razorpay({
      key_id: keyId,
      key_secret: keySecret
    });

    const receiptId = `rcpt_${Date.now()}_${Math.floor(1000 + Math.random() * 9000)}`;

    try {
      const order = await razorpay.orders.create({
        amount: amountInPaise,
        currency: "INR",
        receipt: receiptId,
        notes: {
          userId: request.auth.uid,
          userEmail: request.auth.token.email || "",
          serviceId: service.id,
          serviceName: service.title,
          applicationType: applicationType || "New Application",
          applicantName: applicantName || ""
        }
      });

      // Record pending order in Firestore for tracking and idempotency
      await db.collection("payments").doc(order.id).set({
        orderId: order.id,
        paymentId: "",
        userId: request.auth.uid,
        amount: service.feeInr,
        currency: "INR",
        status: "PENDING",
        serviceName: service.title,
        serviceId: service.id,
        receiptNumber: "",
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      }, { merge: true });

      // Return public Order details. Secret is NEVER returned!
      return {
        success: true,
        orderId: order.id,
        amount: order.amount,
        currency: order.currency,
        keyId: keyId,
        serviceName: service.title,
        feeInr: service.feeInr
      };
    } catch (err) {
      console.error("Razorpay order creation error:", err);
      throw new HttpsError(
        "internal",
        `Failed to create Razorpay order: ${err.message || err}`
      );
    }
  }
);

/**
 * 2. verifyRazorpayPayment
 * Callable Cloud Function: Verifies HMAC signature, fetches payment from Razorpay API,
 * ensures no duplicate claims, creates/updates payment record and activates the application.
 */
exports.verifyRazorpayPayment = onCall(
  {
    secrets: [razorpayKeyId, razorpayKeySecret],
    cors: true
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError(
        "unauthenticated",
        "User must be authenticated to verify payment."
      );
    }

    const {
      razorpayOrderId,
      razorpayPaymentId,
      razorpaySignature,
      applicationId,
      serviceId,
      applicantName,
      applicantPhone,
      applicantEmail,
      applicantAddress,
      idNumber,
      attachedCount,
      applicationType
    } = request.data || {};

    if (!razorpayOrderId || !razorpayPaymentId || !razorpaySignature || !applicationId) {
      throw new HttpsError(
        "invalid-argument",
        "Missing required payment verification details (orderId, paymentId, signature, applicationId)."
      );
    }

    const keySecret = razorpayKeySecret.value();
    const keyId = razorpayKeyId.value();

    // 1. Verify HMAC-SHA256 Signature
    const expectedSignature = crypto
      .createHmac("sha256", keySecret)
      .update(`${razorpayOrderId}|${razorpayPaymentId}`)
      .digest("hex");

    const isSignatureValid = crypto.timingSafeEqual(
      Buffer.from(expectedSignature, "utf-8"),
      Buffer.from(razorpaySignature, "utf-8")
    );

    if (!isSignatureValid) {
      console.error(`Invalid signature for payment ${razorpayPaymentId}`);
      throw new HttpsError(
        "permission-denied",
        "Payment verification failed: Invalid Razorpay signature."
      );
    }

    // 2. Prevent duplicate processing / replay attacks
    const existingPaymentDoc = await db.collection("payments").doc(razorpayPaymentId).get();
    if (existingPaymentDoc.exists && existingPaymentDoc.data().status === "SUCCESS") {
      const data = existingPaymentDoc.data();
      return {
        success: true,
        alreadyProcessed: true,
        receiptNumber: data.receiptNumber,
        paymentId: razorpayPaymentId,
        applicationId: data.applicationId
      };
    }

    // 3. Fetch verified payment status from Razorpay REST API
    const razorpay = new Razorpay({
      key_id: keyId,
      key_secret: keySecret
    });

    let paymentDetails;
    try {
      paymentDetails = await razorpay.payments.fetch(razorpayPaymentId);
    } catch (fetchErr) {
      console.error("Error fetching payment from Razorpay:", fetchErr);
      throw new HttpsError(
        "internal",
        "Unable to fetch live payment verification from Razorpay."
      );
    }

    if (!["captured", "authorized"].includes(paymentDetails.status)) {
      throw new HttpsError(
        "failed-precondition",
        `Payment is not successful on Razorpay. Current status: ${paymentDetails.status}`
      );
    }

    const amountPaidInInr = (paymentDetails.amount || 0) / 100;
    const service = SERVICE_PRICES[serviceId] || { title: "Document Assistance Service" };
    const receiptNumber = `REC-TT-${Date.now().toString().slice(-6)}`;
    const batch = db.batch();

    // 4. Save verified Payment record
    const paymentRef = db.collection("payments").doc(razorpayPaymentId);
    batch.set(paymentRef, {
      paymentId: razorpayPaymentId,
      orderId: razorpayOrderId,
      applicationId: applicationId,
      userId: request.auth.uid,
      amount: amountPaidInInr,
      currency: "INR",
      status: "SUCCESS",
      razorpayPaymentId: razorpayPaymentId,
      razorpaySignature: razorpaySignature,
      receiptNumber: receiptNumber,
      serviceName: service.title,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    // Clean up temporary pending order document if created earlier
    if (razorpayOrderId !== razorpayPaymentId) {
      const pendingOrderRef = db.collection("payments").doc(razorpayOrderId);
      batch.delete(pendingOrderRef);
    }

    // 5. Create or Update ServiceApplication record
    const appRef = db.collection("applications").doc(applicationId);
    const formDataJson = JSON.stringify({
      address: applicantAddress || "",
      idNumber: idNumber || "",
      paymentMethod: "Razorpay Live Checkout"
    });

    batch.set(appRef, {
      applicationId: applicationId,
      userId: request.auth.uid,
      customerName: applicantName || request.auth.token.name || "Customer",
      customerEmail: applicantEmail || request.auth.token.email || "",
      customerPhone: applicantPhone || "",
      serviceId: serviceId || "service",
      serviceName: service.title,
      applicationType: applicationType || "New Application",
      formData: formDataJson,
      status: "Pending", // Ready for operator review
      paymentStatus: "Paid",
      paymentId: razorpayPaymentId,
      paymentOrderId: razorpayOrderId,
      amountPaid: amountPaidInInr,
      consentGiven: true,
      documentCount: Number(attachedCount || 1),
      adminNotes: "Application submitted with verified Razorpay payment. Awaiting operator review.",
      additionalInfoRequested: "",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    // 6. In-App Notification
    const notifRef = db.collection("notifications").doc(`notif_${Date.now()}`);
    batch.set(notifRef, {
      notificationId: notifRef.id,
      userId: request.auth.uid,
      title: `Payment Verified: ₹${Math.round(amountPaidInInr)}`,
      message: `Payment for ${service.title} (#${applicationId}) has been verified. Official Receipt: ${receiptNumber}. Your application is under review.`,
      applicationId: applicationId,
      read: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    await batch.commit();

    return {
      success: true,
      receiptNumber: receiptNumber,
      paymentId: razorpayPaymentId,
      orderId: razorpayOrderId,
      applicationId: applicationId,
      amount: amountPaidInInr,
      status: "SUCCESS"
    };
  }
);

/**
 * 3. razorpayWebhook
 * HTTPS endpoint: Receives server-to-server webhook events directly from Razorpay.
 * Secrets: RAZORPAY_WEBHOOK_SECRET
 */
exports.razorpayWebhook = onRequest(
  {
    secrets: [razorpayWebhookSecret]
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).send("Method Not Allowed");
      return;
    }

    const webhookSignature = req.headers["x-razorpay-signature"];
    const webhookSecret = razorpayWebhookSecret.value();

    if (!webhookSignature || !webhookSecret) {
      console.error("Missing webhook signature or secret.");
      res.status(400).send("Signature or secret missing");
      return;
    }

    // Razorpay webhook signature verification using rawBody
    const expectedSignature = crypto
      .createHmac("sha256", webhookSecret)
      .update(req.rawBody || JSON.stringify(req.body))
      .digest("hex");

    const isSignatureValid = crypto.timingSafeEqual(
      Buffer.from(expectedSignature, "utf-8"),
      Buffer.from(webhookSignature, "utf-8")
    );

    if (!isSignatureValid) {
      console.error("Webhook signature mismatch.");
      res.status(400).send("Invalid signature");
      return;
    }

    const event = req.body;
    console.log(`Received Razorpay webhook event: ${event.event} [${event.id || ""}]`);

    try {
      // 1. Audit trail log
      if (event.id) {
        await db.collection("payment_webhooks").doc(event.id).set({
          eventId: event.id,
          event: event.event,
          payload: event.payload,
          receivedAt: admin.firestore.FieldValue.serverTimestamp()
        }, { merge: true });
      }

      // 2. Handle payment.captured
      if (event.event === "payment.captured") {
        const payment = event.payload.payment.entity;
        const paymentId = payment.id;
        const orderId = payment.order_id;
        const notes = payment.notes || {};
        const userId = notes.userId;
        const amountInInr = (payment.amount || 0) / 100;

        if (paymentId) {
          await db.collection("payments").doc(paymentId).set({
            paymentId: paymentId,
            orderId: orderId || "",
            userId: userId || "",
            amount: amountInInr,
            currency: payment.currency || "INR",
            status: "SUCCESS",
            razorpayPaymentId: paymentId,
            serviceName: notes.serviceName || "Document Assistance Service",
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          }, { merge: true });
        }
      } else if (event.event === "payment.failed") {
        const payment = event.payload.payment.entity;
        const paymentId = payment.id;
        const orderId = payment.order_id;

        if (paymentId) {
          await db.collection("payments").doc(paymentId).set({
            paymentId: paymentId,
            orderId: orderId || "",
            status: "FAILED",
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          }, { merge: true });
        }
      }

      res.status(200).json({ status: "ok" });
    } catch (err) {
      console.error("Error processing webhook:", err);
      res.status(500).json({ error: "Internal processing error" });
    }
  }
);
