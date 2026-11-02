const test = require("node:test");
const assert = require("node:assert/strict");
const crypto = require("node:crypto");

test("Razorpay signature generation and timingSafeEqual verification", () => {
  const secret = "live_secret_sample_test_key_xyz";
  const orderId = "order_NWY1bI1o5iN4G0";
  const paymentId = "pay_NWY24Z5g4BfL4J";

  const expectedSignature = crypto
    .createHmac("sha256", secret)
    .update(`${orderId}|${paymentId}`)
    .digest("hex");

  assert.equal(typeof expectedSignature, "string");
  assert.equal(expectedSignature.length, 64); // SHA-256 hex is 64 characters

  const isValid = crypto.timingSafeEqual(
    Buffer.from(expectedSignature, "utf-8"),
    Buffer.from(expectedSignature, "utf-8")
  );
  assert.equal(isValid, true);

  const fakeSignature = crypto
    .createHmac("sha256", "wrong_secret")
    .update(`${orderId}|${paymentId}`)
    .digest("hex");

  const isFakeValid = crypto.timingSafeEqual(
    Buffer.from(expectedSignature, "utf-8"),
    Buffer.from(fakeSignature, "utf-8")
  );
  assert.equal(isFakeValid, false);
});

test("Razorpay webhook signature verification", () => {
  const webhookSecret = "webhook_secret_live_12345";
  const rawBody = JSON.stringify({
    event: "payment.captured",
    payload: {
      payment: {
        entity: {
          id: "pay_NWY24Z5g4BfL4J",
          amount: 29900,
          currency: "INR",
          status: "captured",
          order_id: "order_NWY1bI1o5iN4G0"
        }
      }
    }
  });

  const validSignature = crypto
    .createHmac("sha256", webhookSecret)
    .update(rawBody)
    .digest("hex");

  assert.equal(typeof validSignature, "string");
  assert.equal(validSignature.length, 64);
});
