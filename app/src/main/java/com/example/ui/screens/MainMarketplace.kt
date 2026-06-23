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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Listing
import com.example.ui.MarketViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AppBottomNavigation(currentScreen: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Market" to Icons.Default.ShoppingCart,
        "Create" to Icons.Default.AddCircle,
        "Community" to Icons.Default.ChatBubble,
        "Profile" to Icons.Default.Person,
        "More" to Icons.Default.Menu
    )

    NavigationBar(
        containerColor = SurfaceOffset,
        tonalElevation = 8.dp,
        modifier = Modifier
            .border(width = 1.dp, brush = Brush.verticalGradient(listOf(GlassCardBorder, Color.Transparent)), shape = RoundedCornerShape(0.dp))
            .navigationBarsPadding(),
        windowInsets = WindowInsets.navigationBars
    ) {
        items.forEach { (screen, icon) ->
            val isSelected = currentScreen == screen || (screen == "More" && (currentScreen == "Admin" || currentScreen == "Audit"))
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = icon, contentDescription = screen) },
                label = { Text(screen, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = Color(0x2200E5FF),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: MarketViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val featured by viewModel.featuredListings.collectAsState()
    val trending by viewModel.trendingListings.collectAsState()

    // Live sales ticker animation helper
    val tickerItems = remember {
        listOf(
            "⚡ SECURE DEAL SOLD: Michael De Santa acquired House #103 Vinewood Hills for $8,500,000",
            "⚡ NEO COIN ACCREDITED: Trevor Philips purchased Legend Package (+340 Coins)",
            "⚡ EXCLUSIVE VEHICLE ACTIVE: Devin Weston watchlisted Buckingham Luxor Helicopter",
            "⚡ VERIFIED AUDIT COMPLETE: Lester Crest verified 100% relationship integrity on Property CSV",
            "⚡ DEAL ROOM UPDATE: Aditya G opened private offer debate for Pegassi Osiris supercar"
        )
    }
    var tickerIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            tickerIndex = (tickerIndex + 1) % tickerItems.size
        }
    }

    Scaffold(
        bottomBar = { AppBottomNavigation("Home") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("home_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // 1. Premium Header with visual hero banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1782197692819),
                    contentDescription = "Home Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay darker cyber navy gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkMidnight),
                                startY = 30f
                            )
                        )
                )

                // Identity + Welcome row
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KAT MARKET NEO",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = CyanGlow
                                ),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Welcome back, ${user?.name ?: "Guest"}",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        // Notifications indicator bell (shows notification overlay inside more screens)
                        IconButton(
                            onClick = { viewModel.navigateTo("Notifications") },
                            modifier = Modifier
                                .background(Color(0x7303071E), CircleShape)
                                .border(1.dp, GlassCardBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = PremiumGold)
                        }
                    }

                    // Ticker layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x99010515))
                            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Live ticker",
                            tint = CyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedContent(
                            targetState = tickerIndex,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "ticker"
                        ) { index ->
                            Text(
                                text = tickerItems[index],
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // 2. Wallet & Coins & Status Blocks
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL ASSET VALUE", fontSize = 10.sp, color = TextSecondary, letterSpacing = 1.sp)
                                Text(
                                    text = "$${String.format("%,.0f", user?.walletBalance ?: 0.0)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x1AFFD700))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Token, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${user?.coinBalance ?: 0} NEO",
                                        fontWeight = FontWeight.Black,
                                        color = PremiumGold,
                                        fontSize = 14.sp
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, "Rank verified icon", tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(user?.reputationRank ?: "Trader", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider(color = GlassCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                        // Score metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.SwapCalls, null, tint = CyanGlow, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TRADES CERTIFIED", fontSize = 10.sp, color = TextSecondary)
                                }
                                Text("${user?.completedTrades ?: 0} Trades", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShowChart, null, tint = CyanGlow, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TRUST SCORE", fontSize = 10.sp, color = TextSecondary)
                                }
                                Text("${user?.traderRankScore ?: 0}% Rating", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, null, tint = CyanGlow, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VERIFICATION LEVEL", fontSize = 10.sp, color = TextSecondary)
                                }
                                Text(user?.verificationLevel ?: "Basic", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                            }
                        }
                    }
                }

                // 3. Featured Carousels / Lists
                Text(
                    text = "FEATURED PREMIUM CONTRACTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanGlow,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )

                if (featured.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceOffset),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No featured items live yet.", color = TextSecondary)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(featured) { listing ->
                            FeaturedContractCard(listing = listing) {
                                viewModel.selectListing(listing)
                            }
                        }
                    }
                }

                // Quick Utilities Shortcuts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x0E00E5FF))
                            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo("MarketIntel") }
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Insights, "Intel", tint = CyanGlow)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Market Intelligence", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Trends & hot indices", fontSize = 10.sp, color = TextSecondary)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x0EFFD700))
                            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo("CoinStore") }
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Token, "Coin shop link", tint = PremiumGold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Exchange NEO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Buy tokens / VIP levels", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // 4. Live trending listings
                Text(
                    text = "HOT LIVE TRENDING DEALS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumGold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )

                if (trending.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceOffset),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No trending listing records.", color = TextSecondary)
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        trending.forEach { listing ->
                            TrendingDealRow(listing = listing) {
                                viewModel.selectListing(listing)
                            }
                        }
                    }
                }

                // Large Interactive announcements list
                Spacer(modifier = Modifier.height(28.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .border(1.dp, Color(0x2210B981), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1210B981))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COMMUNITY ANNOUNCEMENT", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Neo Grand Opening Auction scheduled next Saturday! Expect rare car collection sets and premium real-estate locations parsed from database records.", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { viewModel.navigateTo("Community") },
                            colors = ButtonDefaults.textButtonColors(contentColor = SuccessGreen),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Join Forums")
                            Icon(Icons.Default.ArrowRight, null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedContractCard(listing: Listing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Visual badge header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listing.type,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanGlow,
                    modifier = Modifier
                        .background(Color(0x2200E5FF), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Whatshot, null, tint = PremiumGold, modifier = Modifier.size(12.dp))
                    Text("${listing.demandScore}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = listing.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = listing.location,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%,.0f", listing.price)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.White
                )
                
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = "View spec",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingDealRow(listing: Listing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Specific Icon badge
                val icon = when (listing.category.lowercase()) {
                    "car" -> Icons.Default.DirectionsCar
                    "bike" -> Icons.Default.TwoWheeler
                    "boat" -> Icons.Default.DirectionsBoat
                    "helicopter" -> Icons.Default.AirplanemodeActive
                    "house", "villa" -> Icons.Default.Home
                    "apartment" -> Icons.Default.Apartment
                    else -> Icons.Default.Business
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x192563EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = listing.category, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = listing.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 160.dp)
                        )
                        if (listing.isWatchlisted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Bookmark, "Watched", tint = PremiumGold, modifier = Modifier.size(12.dp))
                        }
                    }
                    Text(
                        text = "${listing.category}  |  ${listing.location}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.0f", listing.price)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Icon(Icons.Default.TrendingUp, null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                    Text("HOT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(start = 2.dp))
                }
            }
        }
    }
}

