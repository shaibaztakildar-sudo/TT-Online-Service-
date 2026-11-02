package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppNotification
import com.example.data.model.DocumentService
import com.example.data.model.ServicesCatalog
import com.example.ui.admin.AdminDashboard
import com.example.ui.admin.AdminViewModel
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.SignInScreen
import com.example.ui.auth.signOut
import com.example.ui.customer.ApplicationFormDialog
import com.example.ui.customer.ApplicationTrackingScreen
import com.example.ui.customer.CustomerViewModel
import com.example.ui.customer.PaymentReceiptDialog
import com.example.ui.customer.PaymentsScreen
import com.example.ui.customer.ServicesScreen
import com.example.ui.customer.SupportScreen
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Green100
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.TTOnlineTheme
import com.example.ui.web.AboutScreen
import com.example.ui.web.ContactScreen
import com.example.ui.web.HomeScreen
import com.example.ui.web.WebsiteWebViewScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TTOnlineTheme {
                AppRoot(
                    authViewModel = authViewModel,
                    customerViewModel = customerViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    adminViewModel: AdminViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            firebaseUser = auth.currentUser
            auth.currentUser?.let {
                authViewModel.handleSignInSuccess(it)
                customerViewModel.startListening(it.uid)
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (firebaseUser == null) {
        SignInScreen(
            onSignInSuccess = {
                Firebase.auth.currentUser?.let {
                    firebaseUser = it
                    authViewModel.handleSignInSuccess(it)
                    customerViewModel.startListening(it.uid)
                }
            }
        )
    } else {
        val authState by authViewModel.uiState.collectAsStateWithLifecycle()
        val customerState by customerViewModel.uiState.collectAsStateWithLifecycle()
        val adminState by adminViewModel.uiState.collectAsStateWithLifecycle()

        // 0: Home, 1: Services, 2: Apply Now, 3: Track Application, 4: About Us, 5: Contact, 6: Owner Admin
        var currentNavPage by remember { mutableIntStateOf(0) }
        var showNotificationsDialog by remember { mutableStateOf(false) }
        var serviceToApply by remember { mutableStateOf<DocumentService?>(null) }

        // BackHandler for custom screen state
        BackHandler(enabled = currentNavPage != 0 || serviceToApply != null || customerState.receiptToView != null) {
            when {
                customerState.receiptToView != null -> customerViewModel.viewReceipt(null)
                serviceToApply != null -> serviceToApply = null
                currentNavPage != 0 -> currentNavPage = 0
            }
        }

        // Only load admin data if authenticated as owner admin
        LaunchedEffect(authState.isAdmin) {
            if (authState.isAdmin) {
                adminViewModel.loadData()
            }
        }

        // Handle error and success messages via snackbar
        LaunchedEffect(customerState.successMessage, customerState.errorMessage, adminState.successMessage, adminState.errorMessage) {
            val message = customerState.successMessage ?: customerState.errorMessage
                ?: adminState.successMessage ?: adminState.errorMessage
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                customerViewModel.clearMessages()
                adminViewModel.clearMessages()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(Navy900)) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { currentNavPage = 0 }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Amber500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Navy900,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "TT ONLINE SERVICES",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Indian Digital Document Assistance Portal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Amber500,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        },
                        actions = {
                            // Role Badge (Read-only for security)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (authState.isAdmin) Amber500 else Blue100,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = if (authState.isAdmin) "Owner Admin" else "Customer",
                                    color = Navy900,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Notifications
                            val unreadCount = customerState.notifications.count { !it.read }
                            IconButton(
                                onClick = { showNotificationsDialog = true },
                                modifier = Modifier.testTag("notifications_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(containerColor = Amber500, contentColor = Navy900) {
                                                Text("$unreadCount", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                                }
                            }

                            // Sign Out
                            IconButton(
                                onClick = {
                                    signOut(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onSignOutComplete = {
                                            firebaseUser = null
                                        },
                                        scope = coroutineScope
                                    )
                                },
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Navy900,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        )
                    )

                    // Web Navigation Bar (Horizontal Scrollable Tabs)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WebNavPill(title = "Home", isSelected = currentNavPage == 0, onClick = { currentNavPage = 0 })
                        WebNavPill(title = "Services (9)", isSelected = currentNavPage == 1, onClick = { currentNavPage = 1 })
                        WebNavPill(title = "Apply Now", isSelected = currentNavPage == 2, onClick = { currentNavPage = 2 })
                        WebNavPill(title = "Track Application", isSelected = currentNavPage == 3, onClick = { currentNavPage = 3 })
                        WebNavPill(title = "About Us", isSelected = currentNavPage == 4, onClick = { currentNavPage = 4 })
                        WebNavPill(title = "Contact", isSelected = currentNavPage == 5, onClick = { currentNavPage = 5 })

                        WebNavPill(title = "🌐 Live Website", isSelected = currentNavPage == 7, onClick = { currentNavPage = 7 }, isSpecial = true)

                        if (authState.isAdmin) {
                            WebNavPill(title = "Admin Panel", isSelected = currentNavPage == 6, onClick = { currentNavPage = 6 }, isSpecial = true)
                        }
                    }
                }
            },
            bottomBar = {
                // Adaptive Mobile Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentNavPage == 0,
                        onClick = { currentNavPage = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy900,
                            selectedTextColor = Navy900,
                            indicatorColor = Blue100
                        )
                    )

                    NavigationBarItem(
                        selected = currentNavPage == 1,
                        onClick = { currentNavPage = 1 },
                        icon = { Icon(Icons.Default.Description, contentDescription = "Services") },
                        label = { Text("Services", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy900,
                            selectedTextColor = Navy900,
                            indicatorColor = Blue100
                        )
                    )

                    NavigationBarItem(
                        selected = currentNavPage == 2,
                        onClick = { currentNavPage = 2 },
                        icon = { Icon(Icons.Default.PostAdd, contentDescription = "Apply") },
                        label = { Text("Apply", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy900,
                            selectedTextColor = Navy900,
                            indicatorColor = Blue100
                        )
                    )

                    NavigationBarItem(
                        selected = currentNavPage == 3,
                        onClick = { currentNavPage = 3 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    val activeCount = customerState.applications.count { it.status != "Completed" }
                                    if (activeCount > 0) {
                                        Badge(containerColor = Navy800, contentColor = Color.White) {
                                            Text("$activeCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Track")
                            }
                        },
                        label = { Text("Track", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy900,
                            selectedTextColor = Navy900,
                            indicatorColor = Blue100
                        )
                    )

                    if (authState.isAdmin) {
                        NavigationBarItem(
                            selected = currentNavPage == 6,
                            onClick = { currentNavPage = 6 },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Navy900,
                                selectedTextColor = Navy900,
                                indicatorColor = Amber500
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentNavPage) {
                    0 -> {
                        // Home Page
                        HomeScreen(
                            onNavigateToServices = { currentNavPage = 1 },
                            onNavigateToApply = { service -> serviceToApply = service },
                            onNavigateToTrack = { currentNavPage = 3 },
                            onNavigateToContact = { currentNavPage = 5 }
                        )
                    }

                    1 -> {
                        // Services Catalog Page
                        ServicesScreen(
                            services = customerState.services,
                            searchQuery = customerState.searchQuery,
                            onSearchQueryChange = { customerViewModel.setSearchQuery(it) },
                            selectedCategory = customerState.selectedCategory,
                            onCategorySelect = { customerViewModel.setSelectedCategory(it) },
                            onApplyClicked = { service -> serviceToApply = service }
                        )
                    }

                    2 -> {
                        // Apply Now Page: Select Service & Launch Application
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Start New Application",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Navy900
                            )
                            Text(
                                text = "Choose a document assistance service below to fill details and submit.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            ServicesScreen(
                                services = customerState.services,
                                searchQuery = customerState.searchQuery,
                                onSearchQueryChange = { customerViewModel.setSearchQuery(it) },
                                selectedCategory = customerState.selectedCategory,
                                onCategorySelect = { customerViewModel.setSelectedCategory(it) },
                                onApplyClicked = { service -> serviceToApply = service }
                            )
                        }
                    }

                    3 -> {
                        // Track Application Page
                        ApplicationTrackingScreen(
                            applications = customerState.applications,
                            payments = customerState.payments,
                            onViewReceipt = { payment -> customerViewModel.viewReceipt(payment) },
                            onSubmitAdditionalDoc = { appId, docName ->
                                customerViewModel.submitAdditionalDocument(appId, docName)
                            },
                            onNavigateToServices = { currentNavPage = 1 }
                        )
                    }

                    4 -> {
                        // About Us Page
                        AboutScreen()
                    }

                    5 -> {
                        // Contact Us Page
                        ContactScreen(
                            userEmail = firebaseUser?.email ?: "customer@example.com"
                        )
                    }

                    6 -> {
                        // Owner-Only Admin Panel
                        AdminDashboard(
                            isAdmin = authState.isAdmin,
                            applications = adminState.allApplications,
                            payments = adminState.allPayments,
                            searchQuery = adminState.searchQuery,
                            onSearchQueryChange = { adminViewModel.setSearchQuery(it) },
                            filterServiceId = adminState.filterServiceId,
                            onFilterServiceChange = { adminViewModel.setFilterService(it) },
                            filterStatus = adminState.filterStatus,
                            onFilterStatusChange = { adminViewModel.setFilterStatus(it) },
                            onUpdateStatus = { appId, status, notes, reqInfo ->
                                adminViewModel.updateApplicationStatus(appId, status, notes, reqInfo)
                            },
                            onUpdateFee = { serviceId, newFee ->
                                adminViewModel.updateServiceFee(serviceId, newFee)
                            },
                            customFees = adminState.customServiceFees
                        )
                    }

                    7 -> {
                        // Full Interactive Web Portal
                        WebsiteWebViewScreen()
                    }
                }
            }
        }

        // Multi-Step Application Form Dialog
        serviceToApply?.let { service ->
            ApplicationFormDialog(
                service = service,
                initialName = authState.userProfile?.name ?: firebaseUser?.displayName ?: "",
                initialEmail = authState.userProfile?.email ?: firebaseUser?.email ?: "",
                initialPhone = authState.userProfile?.phone ?: "",
                isLoading = customerState.isLoading,
                onDismiss = { serviceToApply = null },
                onSubmit = { appType, name, phone, email, address, idNum, attachedCount, payMethod ->
                    customerViewModel.submitApplicationWithPayment(
                        service = service,
                        applicationType = appType,
                        applicantName = name,
                        applicantPhone = phone,
                        applicantEmail = email,
                        applicantAddress = address,
                        idNumber = idNum,
                        attachedDocsCount = attachedCount,
                        paymentMethod = payMethod,
                        onSuccess = {
                            serviceToApply = null
                            currentNavPage = 3 // Navigate directly to Track Application
                        }
                    )
                }
            )
        }

        // Payment Receipt Dialog
        customerState.receiptToView?.let { payment ->
            PaymentReceiptDialog(
                payment = payment,
                onDismiss = { customerViewModel.viewReceipt(null) }
            )
        }

        // Notifications Dialog
        if (showNotificationsDialog) {
            NotificationsDialog(
                notifications = customerState.notifications,
                onDismiss = { showNotificationsDialog = false },
                onNotificationClick = { notif ->
                    customerViewModel.markNotificationAsRead(notif.notificationId)
                }
            )
        }
    }
}

@Composable
fun WebNavPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isSpecial: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            isSpecial && isSelected -> Amber500
            isSpecial -> Amber500.copy(alpha = 0.2f)
            isSelected -> Blue100
            else -> Color.White.copy(alpha = 0.12f)
        },
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = when {
                isSpecial && isSelected -> Navy900
                isSpecial -> Amber500
                isSelected -> Navy900
                else -> Color.White
            },
            fontSize = 12.sp,
            fontWeight = if (isSelected || isSpecial) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun NotificationsDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications (${notifications.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy900
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications yet.", color = Slate600, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (notif.read) Slate50 else Blue50,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNotificationClick(notif) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Navy900,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (!notif.read) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Amber500)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
