package com.example.ui.web

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentService
import com.example.data.model.ServicesCatalog
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
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun HomeScreen(
    onNavigateToServices: () -> Unit,
    onNavigateToApply: (DocumentService) -> Unit,
    onNavigateToTrack: () -> Unit,
    onNavigateToContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Section
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Surface(
                        color = Blue500.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Blue500.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Amber500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Authorized Citizen Document Assistance Center",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Amber500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TT ONLINE SERVICES",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fast, Accurate & Professional Assistance for Indian Government Document Applications",
                        style = MaterialTheme.typography.titleMedium,
                        color = Amber500,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "We guide and assist citizens in applying for PAN Cards, Voter IDs, UIDAI Aadhaar booking, Driving Licences, Passports, and State Certificates. Fixed transparent pricing in Indian Rupees (₹).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // CTA Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToServices,
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Explore Services", color = Navy900, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToTrack,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Track Status", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Trust Pillars
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrustBadge(
                    icon = Icons.Default.Lock,
                    title = "Zero-OTP Risk",
                    subtitle = "No Aadhaar OTP or bank PIN collected",
                    modifier = Modifier.weight(1f)
                )
                TrustBadge(
                    icon = Icons.Default.Payment,
                    title = "Razorpay INR",
                    subtitle = "Official tax receipts issued in ₹",
                    modifier = Modifier.weight(1f)
                )
                TrustBadge(
                    icon = Icons.Default.Security,
                    title = "Data Privacy",
                    subtitle = "Zero-leak document locker",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // How It Works Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "How TT Online Services Works",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = "Simple 4-step facilitation procedure for hassle-free filing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StepItem(step = "1", title = "Select Document Service", desc = "Choose from 9 official government assistance services and review required documents.")
                    StepDivider()
                    StepItem(step = "2", title = "Fill Details & Attach Documents", desc = "Provide accurate applicant details and attach necessary supporting documents securely.")
                    StepDivider()
                    StepItem(step = "3", title = "Pay Facilitation Fee via Razorpay", desc = "Transparent fixed fee in ₹ with instant digital tax-invoice receipt.")
                    StepDivider()
                    StepItem(step = "4", title = "Operator Review & Portal Filing", desc = "Our operators verify and process your application on authorized government portals with live tracking.")
                }
            }
        }

        // Featured Services Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Citizen Services",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Navy900
                )
                Text(
                    text = "View All (9)",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Blue500,
                    modifier = Modifier.clickable { onNavigateToServices() }
                )
            }
        }

        items(ServicesCatalog.allServices.take(4)) { service ->
            HomeServiceCard(
                service = service,
                onApply = { onNavigateToApply(service) }
            )
        }

        // Government Portal Disclaimer Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statutory Consumer Information & Disclaimer",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Navy900
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "TT Online Services is an independent citizen facilitation enterprise. We are not affiliated with any government ministry or department. Government document issuance and approval remain subject to official verification by authorized portal authorities (such as NSDL, UTIITSL, UIDAI, ECI, and Parivahan).",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate700
                    )
                }
            }
        }

        // Website Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TT ONLINE SERVICES — Digital Seva Portal",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Navy900
                )
                Text(
                    text = "Toll Free Helpline: +91 1800 212 9988 | support@ttservices.in",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "© 2026 TT Online Services. All rights reserved. Indian Rupee (₹) Pricing.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
fun TrustBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Blue50),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Navy900,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Slate600,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StepItem(step: String, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Navy800),
            contentAlignment = Alignment.Center
        ) {
            Text(step, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Slate600)
        }
    }
}

@Composable
fun StepDivider() {
    Box(
        modifier = Modifier
            .padding(start = 13.dp)
            .width(2.dp)
            .height(16.dp)
            .background(Slate200)
    )
}

@Composable
fun HomeServiceCard(
    service: DocumentService,
    onApply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                Text(service.subtitle, style = MaterialTheme.typography.labelSmall, color = Slate600)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${service.feeInr.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = Navy800)
            }

            Button(
                onClick = onApply,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Apply", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}