@Composable
fun MarketScreen(viewModel: MarketViewModel) {
    val listingsVal by viewModel.filteredListings.collectAsState()
    val category by viewModel.selectedCategory.collectAsState()
    val search by viewModel.searchQuery.collectAsState()
    var showRangeLimits by remember { mutableStateOf(false) }
    
    var minPriceStr by remember { mutableStateOf("") }
    var maxPriceStr by remember { mutableStateOf("") }

    val categories = listOf(
        "ALL" to "All",
        "VEHICLE" to "Vehicles",
        "PROPERTY" to "Properties",
        "BUSINESS" to "Businesses",
        "ITEM" to "Items"
    )

    Scaffold(
        bottomBar = { AppBottomNavigation("Market") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("market_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Search & Filters Header
            Column(
                modifier = Modifier
                    .background(SurfaceOffset)
                    .padding(16.dp)
            ) {
                Text("NEO TRANSACT DIRECTORY", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search vehicles, locations, code...", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = GlassCardBorder,
                            cursorColor = CyanGlow
                        ),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("market_search_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showRangeLimits = !showRangeLimits },
                        modifier = Modifier
                            .background(Color(0x1A00E5FF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                    ) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Dilaog filters", tint = CyanGlow)
                    }
                }

                // Pricing Range bounds panel
                AnimatedVisibility(visible = showRangeLimits) {
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text("PRICE THRESHOLOGY FILTER ($)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = minPriceStr,
                                onValueChange = { minPriceStr = it },
                                placeholder = { Text("Value", color = TextMuted) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = maxPriceStr,
                                onValueChange = { maxPriceStr = it },
                                placeholder = { Text("Value", color = TextMuted) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    minPriceStr = ""
                                    maxPriceStr = ""
                                    viewModel.setPriceRange(null, null)
                                }
                            ) {
                                Text("Clear", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val minVal = minPriceStr.toDoubleOrNull()
                                    val maxVal = maxPriceStr.toDoubleOrNull()
                                    viewModel.setPriceRange(minVal, maxVal)
                                }
                            ) {
                                Text("Apply Range")
                            }
                        }
                    }
                }

                // Horizontal tab categories selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { (id, label) ->
                        val isSel = category == id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) ElectricBlue else Color(0x19FFFFFF))
                                .border(1.dp, if (isSel) Color.Transparent else GlassCardBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setCategory(id) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("market_category_tab_${id.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Quick Actions Board Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030510))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${listingsVal.size} ACTIVE DEALS FOUND",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { viewModel.navigateTo("BuyerBoard") },
                        colors = ButtonDefaults.textButtonColors(contentColor = CyanGlow)
                    ) {
                        Icon(Icons.Default.ContentPaste, null, modifier = Modifier.size(14.getDpInt()))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buyer Requests", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo("ScammerShield") },
                        colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.PrivacyTip, null, modifier = Modifier.size(14.getDpInt()))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Shield Shield", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Infinite scrollable list of active contracts
            if (listingsVal.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching transactions logged.", color = TextSecondary)
                        Text("Try adjusting your keyword filter values.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listingsVal) { listing ->
                        ContractCardItem(listing = listing) {
                            viewModel.selectListing(listing)
                        }
                    }
                }
            }
        }
    }
}

