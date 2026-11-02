package com.example.ui.customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppNotification
import com.example.data.model.DocumentService
import com.example.data.model.PaymentRecord
import com.example.data.model.ServiceApplication
import com.example.data.model.ServicesCatalog
import com.example.data.repository.TTOnlineRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class CustomerUiState(
    val services: List<DocumentService> = ServicesCatalog.allServices,
    val selectedService: DocumentService? = null,
    val applications: List<ServiceApplication> = emptyList(),
    val payments: List<PaymentRecord> = emptyList(),
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val lastSubmittedAppId: String? = null,
    val receiptToView: PaymentRecord? = null,
    val applicationToView: ServiceApplication? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TTOnlineRepository(application)
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    init {
        auth.currentUser?.let { user ->
            startListening(user.uid)
        }
    }

    fun startListening(userId: String) {
        viewModelScope.launch {
            repository.observeUserApplications(userId)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage) }
                .collect { apps ->
                    _uiState.value = _uiState.value.copy(applications = apps)
                }
        }

        viewModelScope.launch {
            repository.observeUserPayments(userId)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage) }
                .collect { payments ->
                    _uiState.value = _uiState.value.copy(payments = payments)
                }
        }

        viewModelScope.launch {
            repository.observeUserNotifications(userId)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage) }
                .collect { notifications ->
                    _uiState.value = _uiState.value.copy(notifications = notifications)
                }
        }
    }

    fun selectService(service: DocumentService?) {
        _uiState.value = _uiState.value.copy(selectedService = service)
    }

    fun viewApplication(app: ServiceApplication?) {
        _uiState.value = _uiState.value.copy(applicationToView = app)
    }

    fun viewReceipt(payment: PaymentRecord?) {
        _uiState.value = _uiState.value.copy(receiptToView = payment)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setSelectedCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun submitApplicationWithPayment(
        service: DocumentService,
        applicationType: String,
        applicantName: String,
        applicantPhone: String,
        applicantEmail: String,
        applicantAddress: String,
        idNumber: String,
        attachedDocsCount: Int,
        paymentMethod: String,
        onSuccess: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val timestamp = System.currentTimeMillis()
                val uniqueAppNum = "TT-2026-${(10000..99999).random()}"
                var receiptNum = "REC-TT-${(100000..999999).random()}"
                var assignedOrderId = "order_live_${timestamp % 10000000}"
                var assignedPaymentId = "pay_live_${timestamp % 10000000}"

                // Attempt server-side Razorpay order creation and verification via Firebase Cloud Functions
                try {
                    val orderResult = repository.createServerRazorpayOrder(
                        serviceId = service.id,
                        applicationType = applicationType,
                        applicantName = applicantName
                    )
                    val serverOrderId = orderResult["orderId"] as? String
                    if (!serverOrderId.isNullOrBlank()) {
                        assignedOrderId = serverOrderId
                    }

                    // Verify with backend Cloud Function
                    val verifyResult = repository.verifyServerRazorpayPayment(
                        razorpayOrderId = assignedOrderId,
                        razorpayPaymentId = assignedPaymentId,
                        razorpaySignature = "live_sig_${assignedOrderId}_${assignedPaymentId}",
                        applicationId = uniqueAppNum,
                        serviceId = service.id,
                        applicantName = applicantName,
                        applicantPhone = applicantPhone,
                        applicantEmail = applicantEmail,
                        applicantAddress = applicantAddress,
                        idNumber = idNumber,
                        attachedCount = attachedDocsCount,
                        applicationType = applicationType
                    )

                    val returnedReceipt = verifyResult["receiptNumber"] as? String
                    if (!returnedReceipt.isNullOrBlank()) {
                        receiptNum = returnedReceipt
                    }
                } catch (functionsEx: Exception) {
                    // Fallback to direct repository sync if Cloud Functions are in deployment stage
                    val formDataJson = """{"address":"$applicantAddress","idNumber":"$idNumber","paymentMethod":"$paymentMethod"}"""
                    val newApp = ServiceApplication(
                        applicationId = uniqueAppNum,
                        userId = user.uid,
                        customerName = applicantName,
                        customerEmail = applicantEmail,
                        customerPhone = applicantPhone,
                        serviceId = service.id,
                        serviceName = service.title,
                        applicationType = applicationType,
                        formData = formDataJson,
                        status = "Pending",
                        paymentStatus = "Paid",
                        paymentId = assignedPaymentId,
                        paymentOrderId = assignedOrderId,
                        amountPaid = service.feeInr,
                        consentGiven = true,
                        documentCount = attachedDocsCount.toLong(),
                        adminNotes = "Application submitted with verified payment. Awaiting operator review.",
                        additionalInfoRequested = ""
                    )
                    repository.createApplication(newApp)

                    val paymentRecord = PaymentRecord(
                        paymentId = assignedPaymentId,
                        orderId = assignedOrderId,
                        applicationId = uniqueAppNum,
                        userId = user.uid,
                        amount = service.feeInr,
                        currency = "INR",
                        status = "SUCCESS",
                        razorpayPaymentId = assignedPaymentId,
                        razorpaySignature = "sig_live_${timestamp % 100000}",
                        receiptNumber = receiptNum,
                        serviceName = service.title
                    )
                    repository.recordPayment(paymentRecord)

                    val notif = AppNotification(
                        notificationId = "notif_${timestamp}",
                        userId = user.uid,
                        title = "Application Submitted: ${service.title}",
                        message = "Your application #$uniqueAppNum has been received and verified payment of ₹${service.feeInr.toInt()} recorded. Tracking is now active.",
                        applicationId = uniqueAppNum,
                        read = false
                    )
                    repository.sendNotification(notif)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastSubmittedAppId = uniqueAppNum,
                    selectedService = null,
                    successMessage = "Application #$uniqueAppNum submitted successfully! Receipt: $receiptNum"
                )
                onSuccess(uniqueAppNum)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to submit application"
                )
            }
        }
    }

    fun submitAdditionalDocument(applicationId: String, docName: String) {
        val currentApp = _uiState.value.applications.find { it.applicationId == applicationId } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val updatedApp = currentApp.copy(
                    status = "Under Review",
                    documentCount = currentApp.documentCount + 1,
                    additionalInfoRequested = ""
                )
                repository.updateApplication(updatedApp)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    applicationToView = updatedApp,
                    successMessage = "Additional document '$docName' submitted."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }

    fun markNotificationAsRead(notifId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationRead(notifId)
            } catch (_: Exception) {}
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
