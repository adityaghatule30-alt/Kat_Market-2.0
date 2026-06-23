package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.MarketViewModel
import com.example.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(viewModel: MarketViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val watchlist by viewModel.watchlistListings.collectAsState()
    val rooms by viewModel.dealRooms.collectAsState()
    val tickets by viewModel.coinTickets.collectAsState()
    val supplierDeals by viewModel.allSupplierDeals.collectAsState()

    var profileTab by remember { mutableStateOf("WATCHLIST") } // WATCHLIST, CONTRACTS, COINS, DEALS

    Scaffold(
        bottomBar = { AppBottomNavigation("Profile") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("profile_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // 1. Sleek Cyber Avatar + Details Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030510))
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern holographic outline circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0x3300E5FF), DarkMidnight)))
                            .border(1.5.dp, CyanGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name ?: "U").take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user?.name ?: "Aditya Ghatule",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user?.reputationRank ?: "Trusted Trader",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Score: ${user?.traderRankScore ?: 95}%",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(user?.email ?: "adityaghatule40@gmail.com", color = TextMuted, fontSize = 12.sp)
                }
            }

            // 2. Tab toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceOffset)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "WATCHLIST" to "Watchlist",
                    "CONTRACTS" to "Active Deals",
                    "COINS" to "Credit Orders",
                    "DEALS" to "Supplier"
                ).forEach { (t, label) ->
                    val isSel = profileTab == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ElectricBlue else Color(0x0AFFFFFF))
                            .clickable { profileTab = t }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextSecondary)
                    }
                }
            }

            // 3. Render content matching selected tabs
            Column(modifier = Modifier.padding(16.dp)) {
                when (profileTab) {
                    "WATCHLIST" -> {
                        Text("SAVED CONTRACTS WATCHLIST", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (watchlist.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceOffset),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No listings watchlisted.", color = TextSecondary)
                            }
                        } else {
                            Column(
                                modifier = Modifier.heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                watchlist.take(3).forEach { listing ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceOffset)
                                            .border(1.dp, GlassCardBorder, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.selectListing(listing) }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(listing.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(listing.location, color = TextSecondary, fontSize = 11.sp)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Bookmark, null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("$${String.format("%,.0f", listing.price)}", color = Color.White, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "CONTRACTS" -> {
                        Text("ACTIVE TRANSACTION LOG DESIGNATIONS", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (rooms.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceOffset),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No live trading channels registered.", color = TextSecondary)
                            }
                        } else {
                            Column(
                                modifier = Modifier.heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rooms.take(3).forEach { room ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceOffset)
                                            .border(1.dp, GlassCardBorder, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.selectDealRoom(room) }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(room.listingTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Partner: ${room.sellerName}", color = TextSecondary, fontSize = 11.sp)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Forum, null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = room.currentStatus,
                                                color = if (room.currentStatus == "ACCEPTED") SuccessGreen else PremiumGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "COINS" -> {
                        Text("UPI NEO COIN REVENUE TICKETS", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (tickets.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceOffset),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No coin exchange tickets loaded.", color = TextSecondary)
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tickets.forEach { t ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceOffset)
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Ticket: ${t.packageName.uppercase()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("TXID: ${t.transactionId.take(15)}...", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }

                                        Text(
                                            text = t.status,
                                            color = when (t.status) {
                                                "APPROVED" -> SuccessGreen
                                                "REJECTED" -> ErrorRed
                                                else -> PremiumGold
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "DEALS" -> {
                        Text("DECLARE COMPLETED TRADE AS SUPPLIER", fontSize = 11.sp, color = ElectricBlue, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        var gameName by remember { mutableStateOf("") }
                        var dealCode by remember { mutableStateOf("") }
                        var proofLink by remember { mutableStateOf("") }
                        var repDelta by remember { mutableStateOf(1) }
                        var statusMsg by remember { mutableStateOf<String?>(null) }
                        var isError by remember { mutableStateOf(false) }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("New Trade Ingestion", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Submit your completed buyer/seller deal to receive a reputation boost in your dashboard.", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))

                                OutlinedTextField(
                                    value = gameName,
                                    onValueChange = { gameName = it },
                                    label = { Text("Game Name (e.g., Clash of Clans)", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = ElectricBlue,
                                        cursorColor = ElectricBlue
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("supplier_game_name_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = dealCode,
                                    onValueChange = { input ->
                                        if (input.all { it.isDigit() } && input.length <= 6) {
                                            dealCode = input
                                        }
                                    },
                                    label = { Text("6-Digit Code (e.g. 524109)", fontSize = 11.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = ElectricBlue,
                                        cursorColor = ElectricBlue
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("supplier_deal_code_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = proofLink,
                                    onValueChange = { proofLink = it },
                                    label = { Text("Video Proof Link (YouTube / Drive)", fontSize = 11.sp) },
                                    singleLine = true,
                                    placeholder = { Text("https://youtube.com/...", color = Color.Gray, fontSize = 12.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = ElectricBlue,
                                        cursorColor = ElectricBlue
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("supplier_proof_link_input")
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Desired Reputation Reward", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Select boost based on deal value/proof effort.", color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(1, 2).forEach { valBoost ->
                                            val isSel = repDelta == valBoost
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSel) ElectricBlue.copy(alpha = 0.2f) else Color(0x10FFFFFF)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSel) ElectricBlue else Color.Transparent,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { repDelta = valBoost }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "+$valBoost XP",
                                                    color = if (isSel) ElectricBlue else Color.Gray,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                statusMsg?.let { msg ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = msg,
                                        color = if (isError) ErrorRed else SuccessGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        if (gameName.trim().isEmpty() || dealCode.length != 6 || proofLink.trim().isEmpty()) {
                                            isError = true
                                            statusMsg = "Error: All fields are required; Deal code must be 6 digits exactly."
                                        } else {
                                            viewModel.submitSupplierDeal(
                                                gameName = gameName.trim(),
                                                dealCode = dealCode,
                                                videoUrl = proofLink.trim(),
                                                reputationDelta = repDelta
                                            )
                                            gameName = ""
                                            dealCode = ""
                                            proofLink = ""
                                            statusMsg = "Done Deal submitted to admin verification ledger!"
                                            isError = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("submit_supplier_deal_btn"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("PROPOSE DONE DEAL FOR VERIFICATION", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Text("MY SUPPLIER PORTFOLIO RECORDS", fontSize = 11.sp, color = NeonOrchid, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val myDealsBySupplier = remember(supplierDeals, user) {
                            supplierDeals.filter { it.userId == (user?.email ?: "adityaghatule40@gmail.com") }
                        }

                        if (myDealsBySupplier.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceOffset),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No supplier done deals submitted yet.", color = TextSecondary, fontSize = 12.sp)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                myDealsBySupplier.forEach { deal ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, GlassCardBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(deal.gameName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("Code ID: ${deal.dealCode} (Supplier)", color = TextSecondary, fontSize = 11.sp)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = when (deal.status) {
                                                                "APPROVED" -> SuccessGreen.copy(alpha = 0.15f)
                                                                "REJECTED" -> ErrorRed.copy(alpha = 0.15f)
                                                                else -> PremiumGold.copy(alpha = 0.15f)
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
                                                            else -> PremiumGold
                                                        },
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Divider(color = GlassCardBorder.copy(alpha = 0.2f), thickness = 0.5.dp)
                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("Active Boost Requested: +${deal.reputationDelta} Reputation", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                                    Text(
                                                        "Submitted: ${SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(deal.timestamp))}",
                                                        color = TextMuted,
                                                        fontSize = 9.sp
                                                    )
                                                }
                                                
                                                Text(
                                                    text = "Video Proof ↗",
                                                    color = ElectricBlue,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.clickable {
                                                        // clickable youtube link stub
                                                    }
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

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MoreScreen(viewModel: MarketViewModel) {
    Scaffold(
        bottomBar = { AppBottomNavigation("More") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("more_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("SYSTEM AND PREFERENCES", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Advanced Suite", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)
            
            Text(
                "Access administrative panels, alerts records, market trends summary analysis as well as scam audits below.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Menu Options List
            SystemMenuItem(
                title = "Administrative Control Panel",
                description = "Manage listing audits, approve coin sales slips and reviews",
                icon = Icons.Default.AdminPanelSettings,
                tint = PremiumGold,
                testTag = "menu_admin_panel"
            ) {
                viewModel.navigateTo("Admin")
            }

            SystemMenuItem(
                title = "Scammer Shield Reports Log",
                description = "Watch suspect accounts reported by authoritative scans",
                icon = Icons.Default.Security,
                tint = ErrorRed,
                testTag = "menu_scammer_shield"
            ) {
                viewModel.navigateTo("ScammerShield")
            }

            SystemMenuItem(
                title = "System Alerts center",
                description = "Unread notifications regarding price drops and deals",
                icon = Icons.Default.NotificationsActive,
                tint = CyanGlow,
                testTag = "menu_notifications"
            ) {
                viewModel.navigateTo("Notifications")
            }

            SystemMenuItem(
                title = "Exchange Coins Portal",
                description = "Exchange UPI slips to accumulate secure shopping tokens",
                icon = Icons.Default.MonetizationOn,
                tint = SuccessGreen,
                testTag = "menu_coin_store"
            ) {
                viewModel.navigateTo("CoinStore")
            }

            SystemMenuItem(
                title = "Market Intelligence Center",
                description = "View pricing demand score tables, fast items data analysis",
                icon = Icons.Default.TrendingUp,
                tint = ElectricBlue,
                testTag = "menu_market_intel"
            ) {
                viewModel.navigateTo("MarketIntel")
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Logout row button
            Button(
                onClick = { viewModel.performLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ErrorRed, RoundedCornerShape(12.dp))
                    .testTag("system_logout_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = ErrorRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out Safe Session", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SystemMenuItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF030510)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(description, color = TextSecondary, fontSize = 11.sp)
            }

            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}

// ------------------------------------------
// ADMIN AND AUDITING SCREEN CHANNELS
// ------------------------------------------

@Composable
fun AdminPanelScreen(viewModel: MarketViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val tickets by viewModel.coinTickets.collectAsState()
    val scams by viewModel.scamReports.collectAsState()

    var activeAdminTab by remember { mutableStateOf("SLIPS") } // SLIPS, SCAM_ADMIN

    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_panel_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("More") }) {
                    Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                }

                Text(
                    text = "AUTHORITATIVE CONTROL",
                    color = PremiumGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text("ADMIN CONTROL CONTEXT", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Ecosystem Manager", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "SLIPS" to "Verify Receipts",
                    "SCAM_ADMIN" to "Abuse Reports"
                ).forEach { (t, label) ->
                    val isSel = activeAdminTab == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ElectricBlue else Color(0x11FFFFFF))
                            .clickable { activeAdminTab = t }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextSecondary)
                    }
                }
            }

            if (user?.email != "adityaghatule40@gmail.com") {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("ACCESS RESTRICTED: NOT OWNER PROTOCOL ROLE", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            } else {
                when (activeAdminTab) {
                    "SLIPS" -> {
                        if (tickets.filter { it.status == "PENDING" }.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("No pending UPI payment tickets.", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(tickets.filter { it.status == "PENDING" }) { ticket ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("Purchased package: ${ticket.packageName}", color = Color.White, fontWeight = FontWeight.Bold)
                                                    Text("User: Ecosystem Member", color = TextSecondary, fontSize = 11.sp)
                                                }

                                                Text(
                                                    text = "${ticket.coinAmount} COINS",
                                                    color = PremiumGold,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            Text(
                                                text = "TXN ID: ${ticket.transactionId}",
                                                color = CyanGlow,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TextButton(
                                                    onClick = { viewModel.rejectUpiPaymentSlip(ticket.id) },
                                                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                                                ) {
                                                    Text("REJECT SLIP", fontWeight = FontWeight.Bold)
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Button(
                                                    onClick = { viewModel.approveUpiPaymentSlip(ticket.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                                ) {
                                                    Text("VALIDATE & ADD COINS", fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        if (scams.filter { it.status == "PENDING" }.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("No pending abuse investigations logs.", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(scams.filter { it.status == "PENDING" }) { report ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("Suspect: ${report.offenderName ?: report.listingTitle ?: "General Outlaw"}", color = ErrorRed, fontWeight = FontWeight.Bold)
                                                    Text("Filer: ${report.reporterName}", color = TextSecondary, fontSize = 11.sp)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(10.dp)
                                                            .clip(CircleShape)
                                                            .background(ErrorRed)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("ABUSE LOG", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                }
                                            }

                                            Text(
                                                text = report.details,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(vertical = 10.dp)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(
                                                    onClick = { viewModel.closeScamShieldReport(report.id, "DISMISSED") },
                                                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                                                ) {
                                                    Text("DISMISS")
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Button(
                                                    onClick = { viewModel.closeScamShieldReport(report.id, "BANNED_CONFIRMED") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                                                ) {
                                                    Text("RESOLVE & BLACKLIST SUSPECT", fontWeight = FontWeight.Bold, color = Color.White)
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

@Composable
fun ScammerShieldScreen(viewModel: MarketViewModel) {
    val reports by viewModel.scamReports.collectAsState()
    
    var showReportDialog by remember { mutableStateOf(false) }
    var suspect by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("scammer_shield_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("More") }) {
                    Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                }

                Button(
                    onClick = { showReportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Flag, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("File Report Slips", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }

            Text("KAT NEURAL INTEGRITY LOGS", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 10.dp))
            Text("Neural Scammer Shield", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)

            Text(
                text = "Track fraudulent duplicate account, item claims and counter-verification logs. Feel protected with live admin moderation logs.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Overlap file report wizard if active
            if (showReportDialog) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, Color(0x33EF4444), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CONSTRUCT MISCONDUCT INCIDENT TICKET", color = Color.White, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = suspect,
                            onValueChange = { suspect = it },
                            placeholder = { Text("Suspect User Title or Listing Code", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            placeholder = { Text("Describe proof, duplicate screenshot evidence, UPI coin capture fraud logs...", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showReportDialog = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (suspect.isNotEmpty() && reason.isNotEmpty()) {
                                        viewModel.submitScamShieldReport(suspect, reason)
                                        suspect = ""
                                        reason = ""
                                        showReportDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                            ) {
                                Text("File Dispute Ticket", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Reports history logs lists
            if (reports.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Cyber shield reports log is empty.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(reports) { report ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Suspect: ${report.offenderName ?: report.listingTitle ?: "General Outlaw"}", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Reported by matches: ${report.reporterName}", color = TextSecondary, fontSize = 11.sp)
                                    }

                                    Text(
                                        text = report.status,
                                        color = if (report.status == "BANNED_CONFIRMED") ErrorRed else PremiumGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = report.details,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
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
fun NotificationsScreen(viewModel: MarketViewModel) {
    val notes by viewModel.unreadNotifications.collectAsState()

    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("notifications_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("More") }) {
                    Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                }

                TextButton(onClick = { viewModel.clearNotifications() }) {
                    Text("Clear All", color = CyanGlow, fontWeight = FontWeight.Bold)
                }
            }

            Text("REAL-TIME SYSTEM NOTIFICATION ALERTS", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("System Alerts Center", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)

            Text(
                "Keep track of security audits, coin validations, direct offer room counters as well as local updates here.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Your notification tray is empty.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(notes) { n ->
                        val badgeColor = when (n.type) {
                            "SEC" -> ErrorRed
                            "COIN" -> PremiumGold
                            else -> CyanGlow
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(n.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(n.message, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketIntelScreen(viewModel: MarketViewModel) {
    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("market_intel_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("More") },
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
            }

            Text("KAT NEURAL TRANSACTION ANALYSIS INDEX", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Market Intelligence", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)

            Text(
                "Keep close watch of category demand score indices, average pricing indices as well as hot trends summary charts parsed from authoritative database records.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Categorized demand metrics
            Text("CYBER CATEGORIES DEMAND SCORES", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    IntelProgressRow("🚗 Vehicles (Super cars, Helicopters)", 0.88f, "88% HIGH ACTIVE")
                    IntelProgressRow("🏠 Properties (Hill villas, Apartments)", 0.95f, "95% EXTRAORDINARY")
                    IntelProgressRow("🏢 Businesses (Secure banks, Nightclubs)", 0.64f, "64% STEADY MODERATE")
                    IntelProgressRow("📦 Elite custom accessory Items", 0.45f, "45% BALANCED")
                }
            }

            // Database Statistics summary
            Spacer(modifier = Modifier.height(14.dp))
            Text("AUTHORITATIVE DATABASE SYNC CHECKS", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SpecRow("Properties CSV relationship validation", "100% SECURED ACCURATE")
                    SpecRow("Businesses CSV validation", "100% PASS STATUS")
                    SpecRow("Unique location names loaded count", "42 Locations Active")
                    SpecRow("Anti-capture screen security layer", "ACTIVE ON COINS")
                }
            }
        }
    }
}

@Composable
fun IntelProgressRow(label: String, progress: Float, status: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(status, color = PremiumGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(6.dp))
        
        // Progress bar back
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF030510))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(ElectricBlue, CyanGlow)))
            )
        }
    }
}

// ------------------------------------------
// PRESET STYLISH SHARED UI COMPONENTS
// ------------------------------------------

@Composable
fun SpecRow(label: String, valText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(valText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// ------------------------------------------
// PREMIUM UPI DEEP INTEGRATION COINS SHOP
// ------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinStoreScreen(viewModel: MarketViewModel) {
    val user by viewModel.userProfile.collectAsState()
    
    var activePackage by remember { mutableStateOf<CoinPackage?>(null) }
    var userSubmitedTxnId by remember { mutableStateOf("") }
    
    // Multi step shop flow: 1. SELECT, 2. PAYMENT_PROOF
    var shopStep by remember { mutableStateOf("SELECT") }

    val packages = listOf(
        CoinPackage("Starter Package", 10, 199.00, "Excellent starter slip value"),
        CoinPackage("Trader Premium Slips", 50, 799.00, "Most popular choice with VIP badge"),
        CoinPackage("Elite Investor Chest", 150, 1999.00, "Unlocks Trusted Trader status"),
        CoinPackage("Market Legend Vault", 400, 4999.00, "+ Unlocks Elite Trader Verification level")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NEO COINS EXCHANGE GATEWAY", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (shopStep == "SELECT") viewModel.navigateTo("More") else shopStep = "SELECT"
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceOffset)
            )
        },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("coin_store_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            // Render package details screen
            if (shopStep == "SELECT") {
                Text("EXCHANGE PREFERENCES & BALANCES", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Personal Coin balance", color = TextSecondary, fontSize = 12.sp)
                        Text("${user?.coinBalance ?: 0} NEO Coins", color = PremiumGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }

                    Row {
                        Icon(Icons.Default.Verified, null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trader: Verified", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "NEO Coins are the secure utility asset required to deploy premium custom announcements, secure fast tracking or unlock owner verification ranks.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Render lists of Packages
                packages.forEach { pkg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                activePackage = pkg
                                shopStep = "PAYMENT_PROOF"
                            },
                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(pkg.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(pkg.terms, color = TextSecondary, fontSize = 11.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Token, null, tint = PremiumGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+${pkg.coins} NEO", color = PremiumGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                }
                                Text("₹${pkg.priceInInr}", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                val pkg = activePackage!!
                
                Text("SECURED UPI RECEIPT VALIDATION FLOW", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("UPI Fast Settlement", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)

                Text(
                    text = "Follow instructions strictly to settlement logs instantly. After completing payment via external UPI, copy transaction ID and submit.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // UPI Copy QR CARD Screen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("MAPPED UPI ADDRESS", fontSize = 10.sp, color = TextSecondary)
                        Text("neomarketv2@upi", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        
                        Button(
                            onClick = { 
                                viewModel.pushNotification(
                                    "UPI Address Copied",
                                    "neomarketv2@upi copied to clipboard.",
                                    "INFO"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Merchant UPI Code")
                        }

                        HorizontalDivider(color = GlassCardBorder, modifier = Modifier.padding(vertical = 14.dp))

                        // Procedural QR mockup
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(4.dp, CyanGlow, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("QR LOG", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Step 1: Open PhonePe/GPay/Paytm & Pay standard ₹${pkg.priceInInr}", color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enter Txn ID row
                Text("SUBMIT UNIQUE TRANSACTION ID DETAILS", color = CyanGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                
                OutlinedTextField(
                    value = userSubmitedTxnId,
                    onValueChange = { userSubmitedTxnId = it },
                    placeholder = { Text("Enter 12 digit UPI transaction ID (e.g. 524031649012)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Anti screen recording checklist notice
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030510))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PrivacyTip, null, tint = CyanGlow)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Screen capture and duplication detectors are actively scanning. Any fraudulent transaction ID claim results in auto-reputation blacklist rookie level.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (userSubmitedTxnId.length >= 6) {
                            viewModel.submitUpiPaymentTicket(pkg.title, pkg.coins, userSubmitedTxnId)
                            userSubmitedTxnId = ""
                            shopStep = "SELECT"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("confirm_upi_paid_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("I HAVE INITIATED THE TRANSFER", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

data class CoinPackage(
    val title: String,
    val coins: Int,
    val priceInInr: Double,
    val terms: String
)