fun Int.getDpInt(): androidx.compose.ui.unit.Dp = this.dp

@Composable
fun ContractCardItem(listing: Listing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = listing.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (listing.isSold) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "SOLD",
                                color = ErrorRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0x33EF4444), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${listing.category}  |  ${listing.location}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = "$${String.format("%,.0f", listing.price)}",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = listing.description,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = GlassCardBorder, modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Owner: ${listing.ownerName}", color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Verify Contract", color = CyanGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, null, tint = CyanGlow, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
fun ListingDetailScreen(viewModel: MarketViewModel) {
    val listing by viewModel.selectedListing.collectAsState()
    val user by viewModel.userProfile.collectAsState()
    var offerModeOpen by remember { mutableStateOf(false) }
    var userOfferAmt by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("detail_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // Header actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                }

                Row {
                    IconButton(onClick = { listing?.id?.let { viewModel.toggleWatchlist(it) } }) {
                        Icon(
                            imageVector = if (listing?.isWatchlisted == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist toggle",
                            tint = if (listing?.isWatchlisted == true) PremiumGold else Color.White
                        )
                    }

                    IconButton(onClick = { viewModel.navigateTo("ScammerShield") }) {
                        Icon(Icons.Default.Flag, "Report spam", tint = ErrorRed)
                    }
                }
            }

            if (listing == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selected listing is no longer available.", color = TextSecondary)
                }
            } else {
                val currentItem = listing!!
                
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    
                    // Categorized layout icon description sheet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.radialGradient(listOf(Color(0x3300E5FF), SurfaceOffset))),
                        contentAlignment = Alignment.Center
                    ) {
                        val bigIcon = when (currentItem.category.lowercase()) {
                            "car" -> Icons.Default.DirectionsCar
                            "bike" -> Icons.Default.TwoWheeler
                            "boat" -> Icons.Default.DirectionsBoat
                            "helicopter" -> Icons.Default.AirplanemodeActive
                            "house", "villa" -> Icons.Default.Home
                            "apartment" -> Icons.Default.Apartment
                            else -> Icons.Default.Business
                        }
                        Icon(imageVector = bigIcon, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(64.dp))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentItem.type,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            modifier = Modifier
                                .background(Color(0x1A1F51FF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Equalizer, null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DEMAND SCORE: ${currentItem.demandScore}/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                        }
                    }

                    Text(
                        text = currentItem.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Text(
                        text = "$${String.format("%,.2f", currentItem.price)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // DESCRIPTION BLOCK
                    Text(
                        text = "DETAILED DESCRIPTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow,
                        letterSpacing = 1.sp
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                    ) {
                        Text(
                            text = currentItem.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // TECHNICAL SPECIFICATIONS METADATA SUMMARY
                    Text(
                        text = "TECHNICAL ARCHIVE SPECIFICATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SpecRow("Contract Location", currentItem.location)
                            SpecRow("Authoritative Owner", currentItem.ownerName)

                            if (currentItem.type == "VEHICLE") {
                                SpecRow("Mileage Metric", "${currentItem.mileage ?: 0.0} Miles")
                                SpecRow("Registered Owners", "${currentItem.ownersCount ?: 1}")
                                SpecRow("Unique License Plate", currentItem.plateNumber ?: "UNREGISTERED")
                            }

                            if (currentItem.numberCode != null) {
                                SpecRow("CSV ID Tag", currentItem.numberCode)
                                SpecRow("Relationships Code", "VALIDATED AUTHORITATIVE")
                            }
                            
                            SpecRow("Contract timestamp", "June 22, 2026 UTC")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Negotiator Proposal panel
                    AnimatedVisibility(visible = offerModeOpen) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF030510))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("PROPOSE CUSTOM PRICING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                OutlinedTextField(
                                    value = userOfferAmt,
                                    onValueChange = { userOfferAmt = it },
                                    label = { Text("Offer Price ($)", color = TextSecondary) },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { offerModeOpen = false }) {
                                        Text("Cancel", color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val amt = userOfferAmt.toDoubleOrNull()
                                            if (amt != null) {
                                                viewModel.openDealRoomForListing(currentItem.id)
                                                // After entering we'll submit the counter proposal
                                                viewModel.makeNegotiationOffer(amt)
                                                offerModeOpen = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                    ) {
                                        Text("Send Counter Contract")
                                    }
                                }
                            }
                        }
                    }

                    // EXCLUSIVE CLIENT ACTIONS ROW
                    if (!currentItem.isSold) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentItem.ownerName != user?.name) {
                                OutlinedButton(
                                    onClick = { offerModeOpen = !offerModeOpen },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    border = BorderStroke(1.dp, TextMuted)
                                ) {
                                    Icon(Icons.Default.Edit, "Offer icon", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Make Offer", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.openDealRoomForListing(currentItem.id) },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(48.dp)
                                        .testTag("init_negotiation_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                ) {
                                    Icon(Icons.Default.Chat, "Chat icon", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Deal Room", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        viewModel.clearNotifications()
                                        viewModel.navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                                ) {
                                    Text("This is your own listing", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun CreateScreen(viewModel: MarketViewModel) {
    var type by remember { mutableStateOf("VEHICLE") } // VEHICLE, PROPERTY, BUSINESS, ITEM
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Eclipse Towers") }
    var desc by remember { mutableStateOf("") }
    
    // Sub categories
    var vehicleCategory by remember { mutableStateOf("Car") }
    var mileageStr by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    
    // CSV Specific tag
    var propertyCode by remember { mutableStateOf("") }

    val types = listOf("VEHICLE", "PROPERTY", "BUSINESS", "ITEM")
    val vehicleCats = listOf("Car", "Bike", "Boat", "Helicopter")

    Scaffold(
        bottomBar = { AppBottomNavigation("Create") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("create_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("GENERATE NEW TRANSACTION", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Publish Custom Contract", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)
            
            Text(
                text = "Deploying listings requires standard manual or CSV relationship validations to preserve secure ecosystem metrics.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Select liting type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                types.forEach { t ->
                    val isSel = type == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) ElectricBlue else Color(0x11FFFFFF))
                            .border(1.dp, if (isSel) Color.Transparent else GlassCardBorder, RoundedCornerShape(10.dp))
                            .clickable { type = t }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(t, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextSecondary)
                    }
                }
            }

            // Standard common fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Display Title", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_name_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = priceStr,
                onValueChange = { priceStr = it },
                label = { Text("Sale Pricing Value ($)", color = TextSecondary) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_price_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Physical Trade Location", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Clear Deal Terms & Conditions", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            // Dynamic fields based on type
            if (type == "VEHICLE") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("VEHICLE SPECIFICS", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vehicleCats.forEach { c ->
                        val isSel = vehicleCategory == c
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PremiumGold else Color(0x11FFFFFF))
                                .clickable { vehicleCategory = c }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) DarkMidnight else Color.White)
                        }
                    }
                }

                OutlinedTextField(
                    value = mileageStr,
                    onValueChange = { mileageStr = it },
                    label = { Text("Odometer Mileage (Miles)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = plateNumber,
                    onValueChange = { plateNumber = it },
                    label = { Text("Unique Number Plate Code", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            if (type == "PROPERTY") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("PROPERTY CSV VERIFICATION", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = propertyCode,
                    onValueChange = { propertyCode = it },
                    label = { Text("Property Unit code (e.g. #101)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Deployment Button
            Button(
                onClick = {
                    val priceVal = priceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotEmpty() && priceVal > 0) {
                        viewModel.addNewListing(
                            type = type,
                            name = name,
                            price = priceVal,
                            category = if (type == "VEHICLE") vehicleCategory else "Contract",
                            location = location,
                            description = desc,
                            mileage = mileageStr.toDoubleOrNull(),
                            plateNumber = plateNumber,
                            numberCode = if (type == "PROPERTY") propertyCode else null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_create_listing_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Publish, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deploy Live Contract", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
