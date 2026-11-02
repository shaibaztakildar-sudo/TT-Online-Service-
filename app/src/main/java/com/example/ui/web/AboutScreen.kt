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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "About TT Online Services",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Simplifying Indian Citizen Document Facilitation Since 2024",
                        style = MaterialTheme.typography.titleSmall,
                        color = Amber500,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TT Online Services operates as a dedicated citizen document assistance facilitation center. We eliminate confusion and bureaucratic delays by helping applicants verify their documentation, avoid common application rejections, and submit their forms accurately onto the authorized government portals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Mission & Vision
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Our Mission & Standards", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Accurate Verification First", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                            Text("Every application is manually reviewed by trained operators to verify name spellings, address proof matches, and date of birth consistency before filing.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Zero-Trust Privacy & Security", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                            Text("We will never request your Aadhaar OTP, bank OTP, debit card PIN, or internet banking credentials. Your documents are stored under encrypted, authenticated access.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Fixed Indian Rupee (₹) Pricing", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                            Text("No hidden fees. Every service has a transparent facilitation charge, and official GST-compliant payment receipts are generated automatically.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }
                }
            }
        }

        // Statutory Non-Government Disclaimer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Important Legal Notice & Consumer Disclosure", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TT Online Services is an independent citizen facilitation platform operated for the convenience of the public. We are not a government website, nor do we claim to be affiliated with any government department or ministry. All government documents (such as PAN, Voter ID, Driving Licence, Passports, and State Certificates) are processed, verified, and issued solely by the respective authorized government bodies and their official portals (e.g., NSDL, UTIITSL, UIDAI, ECI, Sarathi Parivahan, and State e-District). Our facilitation fee is charged solely for document review, assistance, and filing support.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                }
            }
        }

        // Footer
        item {
            Text(
                text = "TT Online Services • Certified Digital Assistance Center • Support: support@ttservices.in",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
