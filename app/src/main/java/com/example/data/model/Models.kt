package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "customer", // "customer" or "admin"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ServiceApplication(
    val applicationId: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val applicationType: String = "New Application",
    val formData: String = "{}",
    val status: String = "Pending", // "Pending", "Under Review", "Processing", "Completed", "Rejected", "Additional Information Required"
    val paymentStatus: String = "Pending", // "Pending", "Paid", "Failed", "Refunded"
    val paymentId: String = "",
    val paymentOrderId: String = "",
    val amountPaid: Double = 0.0,
    val consentGiven: Boolean = true,
    val documentCount: Long = 0L,
    val adminNotes: String = "",
    val additionalInfoRequested: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PaymentRecord(
    val paymentId: String = "",
    val orderId: String = "",
    val applicationId: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val currency: String = "INR",
    val status: String = "SUCCESS", // "SUCCESS", "PENDING", "FAILED"
    val razorpayPaymentId: String = "",
    val razorpaySignature: String = "",
    val receiptNumber: String = "",
    val serviceName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val applicationId: String = "",
    val read: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class DocumentService(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val feeInr: Double,
    val turnaroundTime: String,
    val officialPortal: String,
    val description: String,
    val instructions: List<String>,
    val checklist: List<String>,
    val applicationTypes: List<String>
)
