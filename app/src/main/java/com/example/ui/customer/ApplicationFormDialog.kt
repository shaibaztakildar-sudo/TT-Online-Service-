package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DocumentService
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue500
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun ApplicationFormDialog(
    service: DocumentService,
    initialName: String,
    initialEmail: String,
    initialPhone: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        appType: String,
        name: String,
        phone: String,
        email: String,
        address: String,
        idNumber: String,
        attachedCount: Int,
        paymentMethod: String
    ) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Details, 2: Documents, 3: Consent & Payment
    var selectedType by remember { mutableStateOf(service.applicationTypes.firstOrNull() ?: "Standard Assistance") }
    var applicantName by remember { mutableStateOf(initialName) }
    var applicantPhone by remember { mutableStateOf(initialPhone) }
    var applicantEmail by remember { mutableStateOf(initialEmail) }
    var applicantAddress by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }

    val attachedDocs = remember { mutableStateListOf<String>() }
    var consentGiven by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("UPI (GPay/PhonePe/Paytm)") }
    var formError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Navy900
                        )
                        Text(
                            text = "Step $step of 3: " + when (step) {
                                1 -> "Applicant Information"
                                2 -> "Document Checklist"
                                else -> "Consent & Payment"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!isLoading) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Step Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (index + 1 <= step) Navy800 else Slate200)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (step) {
                        1 -> {
                            // Step 1: Details
                            item {
                                Text(
                                    text = "Select Application Type",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Navy900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(service.applicationTypes) { type ->
                                        val isSelected = selectedType == type
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) Navy800 else Slate100,
                                            modifier = Modifier.clickable { selectedType = type }
                                        ) {
                                            Text(
                                                text = type,
                                                color = if (isSelected) Color.White else Slate800,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = applicantName,
                                    onValueChange = { applicantName = it },
                                    label = { Text("Applicant Full Name (As per records)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("applicant_name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = applicantPhone,
                                    onValueChange = { applicantPhone = it },
                                    label = { Text("Mobile Number (For SMS updates)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("applicant_phone_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = applicantEmail,
                                    onValueChange = { applicantEmail = it },
                                    label = { Text("Email Address") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = applicantAddress,
                                    onValueChange = { applicantAddress = it },
                                    label = { Text("Complete Residential Address with PIN Code") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = idNumber,
                                    onValueChange = { idNumber = it },
                                    label = { Text("Identity / Reference Number (Aadhaar / Voter / PAN if applicable)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        2 -> {
                            // Step 2: Document Checklist & Attachments
                            item {
                                Text(
                                    text = "Mandatory Supporting Documents",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Navy900
                                )
                                Text(
                                    text = "Please attach or confirm the following documents for our operator to prepare your official filing.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }

                            items(service.checklist) { doc ->
                                val isAttached = attachedDocs.contains(doc)
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isAttached) Green100 else Slate50),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isAttached) Green600 else Slate200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isAttached) Icons.Default.CheckCircle else Icons.Default.AttachFile,
                                                contentDescription = null,
                                                tint = if (isAttached) Green600 else Slate600,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = doc,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (isAttached) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isAttached) Green600 else Slate800
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                if (isAttached) attachedDocs.remove(doc)
                                                else attachedDocs.add(doc)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isAttached) Slate200 else Navy800
                                            ),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isAttached) "Attached" else "Attach",
                                                color = if (isAttached) Slate800 else Color.White,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Blue50,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = Blue500, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Attached documents: ${attachedDocs.size} / ${service.checklist.size}. All files are handled under zero-leak data protection.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Navy900
                                        )
                                    }
                                }
                            }
                        }

                        3 -> {
                            // Step 3: Consent & Real Razorpay Checkout Summary
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Slate50),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Application Fee Summary",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Navy900
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Service Facilitation Fee", style = MaterialTheme.typography.bodySmall, color = Slate700)
                                            Text("₹${service.feeInr.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Document Verification & Review", style = MaterialTheme.typography.bodySmall, color = Slate700)
                                            Text("Included", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Green600)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("GST / Tax", style = MaterialTheme.typography.bodySmall, color = Slate700)
                                            Text("₹0.00", style = MaterialTheme.typography.bodySmall, color = Slate700)
                                        }
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Total Amount Due", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)
                                            Text("₹${service.feeInr.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Navy800)
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "Select Payment Option (Razorpay INR)",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Navy900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val options = listOf("UPI (GPay/PhonePe/Paytm)", "Debit / Credit Card", "Net Banking (All Indian Banks)")
                                options.forEach { option ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { paymentMethod = option }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = paymentMethod == option,
                                            onClick = { paymentMethod = option },
                                            colors = RadioButtonDefaults.colors(selectedColor = Navy800)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = option, style = MaterialTheme.typography.bodyMedium, color = Slate800)
                                    }
                                }
                            }

                            item {
                                // Consent Checkbox
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { consentGiven = !consentGiven }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Checkbox(
                                        checked = consentGiven,
                                        onCheckedChange = { consentGiven = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Navy800),
                                        modifier = Modifier.testTag("consent_checkbox")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "I hereby declare that all information provided is accurate and true. I authorize TT Online Services operators to prepare and process my application on the authorized government portal.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }
                }

                if (formError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (step > 1) {
                        Button(
                            onClick = { step-- },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Slate200),
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Back", color = Navy900, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            formError = null
                            when (step) {
                                1 -> {
                                    if (applicantName.isBlank()) {
                                        formError = "Please enter applicant name."
                                    } else if (applicantPhone.isBlank()) {
                                        formError = "Please enter mobile number."
                                    } else if (applicantAddress.isBlank()) {
                                        formError = "Please enter residential address."
                                    } else {
                                        step = 2
                                    }
                                }
                                2 -> {
                                    if (attachedDocs.isEmpty()) {
                                        formError = "Please attach at least one supporting document."
                                    } else {
                                        step = 3
                                    }
                                }
                                3 -> {
                                    if (!consentGiven) {
                                        formError = "You must give consent to proceed."
                                    } else {
                                        onSubmit(
                                            selectedType,
                                            applicantName,
                                            applicantPhone,
                                            applicantEmail,
                                            applicantAddress,
                                            idNumber,
                                            attachedDocs.size,
                                            paymentMethod
                                        )
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("form_submit_button"),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = when (step) {
                                    1 -> "Next: Checklist"
                                    2 -> "Next: Payment"
                                    else -> "Pay ₹${service.feeInr.toInt()} & Submit"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
