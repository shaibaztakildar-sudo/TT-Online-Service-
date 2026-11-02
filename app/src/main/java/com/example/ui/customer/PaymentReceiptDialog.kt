package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentRecord
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue50
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun PaymentsScreen(
    payments: List<PaymentRecord>,
    onViewReceipt: (PaymentRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    if (payments.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Payment Transactions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When you submit an application, verified Razorpay payment receipts will be accessible here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment History (${payments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "Razorpay Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green600,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(payments) { payment ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(payment) }
                        .testTag("payment_item_${payment.paymentId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = payment.receiptNumber,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Navy800
                            )
                            Text(
                                text = payment.serviceName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Navy900,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "App ID: ${payment.applicationId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${payment.amount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Navy900
                            )
                            Surface(
                                color = Green100,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "PAID",
                                    color = Green600,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentReceiptDialog(
    payment: PaymentRecord,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Receipt Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "TT ONLINE SERVICES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = Navy900
                        )
                        Text(
                            text = "Official Payment & Tax Invoice Receipt",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                        Text(
                            text = "GSTIN: 27AABCT9982Q1Z3 (Facilitation)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Meta Box
                Surface(
                    color = Blue50,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Receipt No:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                            Text(payment.receiptNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy800)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Payment Gateway:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                            Text("Razorpay Verified", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Green600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReceiptRow(label = "Application Number", value = payment.applicationId)
                    ReceiptRow(label = "Service Rendered", value = payment.serviceName)
                    ReceiptRow(label = "Razorpay Payment ID", value = payment.razorpayPaymentId)
                    ReceiptRow(label = "Currency", value = "INR (₹)")
                    ReceiptRow(label = "Transaction Status", value = "SUCCESS / VERIFIED", isHighlight = true)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(14.dp))

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount Paid",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "₹${payment.amount.toInt()}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Navy800
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stamp Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Green600, RoundedCornerShape(8.dp))
                        .background(Green100)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ELECTRONICALLY GENERATED & DIGITALLY VERIFIED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Green600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Slate600)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = if (isHighlight) Green600 else Navy900
        )
    }
}
