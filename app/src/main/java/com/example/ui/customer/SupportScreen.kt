package com.example.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

data class FaqItem(val question: String, val answer: String)

@Composable
fun SupportScreen(
    userEmail: String,
    modifier: Modifier = Modifier
) {
    var querySubject by remember { mutableStateOf("") }
    var queryMessage by remember { mutableStateOf("") }
    var querySubmitted by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            FaqItem(
                "Is TT Online Services a government portal?",
                "No. TT Online Services is an authorized private citizen assistance facilitation center. We assist applicants with accurate document verification, form filling, and submission onto the respective official government portals (such as NSDL, UTIITSL, UIDAI, ECI, Sarathi Parivahan, and State e-District)."
            ),
            FaqItem(
                "How do I track my submitted application?",
                "Once you submit and pay the facilitation fee, an application number starting with 'TT-2026-' is generated. You can view the live progress anytime under the 'My Applications' tab."
            ),
            FaqItem(
                "What if an operator asks for additional documents?",
                "If the official department requires clearer identity proof or an affidavit, our operator will update your status to 'Additional Information Required'. You can view the request and upload the required document directly from the tracking details."
            ),
            FaqItem(
                "Do you collect Aadhaar OTP or bank PINs?",
                "Never. We adhere strictly to zero-trust privacy standards. We will NEVER request Aadhaar OTP, bank OTP, debit card PIN, or internet banking passwords."
            ),
            FaqItem(
                "How are payments processed?",
                "All payments are processed securely in Indian Rupees (₹) via verified Razorpay checkout. An official digital receipt is immediately issued for every transaction."
            )
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Support Center Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Amber500, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Citizen Help & Support Desk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Operating Hours: Monday – Saturday, 9:00 AM – 7:00 PM IST",
                                style = MaterialTheme.typography.labelSmall,
                                color = Amber500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Have questions regarding document eligibility or need urgent assistance with your application? Reach out to our dedicated support operators.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+91 1800 212 9988", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("support@ttservices.in", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // FAQs Header
        item {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Navy900
            )
        }

        items(faqs) { faq ->
            FaqCard(faq = faq)
        }

        // Raise Query Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Raise a Support Query",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "Submit your question or report an issue directly to the operator desk.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (querySubmitted) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Green100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Green600)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Your query has been logged. An operator will respond via email to $userEmail within 2 business hours.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Green600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = querySubject,
                            onValueChange = { querySubject = it },
                            label = { Text("Subject (e.g. Question on PAN Application)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = queryMessage,
                            onValueChange = { queryMessage = it },
                            label = { Text("Describe your query or application ID") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (querySubject.isNotBlank() && queryMessage.isNotBlank()) {
                                    querySubmitted = true
                                }
                            },
                            enabled = querySubject.isNotBlank() && queryMessage.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Query", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FaqCard(faq: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Navy900,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate600
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
            }
        }
    }
}
