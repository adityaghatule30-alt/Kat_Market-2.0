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

@Composable
fun CommunityScreen(viewModel: MarketViewModel) {
    val posts by viewModel.communityPosts.collectAsState()
    
    var activeTab by remember { mutableStateOf("FEED") } // FEED, ALLIANCE, EVENT, POLL
    var contentText by remember { mutableStateOf("") }
    
    // Poll creator
    var pollModeActive by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }

    val tabs = listOf(
        "FEED" to "Global Feed",
        "ALLIANCE" to "Alliances",
        "EVENT" to "Events",
        "POLL" to "Active Polls"
    )

    Scaffold(
        bottomBar = { AppBottomNavigation("Community") { viewModel.navigateTo(it) } },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("community_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Header Tabs block
            Column(
                modifier = Modifier
                    .background(SurfaceOffset)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("NEO DISCORD CENTRAL HUB", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("Ecosystem Hangouts", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { (cat, label) ->
                        val isSel = activeTab == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricBlue else Color(0x11FFFFFF))
                                .clickable { 
                                    activeTab = cat 
                                    pollModeActive = false
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextSecondary)
                        }
                    }
                }
            }

            // Post creation panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (pollModeActive) {
                        Text("CONSTRUCT SYSTEM POLL", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = pollQuestion,
                            onValueChange = { pollQuestion = it },
                            placeholder = { Text("What question would you like to poll?", color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = option1,
                                onValueChange = { option1 = it },
                                placeholder = { Text("Choice A", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = option2,
                                onValueChange = { option2 = it },
                                placeholder = { Text("Choice B", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { pollModeActive = false }) {
                                Text("Standard Post", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (pollQuestion.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty()) {
                                        viewModel.submitCommunityPoll(pollQuestion, option1, option2)
                                        pollQuestion = ""
                                        option1 = ""
                                        option2 = ""
                                        pollModeActive = false
                                        activeTab = "POLL"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold)
                            ) {
                                Text("Launch Poll", color = DarkMidnight, fontWeight = FontWeight.Bold)
                            }
                        }

                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini author tag
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x332563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = contentText,
                                onValueChange = { contentText = it },
                                placeholder = { Text("Discuss listings, post news, pitch contracts...", color = TextSecondary, fontSize = 13.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true
                            )
                            
                            IconButton(onClick = { pollModeActive = true }) {
                                Icon(Icons.Default.Poll, "Poll create", tint = PremiumGold)
                            }

                            IconButton(
                                onClick = {
                                    if (contentText.isNotEmpty()) {
                                        viewModel.submitCommunityPost(contentText, activeTab)
                                        contentText = ""
                                    }
                                },
                                modifier = Modifier
                                    .background(ElectricBlue, CircleShape)
                                    .size(34.dp)
                                    .testTag("community_post_submit")
                            ) {
                                Icon(Icons.Default.Send, "Send post", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Central Feed listings
            val filteredPosts = posts.filter { it.category == activeTab }
            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Chat, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active hub communications in $activeTab.", color = TextSecondary)
                        Text("Send the first dispatch above!", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 0.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredPosts) { post ->
                        CommunityPostCard(post = post, onVote = { index ->
                            viewModel.voteOnPoll(post.id, index)
                        }, onLike = {
                            viewModel.toggleLikePost(post.id)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, onVote: (Int) -> Unit, onLike: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Member signature row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF030510)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(post.authorRank, color = PremiumGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Active", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body text
            Text(post.content, color = TextPrimary, fontSize = 13.sp)

            // Dynamic POLL elements if any
            if (post.isPoll && post.pollOptionsJson != null && post.pollVotesJson != null) {
                Spacer(modifier = Modifier.height(14.dp))
                
                // Parsing mock arrays of poll options and counts
                val cleanedOptions = post.pollOptionsJson.replace("[", "").replace("]", "").replace("\"", "")
                val optionsList = cleanedOptions.split(",").map { it.trim() }

                val cleanedVotes = post.pollVotesJson.replace("[", "").replace("]", "").replace("\"", "")
                val votesList = cleanedVotes.split(",").map { it.trim().toIntOrNull() ?: 0 }

                val totalVotes = votesList.sum().coerceAtLeast(1)

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    optionsList.forEachIndexed { idx, opt ->
                        val voteCount = votesList.getOrElse(idx) { 0 }
                        val pct = ((voteCount.toFloat() / totalVotes.toFloat()) * 100f).toInt()
                        val hasVoted = post.userVotedOptionIndex == idx

                        // Animated vote percentage bar backing card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF030510))
                                .clickable { onVote(idx) }
                                .padding(2.dp)
                        ) {
                            // Progress backing
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(voteCount.toFloat() / totalVotes.toFloat())
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .then(
                                        if (hasVoted) {
                                            Modifier.background(Brush.horizontalGradient(listOf(ElectricBlue, CyanGlow)))
                                        } else {
                                            Modifier.background(Color(0x332563EB))
                                        }
                                    )
                            )

                            // Foregound text
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (hasVoted) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(opt, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text("$pct% ($voteCount votes)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action keys row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like button",
                        tint = if (post.isLiked) ErrorRed else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.likesCount} Likes", color = TextSecondary, fontSize = 11.sp)
                }

                Text("Source: Neo Central Hub", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun BuyerRequestsBoardScreen(viewModel: MarketViewModel) {
    val requests by viewModel.buyerRequests.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var itemTitle by remember { mutableStateOf("") }
    var budgetStr by remember { mutableStateOf("") }
    var detailsStr by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkMidnight,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("buyer_board_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = DarkMidnight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Request", color = DarkMidnight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Text("BUYER REQUEST DIRECTORY BOARD", fontSize = 11.sp, color = CyanGlow, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 14.dp))
            Text("Sourcing Board", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Black)
            
            Text(
                text = "Buyers list exact specs they are looking to buy. Sellers can respond immediately inside secure deal rooms to seal contract.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Form overlay if triggered
            if (showCreateDialog) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceOffset)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FILE NEW BUYER SOURCING TICKET", color = Color.White, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            label = { Text("What are you looking for? (e.g. BMW M5)", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = budgetStr,
                            onValueChange = { budgetStr = it },
                            label = { Text("Exact Budget ($)", color = TextSecondary) },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = detailsStr,
                            onValueChange = { detailsStr = it },
                            label = { Text("Specific Terms, color requirements, mileage limit...", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showCreateDialog = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val bVal = budgetStr.toDoubleOrNull() ?: 0.0
                                    if (itemTitle.isNotEmpty() && bVal > 0) {
                                        viewModel.postBuyerRequest(itemTitle, bVal, detailsStr)
                                        itemTitle = ""
                                        budgetStr = ""
                                        detailsStr = ""
                                        showCreateDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow)
                            ) {
                                Text("Publish Request", color = DarkMidnight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // List of active sourcing tickets
            if (requests.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No active purchase requests listed now.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(requests) { req ->
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
                                        Text(req.itemType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Posted by: ${req.requesterName}", color = TextSecondary, fontSize = 11.sp)
                                    }

                                    Text(
                                        text = "BUDGET: $${String.format("%,.0f", req.budget)}",
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = req.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status: Seeking responses",
                                        color = PremiumGold,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.pushNotification(
                                                "Sourcing Dispatch Initiated",
                                                "Contacting ${req.requesterName} regarding their request: ${req.itemType}.",
                                                "INFO"
                                            )
                                            // Prepopulate a deal system mock or alert
                                            viewModel.navigateTo("Market")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("I Have This", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealRoomScreen(viewModel: MarketViewModel) {
    val room by viewModel.activeDealRoom.collectAsState(null)
    val messages by viewModel.currentDealMessages.collectAsState(emptyList())
    val user by viewModel.userProfile.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    var offerSlider by remember { mutableStateOf(false) }
    var proposedOffer by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(room?.listingTitle ?: "Direct Trade Room", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "STATUS: ${room?.currentStatus ?: "PENDING"}",
                            color = when(room?.currentStatus) {
                                "ACCEPTED" -> SuccessGreen
                                "REJECTED" -> ErrorRed
                                else -> PremiumGold
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back icon", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceOffset),
                actions = {
                    if (room?.currentStatus != "ACCEPTED") {
                        IconButton(onClick = { offerSlider = !offerSlider }) {
                            Icon(Icons.Default.Label, "Counter offer", tint = PremiumGold)
                        }
                    }
                }
            )
        },
        containerColor = DarkMidnight,
        modifier = Modifier.testTag("deal_room_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Sub Offer proposing slider drawer
            AnimatedVisibility(visible = offerSlider) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030510))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("MAKE COUNTER TRANSACTION PROPOSAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        
                        OutlinedTextField(
                            value = proposedOffer,
                            onValueChange = { proposedOffer = it },
                            label = { Text("Proposal pricing value ($)", color = TextSecondary) },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { offerSlider = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val valPrice = proposedOffer.toDoubleOrNull()
                                    if (valPrice != null) {
                                        viewModel.makeNegotiationOffer(valPrice)
                                        proposedOffer = ""
                                        offerSlider = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumGold)
                            ) {
                                Text("Propose Counter", color = DarkMidnight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Top Status overview contract summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x99010515))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("LAST SECURED PROPOSAL", fontSize = 10.sp, color = TextSecondary)
                        Text("$${String.format("%,.2f", room?.lastOfferPrice ?: 0.0)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    if (room?.currentStatus != "ACCEPTED") {
                        Row {
                            TextButton(
                                onClick = { viewModel.rejectNegotiationDeal() },
                                colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                            ) {
                                Text("REJECT", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { viewModel.acceptNegotiationDeal() },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("ACCEPT", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "Accepted Deal Icon", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TRADE ARCHIVED", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Live messages List scroll
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(messages) { msg ->
                    val isUserMsg = msg.senderName == user?.name
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isUserMsg) Alignment.End else Alignment.Start
                    ) {
                        // Speaker badge
                        Text(
                            text = "${msg.senderName} • System Verified",
                            color = if (isUserMsg) CyanGlow else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        // Main dialogue card
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isUserMsg) 12.dp else 0.dp,
                                        bottomEnd = if (isUserMsg) 0.dp else 12.dp
                                    )
                                )
                                .background(if (isUserMsg) ElectricBlue else Color(0x19FFFFFF))
                                .padding(12.dp)
                        ) {
                            Text(msg.messageText, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Message compilation inputs footer
            if (room?.currentStatus != "ACCEPTED") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceOffset)
                        .padding(12.dp)
                        .testTag("deal_chat_input_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Write secured response...", color = TextSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("deal_chat_text_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GlassCardBorder,
                            unfocusedBorderColor = GlassCardBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotEmpty()) {
                                viewModel.sendChatMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .background(ElectricBlue, CircleShape)
                            .size(44.dp)
                            .testTag("deal_chat_send_button")
                    ) {
                        Icon(Icons.Default.Send, "Send button", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
