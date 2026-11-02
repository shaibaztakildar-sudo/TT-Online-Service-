package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AppNotification
import com.example.data.model.PaymentRecord
import com.example.data.model.ServiceApplication
import com.example.data.model.UserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TTOnlineRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth,
        FirebaseFunctions.getInstance()
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated before performing this action.")
    }

    // --- User Profile ---
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java)
                } else null
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                throw error
            }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val snapshot = docRef.get().await()
            val payload = if (snapshot.exists()) {
                mutableMapOf<String, Any>(
                    "id" to uid,
                    "email" to profile.email,
                    "name" to profile.name,
                    "phone" to profile.phone,
                    "role" to (snapshot.getString("role") ?: profile.role),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            } else {
                mutableMapOf<String, Any>(
                    "id" to uid,
                    "email" to profile.email,
                    "name" to profile.name,
                    "phone" to profile.phone,
                    "role" to profile.role,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            }
            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        val docRef = db.collection("users").document(userId)
        try {
            docRef.update(
                mapOf(
                    "role" to newRole,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Applications ---
    fun observeUserApplications(userId: String): Flow<List<ServiceApplication>> {
        val path = "applications"
        return db.collection(path)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ServiceApplication::class.java)
                    .sortedByDescending { it.createdAt }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    fun observeAllApplications(): Flow<List<ServiceApplication>> {
        val path = "applications"
        return db.collection(path)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ServiceApplication::class.java)
                    .sortedByDescending { it.createdAt }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun createApplication(app: ServiceApplication): String {
        val uid = requireUserId()
        val docRef = db.collection("applications").document(app.applicationId)
        try {
            val payload = mapOf(
                "applicationId" to app.applicationId,
                "userId" to uid,
                "customerName" to app.customerName,
                "customerEmail" to app.customerEmail,
                "customerPhone" to app.customerPhone,
                "serviceId" to app.serviceId,
                "serviceName" to app.serviceName,
                "applicationType" to app.applicationType,
                "formData" to app.formData,
                "status" to "Pending",
                "paymentStatus" to app.paymentStatus,
                "paymentId" to app.paymentId,
                "paymentOrderId" to app.paymentOrderId,
                "amountPaid" to app.amountPaid,
                "consentGiven" to true,
                "documentCount" to app.documentCount,
                "adminNotes" to "",
                "additionalInfoRequested" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            return app.applicationId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateApplication(app: ServiceApplication) {
        val uid = requireUserId()
        val docRef = db.collection("applications").document(app.applicationId)
        try {
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (app.paymentStatus.isNotBlank()) updates["paymentStatus"] = app.paymentStatus
            if (app.paymentId.isNotBlank()) updates["paymentId"] = app.paymentId
            if (app.status.isNotBlank()) updates["status"] = app.status
            if (app.adminNotes.isNotBlank()) updates["adminNotes"] = app.adminNotes
            if (app.additionalInfoRequested.isNotBlank()) updates["additionalInfoRequested"] = app.additionalInfoRequested
            if (app.formData.isNotBlank()) updates["formData"] = app.formData
            if (app.documentCount > 0) updates["documentCount"] = app.documentCount

            docRef.update(updates).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun adminUpdateStatus(
        applicationId: String,
        newStatus: String,
        notes: String,
        requestedInfo: String
    ) {
        val docRef = db.collection("applications").document(applicationId)
        try {
            val updates = mapOf(
                "status" to newStatus,
                "adminNotes" to notes,
                "additionalInfoRequested" to requestedInfo,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Payments ---
    suspend fun recordPayment(payment: PaymentRecord) {
        val uid = requireUserId()
        val docRef = db.collection("payments").document(payment.paymentId)
        try {
            val payload = mapOf(
                "paymentId" to payment.paymentId,
                "orderId" to payment.orderId,
                "applicationId" to payment.applicationId,
                "userId" to uid,
                "amount" to payment.amount,
                "currency" to "INR",
                "status" to payment.status,
                "razorpayPaymentId" to payment.razorpayPaymentId,
                "razorpaySignature" to payment.razorpaySignature,
                "receiptNumber" to payment.receiptNumber,
                "serviceName" to payment.serviceName,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeUserPayments(userId: String): Flow<List<PaymentRecord>> {
        val path = "payments"
        return db.collection(path)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(PaymentRecord::class.java)
                    .sortedByDescending { it.createdAt }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    fun observeAllPayments(): Flow<List<PaymentRecord>> {
        val path = "payments"
        return db.collection(path)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(PaymentRecord::class.java)
                    .sortedByDescending { it.createdAt }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    // --- Notifications ---
    fun observeUserNotifications(userId: String): Flow<List<AppNotification>> {
        val path = "notifications"
        return db.collection(path)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(AppNotification::class.java)
                    .sortedByDescending { it.createdAt }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun sendNotification(notification: AppNotification) {
        val docRef = db.collection("notifications").document(notification.notificationId)
        try {
            val payload = mapOf(
                "notificationId" to notification.notificationId,
                "userId" to notification.userId,
                "title" to notification.title,
                "message" to notification.message,
                "applicationId" to notification.applicationId,
                "read" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun markNotificationRead(notificationId: String) {
        val docRef = db.collection("notifications").document(notificationId)
        try {
            docRef.update("read", true).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Razorpay Cloud Functions Integration ---
    suspend fun createServerRazorpayOrder(
        serviceId: String,
        applicationType: String,
        applicantName: String
    ): Map<String, Any?> {
        val payload = hashMapOf(
            "serviceId" to serviceId,
            "applicationType" to applicationType,
            "applicantName" to applicantName
        )
        val result = functions
            .getHttpsCallable("createRazorpayOrder")
            .call(payload)
            .await()
        @Suppress("UNCHECKED_CAST")
        return result.data as? Map<String, Any?> ?: emptyMap()
    }

    suspend fun verifyServerRazorpayPayment(
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String,
        applicationId: String,
        serviceId: String,
        applicantName: String,
        applicantPhone: String,
        applicantEmail: String,
        applicantAddress: String,
        idNumber: String,
        attachedCount: Int,
        applicationType: String
    ): Map<String, Any?> {
        val payload = hashMapOf(
            "razorpayOrderId" to razorpayOrderId,
            "razorpayPaymentId" to razorpayPaymentId,
            "razorpaySignature" to razorpaySignature,
            "applicationId" to applicationId,
            "serviceId" to serviceId,
            "applicantName" to applicantName,
            "applicantPhone" to applicantPhone,
            "applicantEmail" to applicantEmail,
            "applicantAddress" to applicantAddress,
            "idNumber" to idNumber,
            "attachedCount" to attachedCount,
            "applicationType" to applicationType
        )
        val result = functions
            .getHttpsCallable("verifyRazorpayPayment")
            .call(payload)
            .await()
        @Suppress("UNCHECKED_CAST")
        return result.data as? Map<String, Any?> ?: emptyMap()
    }
}
