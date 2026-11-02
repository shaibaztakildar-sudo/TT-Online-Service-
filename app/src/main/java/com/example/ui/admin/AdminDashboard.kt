package com.example.ui.admin

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.PaymentRecord
import com.example.data.model.ServiceApplication
import com.example.data.model.ServicesCatalog
import com.example.ui.customer.StatusBadge
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red100
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    isAdmin: Boolean,
    applications: List<ServiceApplication>,
    payments: List<PaymentRecord>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterServiceId: String?,
    onFilterServiceChange: (String?) -> Unit,
    filterStatus: String?,
    onFilterStatusChange: (String?) -> Unit,
    onUpdateStatus: (appId: String, newStatus: String, notes: String, reqInfo: String) -> Unit,
    onUpdateFee: (serviceId: String, newFee: Double) -> Unit,
    customFees: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    if (!isAdmin) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Red100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Red600,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Access Restricted",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Red600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This panel is accessible exclusively by authorized administrative accounts. Firestore Security Rules enforce multi-tenant isolation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate800,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Applications, 1: Service Fees, 2: Reports
    var activeAppForReview by remember { mutableStateOf<ServiceApplication?>(null) }

    val filteredApps = remember(applications, searchQuery, filterServiceId, filterStatus) {
        applications.filter { app ->
            val matchesSearch = searchQuery.isBlank() ||
                    app.applicationId.contains(searchQuery, ignoreCase = true) ||
                    app.customerName.contains(searchQuery, ignoreCase = true) ||
                    app.customerPhone.contains(searchQuery, ignoreCase = true)
            val matchesService = filterServiceId == null || app.serviceId == filterServiceId
            val matchesStatus = filterStatus == null || app.status == filterStatus
            matchesSearch && matchesService && matchesStatus
        }
    }

    val totalRevenue = remember(payments) {
        payments.sumOf { it.amount }
    }

    val pendingCount = remember(applications) {
        applications.count { it.status == "Pending" || it.status == "Under Review" }
    }

    val completedCount = remember(applications) {
        applications.count { it.status == "Completed" }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Admin Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Navy900,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Amber500
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Applications (${filteredApps.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Manage Fees", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Reports & Stats", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Applications Management Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Summary metrics
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Total Apps",
                                value = "${applications.size}",
                                color = Navy800,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Pending",
                                value = "$pendingCount",
                                color = Amber600,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Completed",
                                value = "$completedCount",
                                color = Green600,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Search box
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search by App ID (TT-2026-...), Name, or Phone") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_search_input"),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate600) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // Status Filter Chips
                    item {
                        val statuses = listOf("All", "Pending", "Under Review", "Processing", "Completed", "Rejected", "Additional Information Required")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(statuses) { st ->
                                val isSelected = (filterStatus == null && st == "All") || filterStatus == st
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onFilterStatusChange(if (st == "All") null else st) },
                                    label = { Text(st, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Navy800,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    if (filteredApps.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No matching applications found.", color = Slate600, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    } else {
                        items(filteredApps) { app ->
                            AdminAppCard(
                                app = app,
                                onReviewClicked = { activeAppForReview = app }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Service Fee Management Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Assistance Service Fees (INR ₹)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Navy900
                        )
                        Text(
                            text = "Configure customer facilitation fees across all 9 document services.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }

                    items(ServicesCatalog.allServices) { service ->
                        val currentFee = customFees[service.id] ?: service.feeInr
                        ServiceFeeRow(
                            service = service,
                            fee = currentFee,
                            onFeeSave = { newFee -> onUpdateFee(service.id, newFee) }
                        )
                    }
                }
            }

            2 -> {
                // Reports & Stats Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Navy900),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Revenue & Facilitation Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Revenue Collected", style = MaterialTheme.typography.labelSmall, color = Slate400)
                                        Text("₹${totalRevenue.toInt()}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Amber500)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Total Transactions", style = MaterialTheme.typography.labelSmall, color = Slate400)
                                        Text("${payments.size}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Service Volume Breakdown", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Navy900)
                                ServicesCatalog.allServices.forEach { s ->
                                    val count = applications.count { it.serviceId == s.id }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(s.title, style = MaterialTheme.typography.bodySmall, color = Slate700)
                                        Text("$count applications", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Navy800)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    activeAppForReview?.let { app ->
        AdminReviewDialog(
            app = app,
            onDismiss = { activeAppForReview = null },
            onSave = { newStatus, notes, reqInfo ->
                onUpdateStatus(app.applicationId, newStatus, notes, reqInfo)
                activeAppForReview = null
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Slate600)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = color)
        }
    }
}

@Composable
fun AdminAppCard(
    app: ServiceApplication,
    onReviewClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_app_card_${app.applicationId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${app.serviceName} • ${app.applicationType}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Navy900
            )
            Text(
                text = "Applicant: ${app.customerName} • Phone: ${app.customerPhone} • Email: ${app.customerEmail}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )

            if (app.adminNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${app.adminNotes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paid: ₹${app.amountPaid.toInt()} (${app.paymentStatus})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Green600
                )

                Button(
                    onClick = onReviewClicked,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_review_button_${app.applicationId}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Review & Update", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewDialog(
    app: ServiceApplication,
    onDismiss: () -> Unit,
    onSave: (newStatus: String, notes: String, reqInfo: String) -> Unit
) {
    var status by remember { mutableStateOf(app.status) }
    var notes by remember { mutableStateOf(app.adminNotes) }
    var reqInfo by remember { mutableStateOf(app.additionalInfoRequested) }
    var isExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "Pending", "Under Review", "Processing", "Completed", "Rejected", "Additional Information Required"
    )

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
                                text = "Operator Action: ${app.applicationId}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Navy900
                            )
                            Text(app.serviceName, style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                item {
                    Text("Update Application Status", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)
                    ExposedDropdownMenuBox(
                        expanded = isExpanded,
                        onExpandedChange = { isExpanded = !isExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("admin_status_dropdown"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        status = option
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Operator Processing Notes (Visible to applicant)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = reqInfo,
                        onValueChange = { reqInfo = it },
                        label = { Text("Request Specific Documents (if Info Required)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Button(
                        onClick = { onSave(status, notes, reqInfo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_save_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                    ) {
                        Text("Save Status & Notify Customer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceFeeRow(
    service: com.example.data.model.DocumentService,
    fee: Double,
    onFeeSave: (Double) -> Unit
) {
    var feeText by remember(fee) { mutableStateOf("${fee.toInt()}") }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)
                Text(service.category, style = MaterialTheme.typography.labelSmall, color = Slate600)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₹", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Navy800)
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it },
                    modifier = Modifier.width(90.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val parsed = feeText.toDoubleOrNull()
                        if (parsed != null && parsed >= 0) {
                            onFeeSave(parsed)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Save", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}
