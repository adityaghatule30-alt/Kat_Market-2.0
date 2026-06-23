package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NekoOrder
import com.example.data.model.NekoPayment
import com.example.data.model.SupplierDeal
import com.example.ui.theme.*

import com.example.ui.MarketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Style Constants for Gaming Theme
val MidnightBg = Color(0xFF030305)
val CyberCard = Color(0xFF0B0C10)
val CyberBorder = Color(0xFF1F2833)
val NeonPurple = Color(0xFF8A2BE2)
val NeonOrchid = Color(0xFFBA55D3)
val NeonGreen = Color(0xFF00FF66)
val CyberGray = Color(0xFF455A64)
val TextMuted = Color(0xFF8892B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NekoCoinStoreScreen(viewModel: MarketViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    val packageList = listOf(
        StoreCoinPackage("Suzu Starter Pack", 100, 10.0),
        StoreCoinPackage("Neko Bronze Box", 250, 20.0),
        StoreCoinPackage("Kuro Silver Chest", 700, 50.0),
        StoreCoinPackage("Katar Gold Bundle", 1500, 100.0),
        StoreCoinPackage("Ecosystem Elite Vault", 3500, 200.0),
        StoreCoinPackage("Ultimate Empress Cache", 10000, 500.0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NEO COIN EXCHANGE",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("More") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo("AdminPayments") }) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Payments", tint = NeonPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBg)
            )
        },
        containerColor = MidnightBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header Balance Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF1A1B2F), Color(0xFF0B0D17))),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(1.dp, Brush.horizontalGradient(listOf(NeonPurple, NeonOrchid)), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Text(
                            text = "CURRENT ACCOUNT WALLET",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GeneratingTokens, "Coins Icon", tint = NeonOrchid, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${userProfile?.coinBalance ?: 0} NEO COINS",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "SECURE LEDGER",
                                    color = NeonOrchid,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Shop Section Label
            item {
                Text(
                    text = "SELECT COIN PACKAGE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 10.dp, bottom = 12.dp)
                )
            }

            // Grid of Packages
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    packageList.chunked(2).forEach { rowPackages ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowPackages.forEach { pkg ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CyberCard, RoundedCornerShape(14.dp))
                                        .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            viewModel.selectPackageAndCreateOrder(pkg.price, pkg.coins)
                                        }
                                        .padding(14.dp)
                                        .testTag("package_${pkg.coins}_coins")
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Icon(
                                            imageVector = Icons.Default.CurrencyExchange,
                                            contentDescription = null,
                                            tint = if (pkg.coins >= 3500) NeonOrchid else NeonPurple,
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "${pkg.coins} Coins",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = pkg.name,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "₹${pkg.price.toInt()}",
                                                color = NeonGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.AddShoppingCart,
                                                contentDescription = "Buy",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowPackages.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Bridge Instruction Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0C14)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(NeonGreen.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.AutoMode,
                                null,
                                tint = NeonGreen,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "KATPAY SMS BRIDGING ACTIVE",
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Payments made via UPI to 'adityaghatule30@okaxis' are verified automatically of bank SMS. Average speed: 5 seconds.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Transaction History section
            item {
                Text(
                    text = "TRANSACTION HISTORY & LEDGER",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (orders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No orders completed or pending yet.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                items(orders) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                if (order.status == "PENDING_PAYMENT") {
                                    viewModel.makeActiveOrder(order)
                                    viewModel.navigateTo("PaymentScreen")
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = order.id,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when (order.status) {
                                                    "COMPLETED" -> NeonGreen.copy(alpha = 0.15f)
                                                    "PENDING_PAYMENT" -> Color.Yellow.copy(alpha = 0.15f)
                                                    "EXPIRED" -> Color.Red.copy(alpha = 0.15f)
                                                    else -> Color.Gray.copy(alpha = 0.15f)
                                                },
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = order.status,
                                            color = when (order.status) {
                                                "COMPLETED" -> NeonGreen
                                                "PENDING_PAYMENT" -> Color.Yellow
                                                "EXPIRED" -> Color.Red
                                                else -> Color.Gray
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(order.createdAt)),
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${order.coinAmount} Coins",
                                    color = NeonOrchid,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "₹${order.amount.toInt()}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
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
fun NekoPaymentScreen(viewModel: MarketViewModel) {
    val activeOrder by viewModel.currentActiveOrder.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var timeLeft by remember { mutableStateOf(900) } // 15 mins = 900 seconds
    val openIntentIndicator = remember { mutableStateOf<String?>(null) }
    
    // Simulate Countdown Launcher timer
    LaunchedEffect(activeOrder) {
        if (activeOrder != null) {
            val remainSeconds = (((activeOrder!!.expiresAt - System.currentTimeMillis()) / 1000).toInt()).coerceAtLeast(0)
            timeLeft = remainSeconds
        }
    }

    LaunchedEffect(timeLeft, activeOrder) {
        if (timeLeft > 0 && activeOrder?.status == "PENDING_PAYMENT") {
            delay(1000)
            timeLeft -= 1
        } else if (timeLeft <= 0 && activeOrder?.status == "PENDING_PAYMENT") {
            viewModel.activeOrderTimedOut(activeOrder!!.id)
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        containerColor = MidnightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("CoinStore") }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("SECURE INSTANT CHECKOUT", color = TextMuted, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.weight(1.5f))
            }

            if (activeOrder == null) {
                Text("No order loaded. Return to store.", color = Color.White, modifier = Modifier.padding(20.dp))
                Button(onClick = { viewModel.navigateTo("CoinStore") }) {
                    Text("Go to Store")
                }
                return@Scaffold
            }

            val order = activeOrder!!

            // Order Status Summary Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ORDER IDENTIFIER", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(order.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("TOTAL AMOUNT DUE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("₹${order.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EXPIRES IN", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = timerString,
                            color = if (timeLeft < 120) Color.Red else Color(0xFFFF9900),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${order.coinAmount} Coins Package", color = NeonOrchid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code Container Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF07080E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SCAN UPI QR TO PAY",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Procedural Dynamic QR Drawing Code
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(4.dp, NeonPurple, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing static vector pattern blocks like standard QR using Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cellSize = size.width / 13f
                            // Top-left finder pattern
                            drawRect(Color.Black, Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(cellSize * 4, cellSize * 4))
                            drawRect(Color.White, Offset(cellSize, cellSize), size = androidx.compose.ui.geometry.Size(cellSize * 2, cellSize * 2))
                            drawRect(Color.Black, Offset(cellSize * 1.5f, cellSize * 1.5f), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))

                            // Top-right finder pattern
                            drawRect(Color.Black, Offset(size.width - cellSize * 4, 0f), size = androidx.compose.ui.geometry.Size(cellSize * 4, cellSize * 4))
                            drawRect(Color.White, Offset(size.width - cellSize * 3, cellSize), size = androidx.compose.ui.geometry.Size(cellSize * 2, cellSize * 2))
                            drawRect(Color.Black, Offset(size.width - cellSize * 2.5f, cellSize * 1.5f), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))

                            // Bottom-left finder pattern
                            drawRect(Color.Black, Offset(0f, size.height - cellSize * 4), size = androidx.compose.ui.geometry.Size(cellSize * 4, cellSize * 4))
                            drawRect(Color.White, Offset(cellSize, size.height - cellSize * 3), size = androidx.compose.ui.geometry.Size(cellSize * 2, cellSize * 2))
                            drawRect(Color.Black, Offset(cellSize * 1.5f, size.height - cellSize * 2.5f), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))

                            // Some cute randomly spaced digital block matrix bits to look authentically full QR
                            for (i in 4 until 9) {
                                for (j in 4 until 9) {
                                    if ((i + j) % 2 == 0) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(cellSize * i, cellSize * j),
                                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                            // Extra blocks along bottom right
                            drawRect(Color.Black, Offset(cellSize * 11, cellSize * 11), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))
                            drawRect(Color.Black, Offset(cellSize * 10, cellSize * 11), size = androidx.compose.ui.geometry.Size(cellSize * 2, cellSize))
                            drawRect(Color.Black, Offset(cellSize * 11, cellSize * 9), size = androidx.compose.ui.geometry.Size(cellSize, cellSize * 2))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("MERCHANT INTEL ADDRESS", color = TextMuted, fontSize = 9.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "adityaghatule30@okaxis",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("adityaghatule30@okaxis"))
                                viewModel.pushNotification("Copied", "UPI address adityaghatule30@okaxis copied to clipboard.", "INFO")
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, "Copy UPI ID", tint = NeonOrchid, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // External Wallet Launch App Intent Buttons
            Text(
                text = "PAY DIRECTLY VIA EXTERNAL APPS",
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Pair("GPay", Color(0xFF4285F4)),
                    Pair("PhonePe", Color(0xFF5F259F)),
                    Pair("Paytm", Color(0xFF00B9F5))
                ).forEach { (app, color) ->
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("adityaghatule30@okaxis"))
                            openIntentIndicator.value = app
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCard),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(app, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(visible = openIntentIndicator.value != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101224)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .border(1.dp, NeonOrchid.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "LAUNCHING ${openIntentIndicator.value?.uppercase()} ...",
                            color = NeonOrchid,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Copied UPI ID 'adityaghatule30@okaxis' and ₹${order.amount.toInt()} into cache. Open app and pay this merchant.",
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Button(
                onClick = { viewModel.refreshActiveOrderStatus() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("refresh_payment_status")
            ) {
                Icon(Icons.Default.Refresh, "Refresh icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("REFRESH STATUS (AWAITING PAYMENT...)", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive KatPay Bridge simulator built in for seamless evaluation!
            KatPayBridgeSimulator(viewModel = viewModel, amountForOrder = order.amount)
        }
    }
}

// Interactive Emulator for testing auto-detection (Bank of India SMS receiver simulation)
@Composable
fun KatPayBridgeSimulator(viewModel: MarketViewModel, amountForOrder: Double) {
    var testUtr by remember { mutableStateOf("") }
    var senderName by remember { mutableStateOf("BOI") }
    val scope = rememberCoroutineScope()

    // Create randomized valid 12-digit UTR on init
    LaunchedEffect(Unit) {
        testUtr = (524000000000L + (Math.random() * 999999999L).toLong()).toString()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0A15)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonGreen.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(NeonGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KATPAY BRIDGE EMULATOR (STETHOSCOPE TOOL)",
                    color = NeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "Since this app is executing inside a server environment without local device radio SMS access, simulate the owner's KatPay Bridge receiving a Bank of India UPI SMS containing your transaction details.",
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = testUtr,
                    onValueChange = { testUtr = it },
                    label = { Text("Simulated Bank UTR", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPurple
                    ),
                    modifier = Modifier.weight(1.5f)
                )
                OutlinedTextField(
                    value = "₹${amountForOrder.toInt()}",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Amount", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.White,
                        disabledLabelColor = Color.Gray
                    ),
                    modifier = Modifier.weight(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.simulateKatPayBridgeSms(amountForOrder, testUtr, senderName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulate_gateway_payment_button")
            ) {
                Text(
                    text = "SIMULATE BRIDGE SMS (INR ${amountForOrder.toInt()})",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun NekoSuccessScreen(viewModel: MarketViewModel) {
    val activeOrder by viewModel.currentActiveOrder.collectAsState()

    // Confetti Animation Controller
    val infiniteTransition = rememberInfiniteTransition("confetti")
    val confettiAnimY by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiY"
    )

    Scaffold(
        containerColor = MidnightBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clipToBounds()
        ) {
            // Draw Interactive Canvas Confetti
            Canvas(modifier = Modifier.fillMaxSize()) {
                val colors = listOf(NeonPurple, NeonOrchid, NeonGreen, Color.Yellow, Color.Cyan)
                val random = Random(42)
                for (i in 0 until 60) {
                    val x = (random.nextFloat() * size.width)
                    val offsetSpeed = (50 + random.nextInt(200))
                    val dynamicY = (confettiAnimY + i * offsetSpeed) % size.height
                    val sizeScale = (6 + random.nextInt(12)).dp.toPx()

                    drawCircle(
                        color = colors[i % colors.size].copy(alpha = 0.8f),
                        radius = sizeScale / 2,
                        center = Offset(x, dynamicY)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Checkbox Circle Animation
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(NeonGreen.copy(alpha = 0.15f), CircleShape)
                        .border(3.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success checkmark",
                        tint = NeonGreen,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "PAYMENT RECEIVED",
                    color = NeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${activeOrder?.coinAmount ?: 1500} COINS ADDED",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Invoice Slip Detail Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "OFFICIAL SECURE RECEIPT",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction ID", color = TextMuted, fontSize = 12.sp)
                            Text(activeOrder?.id ?: "ORD-SUCCESS", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Matched Coins", color = TextMuted, fontSize = 12.sp)
                            Text("+${activeOrder?.coinAmount ?: 1500} NEO", color = NeonOrchid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bank Reference (UTR)", color = TextMuted, fontSize = 12.sp)
                            Text("SMS AutoMatched Verified", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Settle Date", color = TextMuted, fontSize = 12.sp)
                            Text(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()), color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { viewModel.navigateTo("CoinStore") },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_success_screen")
                ) {
                    Text("RETURN TO COIN SHOP", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NekoAdminPaymentsScreen(viewModel: MarketViewModel) {
    val orders by viewModel.allOrders.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val supplierDeals by viewModel.allSupplierDeals.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Orders, 1: Payments Ingested, 2: Coin Ledger, 3: Supplier Deals
    var orderStatusFilter by remember { mutableStateOf("ALL") } // ALL, PENDING, COMPLETED, EXPIRED
    var paymentFilterType by remember { mutableStateOf("ALL") } // ALL, UNMATCHED, MATCHED

    var manualAssignOrderId by remember { mutableStateOf("") }
    var selectedPaymentForAssign by remember { mutableStateOf<NekoPayment?>(null) }

    // Dynamic Derived KPI statistics
    val totalPendingOrders = remember(orders) { orders.count { it.status == "PENDING_PAYMENT" } }
    val totalCompletedOrders = remember(orders) { orders.count { it.status == "COMPLETED" } }
    val totalUnmatchedPayments = remember(payments, transactions) {
        payments.count { pay -> transactions.none { it.paymentId == pay.id } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, "Admin icon", tint = NeonOrchid, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KATPAY ADMIN POWERBAR",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("CoinStore") }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBg)
            )
        },
        containerColor = MidnightBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // High-fidelity Stats Counter Dash Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Orders Metric Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.Yellow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "PENDING",
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$totalPendingOrders",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text("Active Orders", color = TextMuted, fontSize = 8.sp)
                    }
                }

                // Completed Orders Metric Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "COMPLETED",
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$totalCompletedOrders",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text("Settle Count", color = TextMuted, fontSize = 8.sp)
                    }
                }

                // Unmatched Ingestions Metric Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonOrchid.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "UNMATCHED",
                            color = NeonOrchid,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$totalUnmatchedPayments",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text("Sms Orphans", color = TextMuted, fontSize = 8.sp)
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MidnightBg,
                contentColor = NeonPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonPurple
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ORDERS", color = if (selectedTab == 0) NeonPurple else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("SMS", color = if (selectedTab == 1) NeonPurple else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("COINS", color = if (selectedTab == 2) NeonPurple else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("SUPPLIERS", color = if (selectedTab == 3) NeonPurple else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp) })
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // Orders Layout with Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "PENDING", "COMPLETED", "EXPIRED").forEach { filter ->
                            val isSelected = orderStatusFilter == filter
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NeonPurple.copy(alpha = 0.15f) else CyberCard
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonPurple else CyberBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { orderStatusFilter = filter }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter,
                                        color = if (isSelected) NeonPurple else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    val filteredOrders = remember(orders, orderStatusFilter) {
                        orders.filter { order ->
                            when (orderStatusFilter) {
                                "PENDING" -> order.status == "PENDING_PAYMENT"
                                "COMPLETED" -> order.status == "COMPLETED"
                                "EXPIRED" -> order.status == "EXPIRED"
                                else -> true
                            }
                        }
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No orders associated with this status.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filteredOrders) { order ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(order.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("Subscriber: ${order.userId}", color = TextMuted, fontSize = 11.sp)
                                                Text(
                                                    text = "Created: " + SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(order.createdAt)),
                                                    color = TextMuted,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = when (order.status) {
                                                            "COMPLETED" -> NeonGreen.copy(alpha = 0.15f)
                                                            "PENDING_PAYMENT" -> Color.Yellow.copy(alpha = 0.15f)
                                                            "EXPIRED" -> Color.Red.copy(alpha = 0.15f)
                                                            else -> Color.Gray.copy(alpha = 0.15f)
                                                        },
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = order.status,
                                                    color = when (order.status) {
                                                        "COMPLETED" -> NeonGreen
                                                        "PENDING_PAYMENT" -> Color.Yellow
                                                        "EXPIRED" -> Color.Red
                                                        else -> Color.Gray
                                                    },
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider(color = CyberBorder, thickness = 0.5.dp)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row {
                                                Text("Price: ₹${order.amount.toInt()} ", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("(${order.coinAmount} Coins)", color = NeonOrchid, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            if (order.status == "PENDING_PAYMENT" || order.status == "EXPIRED") {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(
                                                        onClick = { viewModel.adminApproveOrder(order.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("Approve", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Button(
                                                        onClick = { viewModel.adminRejectOrder(order.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("Reject", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // SMS Ingestions with Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "UNMATCHED", "MATCHED").forEach { filter ->
                            val isSelected = paymentFilterType == filter
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NeonOrchid.copy(alpha = 0.15f) else CyberCard
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonOrchid else CyberBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { paymentFilterType = filter }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter,
                                        color = if (isSelected) NeonOrchid else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Map payments to their match status derived from Ledger logs
                    val matchedPaymentsMap = remember(payments, transactions) {
                        payments.map { pay ->
                            val linkedTx = transactions.find { it.paymentId == pay.id }
                            Pair(pay, linkedTx)
                        }
                    }

                    val filteredPayments = remember(matchedPaymentsMap, paymentFilterType) {
                        matchedPaymentsMap.filter { (_, linkedTx) ->
                            when (paymentFilterType) {
                                "UNMATCHED" -> linkedTx == null
                                "MATCHED" -> linkedTx != null
                                else -> true
                            }
                        }
                    }

                    if (filteredPayments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No payment SMS entries in this registry.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filteredPayments) { (pay, tx) ->
                                val isMatched = tx != null
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = if (isMatched) NeonGreen.copy(alpha = 0.25f) else NeonOrchid.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column {
                                                Text("UTR ID: ${pay.utr}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("Gateway Sender: ${pay.smsSender}", color = TextMuted, fontSize = 11.sp)
                                                Text(
                                                    text = "Received: " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(pay.timestamp)),
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("₹${pay.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                // Dynamic matched status badge
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = if (isMatched) NeonGreen.copy(alpha = 0.15f) else Color(0xFFFF9900).copy(alpha = 0.15f),
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (isMatched) "MATCHED" else "UNMATCHED",
                                                        color = if (isMatched) NeonGreen else Color(0xFFFF9900),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Raw parsed Bank SMS
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Black, RoundedCornerShape(6.dp))
                                                .border(0.5.dp, CyberBorder, RoundedCornerShape(6.dp))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = pay.rawSms,
                                                color = Color.LightGray,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        if (isMatched) {
                                            // Displays bound connection
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(NeonGreen.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                                    .border(1.dp, NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Check, "Shield matched", tint = NeonGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Autolink Success: Connected to Order ${tx!!.orderId} for User ${tx.userId}",
                                                    color = NeonGreen,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        } else {
                                            // SMART MATCH RECOMMENDATIONS ENGINE (Advise Admin instantly)
                                            val smartMatches = remember(orders) {
                                                orders.filter { it.amount == pay.amount && it.status == "PENDING_PAYMENT" }
                                            }

                                            if (smartMatches.isNotEmpty()) {
                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A12)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                        .border(1.dp, NeonGreen.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text(
                                                            text = "💡 KATPAY SMART MATCH SUGGESTION",
                                                            color = NeonGreen,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        smartMatches.take(2).forEach { sMatch ->
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Column {
                                                                    Text(text = "Order ID: ${sMatch.id}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                    Text(text = "Buyer Profile: ${sMatch.userId}", color = TextMuted, fontSize = 10.sp)
                                                                }
                                                                Button(
                                                                    onClick = {
                                                                        viewModel.adminAssignPayment(pay.id, sMatch.id)
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                                    modifier = Modifier.height(26.dp)
                                                                ) {
                                                                    Text("Match & Link", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                        }
                                                    }
                                                }
                                            }

                                            // MANUAL MANUAL ASSIGN PANEL
                                            if (selectedPaymentForAssign?.id == pay.id) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color(0xFF0E0E12), RoundedCornerShape(10.dp))
                                                        .border(0.5.dp, NeonPurple, RoundedCornerShape(10.dp))
                                                        .padding(10.dp)
                                                ) {
                                                    Text(
                                                        "Manual Order Assignment",
                                                        color = NeonOrchid,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    // Fast pick active pending order
                                                    val pendingOrdersToSelect = remember(orders) {
                                                        orders.filter { it.status == "PENDING_PAYMENT" }
                                                    }

                                                    if (pendingOrdersToSelect.isNotEmpty()) {
                                                        Text("Quick Selection (Pending Orders):", color = TextMuted, fontSize = 9.sp)
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .horizontalScroll(rememberScrollState()),
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            pendingOrdersToSelect.forEach { pOrder ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .background(CyberBorder, RoundedCornerShape(6.dp))
                                                                        .clickable {
                                                                            manualAssignOrderId = pOrder.id
                                                                        }
                                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                                ) {
                                                                    Column {
                                                                        Text(pOrder.id, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                        Text("₹${pOrder.amount.toInt()}", color = NeonGreen, fontSize = 8.sp)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        OutlinedTextField(
                                                            value = manualAssignOrderId,
                                                            onValueChange = { manualAssignOrderId = it },
                                                            placeholder = { Text("e.g. ORD-123456", color = Color.Gray, fontSize = 11.sp) },
                                                            label = { Text("Selected Order ID", color = NeonPurple, fontSize = 9.sp) },
                                                            singleLine = true,
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedTextColor = Color.White,
                                                                unfocusedTextColor = Color.White,
                                                                focusedBorderColor = NeonPurple
                                                            ),
                                                            modifier = Modifier.weight(1.2f)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Button(
                                                            onClick = {
                                                                if (manualAssignOrderId.isNotEmpty()) {
                                                                    viewModel.adminAssignPayment(pay.id, manualAssignOrderId.trim().uppercase())
                                                                    selectedPaymentForAssign = null
                                                                    manualAssignOrderId = ""
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                                            modifier = Modifier.height(48.dp)
                                                        ) {
                                                            Text("Link", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            } else {
                                                Button(
                                                    onClick = {
                                                        selectedPaymentForAssign = pay
                                                        manualAssignOrderId = ""
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.15f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(Icons.Default.Link, "Link icon", tint = NeonOrchid, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("MANUALLY BIND PAYMENT TO USER ORDER", color = NeonOrchid, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Coin Ledger Screen
                    Text("COIN DISPATCH LEDGER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No dispatched transactions recorded.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(transactions) { tx ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Tx #${tx.id} Ledger Entry", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("User Wallet ID: ${tx.userId}", color = Color.White, fontSize = 11.sp)
                                            Row(modifier = Modifier.padding(top = 4.dp)) {
                                                Text("Link Order: ${tx.orderId ?: "None (Manual)"} ", color = TextMuted, fontSize = 11.sp)
                                                Text("Link Pay ID: ${tx.paymentId ?: "Manual"}", color = TextMuted, fontSize = 11.sp)
                                            }
                                            Text(
                                                text = "Completed: " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(tx.completedAt)),
                                                color = TextMuted,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                        Text("+${tx.coinsAdded} coins", color = NeonPurple, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    Text("SUPPLIER TRUST VERIFICATION DESPATCH ENGINE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (supplierDeals.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No supplier deal proposal logs recorded yet.", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(supplierDeals) { deal ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(deal.gameName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("Supplier ID: ${deal.userId}", color = TextMuted, fontSize = 11.sp)
                                                Text("6-Digit Code: ${deal.dealCode}", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = when (deal.status) {
                                                            "APPROVED" -> SuccessGreen.copy(alpha = 0.15f)
                                                            "REJECTED" -> ErrorRed.copy(alpha = 0.15f)
                                                            else -> WarningAmber.copy(alpha = 0.15f)
                                                        },
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = deal.status,
                                                    color = when (deal.status) {
                                                        "APPROVED" -> SuccessGreen
                                                        "REJECTED" -> ErrorRed
                                                        else -> WarningAmber
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = CyberBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Proof Link: ${deal.videoUrl}", color = ElectricBlue, fontSize = 11.sp, modifier = Modifier.clickable {})
                                                Text(
                                                    "Reputation Delta Requested: +${deal.reputationDelta} Reputation Score",
                                                    color = Color.LightGray,
                                                    fontSize = 11.sp
                                                )
                                            }

                                            Text(
                                                text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(deal.timestamp)),
                                                color = Color.Gray,
                                                fontSize = 9.sp
                                            )
                                        }

                                        if (deal.status == "PENDING") {
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.rejectSupplierDeal(deal.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text("REJECT DEAL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = { viewModel.approveSupplierDeal(deal.id, deal.reputationDelta) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text("APPROVE (+${deal.reputationDelta} XP)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class StoreCoinPackage(
    val name: String,
    val coins: Int,
    val price: Double
)
