package com.example.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppNotification
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

data class AdminUiState(
    val allApplications: List<ServiceApplication> = emptyList(),
    val allPayments: List<PaymentRecord> = emptyList(),
    val searchQuery: String = "",
    val filterServiceId: String? = null,
    val filterStatus: String? = null,
    val selectedApplication: ServiceApplication? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val customServiceFees: Map<String, Double> = emptyMap()
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TTOnlineRepository(application)
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadData() {
        val user = auth.currentUser ?: return
        val isOwner = user.email?.trim().equals(com.example.ui.auth.AuthViewModel.OWNER_EMAIL, ignoreCase = true)
        if (!isOwner) return

        viewModelScope.launch {
            repository.observeAllApplications()
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage) }
                .collect { apps ->
                    _uiState.value = _uiState.value.copy(allApplications = apps)
                }
        }

        viewModelScope.launch {
            repository.observeAllPayments()
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage) }
                .collect { payments ->
                    _uiState.value = _uiState.value.copy(allPayments = payments)
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setFilterService(serviceId: String?) {
        _uiState.value = _uiState.value.copy(filterServiceId = serviceId)
    }

    fun setFilterStatus(status: String?) {
        _uiState.value = _uiState.value.copy(filterStatus = status)
    }

    fun selectApplication(app: ServiceApplication?) {
        _uiState.value = _uiState.value.copy(selectedApplication = app)
    }

    fun updateApplicationStatus(
        applicationId: String,
        newStatus: String,
        adminNotes: String,
        additionalInfoRequest: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.adminUpdateStatus(
                    applicationId = applicationId,
                    newStatus = newStatus,
                    notes = adminNotes,
                    requestedInfo = additionalInfoRequest
                )

                // If additional info requested or status changed, notify customer
                val app = _uiState.value.allApplications.find { it.applicationId == applicationId }
                if (app != null) {
                    val notifTitle = if (additionalInfoRequest.isNotBlank()) {
                        "Action Required: Documents Needed for $applicationId"
                    } else {
                        "Status Update: Application $applicationId is $newStatus"
                    }

                    val notifMessage = if (additionalInfoRequest.isNotBlank()) {
                        "Operator notes: $additionalInfoRequest"
                    } else {
                        "Your application for ${app.serviceName} is now marked as $newStatus. Notes: $adminNotes"
                    }

                    val notif = AppNotification(
                        notificationId = "notif_${System.currentTimeMillis()}",
                        userId = app.userId,
                        title = notifTitle,
                        message = notifMessage,
                        applicationId = applicationId,
                        read = false
                    )
                    repository.sendNotification(notif)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedApplication = null,
                    successMessage = "Application #$applicationId updated to $newStatus."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to update status"
                )
            }
        }
    }

    fun updateServiceFee(serviceId: String, newFee: Double) {
        val current = _uiState.value.customServiceFees.toMutableMap()
        current[serviceId] = newFee
        _uiState.value = _uiState.value.copy(
            customServiceFees = current,
            successMessage = "Service fee updated to ₹${newFee.toInt()}."
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
