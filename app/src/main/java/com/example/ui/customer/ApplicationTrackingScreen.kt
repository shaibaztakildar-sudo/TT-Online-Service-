package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentRecord
import com.example.data.model.ServiceApplication
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue500
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red100
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun ApplicationTrackingScreen(
    applications: List<ServiceApplication>,
    payments: List<PaymentRecord>,
    onViewReceipt: (PaymentRecord) -> Unit,
    onSubmitAdditionalDoc: (applicationId: String, docName: String) -> Unit,
    onNavigateToServices: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedApp by remember { mutableStateOf<ServiceApplication?>(null) }

    if (applications.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Blue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Navy800,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Applications Yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You haven't submitted any document assistance applications yet. Choose a service to get started.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToServices,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                    ) {
                        Text("Browse Services", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Applications (${applications.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "Live Tracking",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green600,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(applications) { app ->
                ApplicationTrackingCard(
                    app = app,
                    onClick = { selectedApp = app }
                )
            }
        }
    }

    selectedApp?.let { app ->
        ApplicationDetailDialog(
            app = app,
            payment = payments.find { it.applicationId == app.applicationId || it.paymentId == app.paymentId },
            onDismiss = { selectedApp = null },
            onViewReceipt = { p ->
                selectedApp = null
                onViewReceipt(p)
            },
            onSubmitAdditionalDoc = { docName ->
                onSubmitAdditionalDoc(app.applicationId, docName)
                selectedApp = null
            }
        )
    }
}

@Composable
fun ApplicationTrackingCard(
    app: ServiceApplication,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_tracking_card_${app.applicationId}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.applicationId,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Navy800
                )
                StatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.serviceName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Navy900
            )
            Text(
                text = "Type: ${app.applicationType} • Applicant: ${app.customerName}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Slate200)
            Spacer(modifier = Modifier.height(12.dp))

            // Progress Timeline Mini Steps
            TrackingTimelineMini(status = app.status)

            if (app.status == "Additional Information Required") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Amber100
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Amber600, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Action Needed: Tap to submit requested documents",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Navy900
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Completed" -> Pair(Green100, Green600)
        "Processing", "Under Review" -> Pair(Blue100, Navy800)
        "Rejected" -> Pair(Red100, Red600)
        "Additional Information Required" -> Pair(Amber100, Amber600)
        else -> Pair(Slate100, Slate700)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TrackingTimelineMini(status: String) {
    val steps = listOf("Submitted", "Under Review", "Processing", "Completed")
    val currentIndex = when (status) {
        "Pending" -> 0
        "Under Review" -> 1
        "Processing" -> 2
        "Completed" -> 3
        "Rejected" -> 1
        "Additional Information Required" -> 1
        else -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, stepName ->
            val isDone = index <= currentIndex
            val isCurrent = index == currentIndex
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isDone) Green600 else Slate200),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stepName,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone) Navy900 else Slate400
                )
            }
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (index < currentIndex) Green600 else Slate200)
                )
            }
        }
    }
}

@Composable
fun ApplicationDetailDialog(
    app: ServiceApplication,
    payment: PaymentRecord?,
    onDismiss: () -> Unit,
    onViewReceipt: (PaymentRecord) -> Unit,
    onSubmitAdditionalDoc: (docName: String) -> Unit
) {
    var newDocName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.applicationId,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Navy900
                            )
                            Text(
                                text = app.serviceName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current Status:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        StatusBadge(status = app.status)
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate50),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Applicant:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Text(app.customerName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Phone:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Text(app.customerPhone, style = MaterialTheme.typography.bodySmall, color = Slate800)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Payment Status:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Text(app.paymentStatus, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Green600)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount Paid:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Text("₹${app.amountPaid.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Attached Documents:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Text("${app.documentCount} files verified", style = MaterialTheme.typography.bodySmall, color = Navy900)
                            }
                        }
                    }
                }

                // Operator Notes
                if (app.adminNotes.isNotBlank()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Blue50),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Operator Processing Note",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Navy900
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = app.adminNotes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate800
                                )
                            }
                        }
                    }
                }

                // If Additional Info Requested
                if (app.additionalInfoRequested.isNotBlank() || app.status == "Additional Information Required") {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Amber100),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Amber600, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Additional Information Requested",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Navy900
                                    )
                                }
                                if (app.additionalInfoRequested.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = app.additionalInfoRequested,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate800
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = newDocName,
                                    onValueChange = { newDocName = it },
                                    label = { Text("Document name / File description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (newDocName.isNotBlank()) {
                                            isUploading = true
                                            onSubmitAdditionalDoc(newDocName)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = newDocName.isNotBlank() && !isUploading
                                ) {
                                    if (isUploading) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                    } else {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Submit Additional Document", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Receipt Action
                if (payment != null) {
                    item {
                        Button(
                            onClick = { onViewReceipt(payment) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Green600),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Official Payment Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
