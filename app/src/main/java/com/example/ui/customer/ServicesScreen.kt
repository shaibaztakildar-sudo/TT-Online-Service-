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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.DocumentService
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue500
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
fun ServicesScreen(
    services: List<DocumentService>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    onApplyClicked: (DocumentService) -> Unit,
    modifier: Modifier = Modifier
) {
    var detailService by remember { mutableStateOf<DocumentService?>(null) }

    val categories = remember(services) {
        listOf("All") + services.map { it.category }.distinct()
    }

    val filteredServices = remember(services, searchQuery, selectedCategory) {
        services.filter { service ->
            val matchesSearch = service.title.contains(searchQuery, ignoreCase = true) ||
                    service.subtitle.contains(searchQuery, ignoreCase = true) ||
                    service.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || selectedCategory == "All" || service.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Blue500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Citizen Document Assistance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Authorized Operator Facilitation on Official Portals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Amber500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Fast, transparent, and accurate application filing for all your essential identification and government certificates. Fixed service fees in Indian Rupees (₹).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("services_search_input"),
                placeholder = { Text("Search services (e.g. PAN, Voter, Passport...)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Slate600
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Navy800,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = (selectedCategory == null && cat == "All") || selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(if (cat == "All") null else cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Navy800,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Slate800
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Navy800 else Slate200
                        )
                    )
                }
            }
        }

        // Services Count
        item {
            Text(
                text = "Available Services (${filteredServices.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Navy900
            )
        }

        // Service Cards List
        items(filteredServices) { service ->
            ServiceCard(
                service = service,
                onViewDetails = { detailService = service },
                onApply = { onApplyClicked(service) }
            )
        }
    }

    // Detail & Instructions Dialog
    detailService?.let { service ->
        ServiceDetailDialog(
            service = service,
            onDismiss = { detailService = null },
            onApply = {
                detailService = null
                onApplyClicked(service)
            }
        )
    }
}

@Composable
fun ServiceCard(
    service: DocumentService,
    onViewDetails: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("service_card_${service.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    Text(
                        text = service.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }

                // Service Fee Badge
                Surface(
                    color = Blue50,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Blue100)
                ) {
                    Text(
                        text = "₹${service.feeInr.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Navy800,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate700,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate200)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Green600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.turnaroundTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onViewDetails,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate200),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Checklist", color = Navy900, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("apply_button_${service.id}")
                    ) {
                        Text("Apply", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDetailDialog(
    service: DocumentService,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
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
                                text = service.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Navy900
                            )
                            Text(
                                text = "Official Portal: ${service.officialPortal}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Amber600,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                item {
                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Blue50),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Assistance Service Fee", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text("₹${service.feeInr.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Navy800)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Turnaround", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(service.turnaroundTime, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Green600)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Required Documents Checklist",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                }

                items(service.checklist) { item ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Green600,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800
                        )
                    }
                }

                item {
                    Text(
                        text = "Assistance Instructions",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                }

                items(service.instructions) { instruction ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Amber500)
                                .padding(top = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate700
                        )
                    }
                }

                item {
                    // Legal Disclaimer
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Notice: TT Online Services provides document preparation and online submission assistance. Issuance is subject to verification by government portal authorities.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                    ) {
                        Text("Proceed with Application", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
