package com.example.ui.web

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue100
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

@Composable
fun ContactScreen(
    userEmail: String,
    modifier: Modifier = Modifier
) {
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "Contact Us",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We are here to assist with any questions regarding your application",
                        style = MaterialTheme.typography.bodySmall,
                        color = Amber500
                    )
                }
            }
        }

        // Contact Info Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Helpline & Center Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)

                    ContactRow(
                        icon = Icons.Default.Phone,
                        title = "Toll-Free Helpline",
                        value = "+91 1800 212 9988 / +91 98200 11223"
                    )

                    ContactRow(
                        icon = Icons.Default.Email,
                        title = "Official Support Email",
                        value = "support@ttservices.in"
                    )

                    ContactRow(
                        icon = Icons.Default.Schedule,
                        title = "Operating Hours",
                        value = "Monday – Saturday: 9:00 AM – 7:00 PM IST (Closed Sundays)"
                    )

                    ContactRow(
                        icon = Icons.Default.LocationOn,
                        title = "Central Facilitation Office",
                        value = "TT Online Services Hub, Digital Seva Bhavan, India"
                    )
                }
            }
        }

        // Contact Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Send Us a Direct Message",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "Have a question about document requirements or tracking? Send a message and our team will reply within 2 business hours.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (submitted) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Green100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Thank you! Your message has been received.",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Green600
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Our support officer will contact you at $userEmail promptly.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Green600
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = senderName,
                            onValueChange = { senderName = it },
                            label = { Text("Your Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = senderPhone,
                            onValueChange = { senderPhone = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject (e.g., PAN Card Inquiry)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Your Message or Application ID") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (senderName.isNotBlank() && message.isNotBlank()) {
                                    submitted = true
                                }
                            },
                            enabled = senderName.isNotBlank() && message.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Message", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Blue100),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Navy800, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Slate600)
            Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Navy900)
        }
    }
}
