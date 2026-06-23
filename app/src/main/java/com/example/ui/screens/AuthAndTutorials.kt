package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MarketViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: MarketViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    LaunchedEffect(Unit) {
        delay(2500) // Beautiful cinematic intro timing
        viewModel.navigateTo("Onboarding")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkMidnight, DeepNavy)
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-tech decorative ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .border(2.dp, Brush.linearGradient(listOf(ElectricBlue, CyanGlow)), RoundedCornerShape(40.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Procedural neon loading ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                        .border(
                            width = 4.dp,
                            brush = Brush.sweepGradient(listOf(Color.Transparent, ElectricBlue, CyanGlow)),
                            shape = RoundedCornerShape(32.dp)
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1782197676591),
                    contentDescription = "App Logo mascot",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "KAT MARKET NEO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = Color.White
                ),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "V2",
                    color = PremiumGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier
                        .background(Color(0x33FFD700), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NEO SYSTEMS LTD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
            
            CircularProgressIndicator(
                color = CyanGlow,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun OnboardingScreen(viewModel: MarketViewModel) {
    var step by remember { mutableStateOf(1) }
    
    val totalSteps = 4
    val bgGradients = listOf(
        listOf(DeepNavy, DarkMidnight),
        listOf(DarkMidnight, SurfaceOffset),
        listOf(SurfaceOffset, DeepNavy),
        listOf(DeepNavy, DarkMidnight)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradients[step - 1]))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_screen_container")
    ) {
        // Step layout content
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Step marker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    for (i in 1..totalSteps) {
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(if (i == step) 28.dp else 12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i == step) ElectricBlue else TextMuted)
                                .padding(horizontal = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                TextButton(
                    onClick = { viewModel.navigateTo("Login") },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Skip Tutorial")
                }
            }

            // Central Visual Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "tutorial_step"
                ) { targetStep ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        when (targetStep) {
                            1 -> {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .background(Color(0x1A00E5FF), RoundedCornerShape(32.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = CyanGlow,
                                        modifier = Modifier.size(72.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Premium Marketplace",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Explore elite listings: sports cars, helicopters, properties, and profitable businesses verified at source.",
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            2 -> {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .background(Color(0x1AFFD700), RoundedCornerShape(32.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = PremiumGold,
                                        modifier = Modifier.size(72.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "NEO Coins Ecosystem",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Purchase stable coins inside the store using secure instant UPI receipts, unlock premium rankings, and complete audits securely.",
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            3 -> {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .background(Color(0x1A10B981), RoundedCornerShape(32.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(72.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Unified Reputation Ranks",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Build trade profile from Rookie to Market Legend! Every successful agreement boosts verification status automatically and locks trust.",
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .background(Color(0x1AEF4444), RoundedCornerShape(32.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(72.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Scammer Shield",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Our neural integrity algorithms flag duplicate listing frauds and scam logs. Feel protected with live admin moderation logs.",
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Lower Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, TextMuted)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Button(
                    onClick = {
                        if (step < totalSteps) {
                            step++
                        } else {
                            viewModel.navigateTo("Login")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("onboarding_next_button")
                ) {
                    Text(if (step == totalSteps) "Get Started" else "Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MarketViewModel) {
    var email by remember { mutableStateOf("adityaghatule40@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var showLogs by remember { mutableStateOf(false) }

    var showGoogleSelector by remember { mutableStateOf(false) }
    var selectorStep by remember { mutableStateOf(1) } // 1: Chooser, 2: New Email, 3: New Name, 4: Verifying Loader
    var customEmail by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    var googleError by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf("") }

    val savedGoogleAccounts = remember {
        mutableStateListOf(
            Pair("Aditya G", "adityaghatule40@gmail.com"),
            Pair("Eco Ranger", "ecosystem.member@gmail.com")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkMidnight, DeepNavy)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .testTag("login_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Header Card
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x242563EB))
                    .border(1.dp, GlassCardBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1782197676591),
                    contentDescription = null,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Neo Market",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Redesigned, multi-auth secure trading gateway",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Glassmorphic Login Block
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceOffset),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "SECURE ACCOUNT PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = GlassCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = CyanGlow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input_field"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, "Email icon", tint = TextSecondary) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Security Password", color = TextSecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = GlassCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = CyanGlow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input_field"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, "Lock icon", tint = TextSecondary) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email login button
                    Button(
                        onClick = {
                            if (email.contains("@")) {
                                viewModel.performEmailLogin(email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("email_login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log In via Email", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Divider: Or login via google
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = GlassCardBorder)
                        Text(
                            text = " OR ",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = GlassCardBorder)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google Premium Dark-Themed Brand-Compliant button
                    Button(
                        onClick = { 
                            showGoogleSelector = true
                            selectorStep = 1
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131314)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(1.dp, Color(0xFF303030), RoundedCornerShape(12.dp))
                            .testTag("google_login_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign in with Google",
                            color = Color(0xFFE3E3E3),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Simulation Session log toggler
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1100E5FF))
                    .clickable { showLogs = !showLogs }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Terminal, "Session tracker icon", tint = CyanGlow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Show Secure Device Logs", fontSize = 12.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (showLogs) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = CyanGlow
                )
            }

            AnimatedVisibility(visible = showLogs) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030510)),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text("SYSTEM SECURITY AUDS LOGS", fontSize = 10.sp, color = PremiumGold, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Device Host: Android VirtEmulator API ${Build.VERSION.SDK_INT}", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("• Client Signature: com.aistudio.katmarketneo.kxmpzq", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("• OAuth Token: GOOGLE_ONE_TAP_MOCK_ACTIVE", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("• Encryption protocol: RSA-4096 Secure Socket Layer", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("• Anti-abuse block: Coins purchase UPI screen capture detector is ON", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        Text("• Scam shield status: Authoritative CSV validation loaded (100% Relationship integrity)", fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }
            }
        }
    }

    if (showGoogleSelector) {
        androidx.compose.ui.window.Dialog(onDismissRequest = {
            if (selectorStep != 4) {
                showGoogleSelector = false
                selectorStep = 1
            }
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight()
                    .border(1.dp, Color(0xFFDADCE0), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                var loaderProgressText by remember { mutableStateOf("Connecting to Google Auth API...") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Logo Header
                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 26.sp)
                        Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 26.sp)
                        Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Black, fontSize = 26.sp)
                        Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 26.sp)
                        Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Black, fontSize = 26.sp)
                        Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 26.sp)
                    }

                    when (selectorStep) {
                        1 -> {
                            Text(
                                "Choose an account",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                            Text(
                                "to continue to Kat Market Neo",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            HorizontalDivider(color = Color(0xFFEEEEEE))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                savedGoogleAccounts.forEach { account ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedEmail = account.second
                                                selectedName = account.first
                                                selectorStep = 4 // loader
                                            }
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = if (account.second == "adityaghatule40@gmail.com") PremiumGold else ElectricBlue,
                                                    shape = RoundedCornerShape(18.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = account.first.take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = account.first,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = account.second,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        if (account.second == "adityaghatule40@gmail.com") {
                                            Icon(
                                                imageVector = Icons.Default.VerifiedUser,
                                                contentDescription = "Market Owner",
                                                tint = PremiumGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = Color(0xFFEEEEEE))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            customEmail = ""
                                            customName = ""
                                            googleError = ""
                                            selectorStep = 2
                                        }
                                        .padding(vertical = 14.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add account",
                                        tint = Color(0xFF1A73E8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Use another account / add ID",
                                        fontSize = 13.sp,
                                        color = Color(0xFF1A73E8),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "To switch systems, enter any custom Google address to load corresponding ecosystem credentials.",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        2 -> {
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                            Text(
                                text = "Use your custom Google account ID",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = customEmail,
                                onValueChange = {
                                    customEmail = it
                                    googleError = ""
                                },
                                placeholder = { Text("your.name@gmail.com", color = Color.LightGray) },
                                label = { Text("Email or phone", color = Color.Gray) },
                                singleLine = true,
                                isError = googleError.isNotEmpty(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (googleError.isNotEmpty()) {
                                Text(
                                    text = googleError,
                                    color = ErrorRed,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { selectorStep = 1 }) {
                                    Text("Back", color = Color(0xFF1A73E8))
                                }

                                Button(
                                    onClick = {
                                        if (customEmail.contains("@") && customEmail.contains(".")) {
                                            selectorStep = 3
                                        } else {
                                            googleError = "Enter a valid email address (e.g., mail@gmail.com)."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
                                ) {
                                    Text("Next", color = Color.White)
                                }
                            }
                        }

                        3 -> {
                            Text(
                                text = "Ecosystem Profile details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                            Text(
                                text = "Provide display name for secure trades",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = customName,
                                onValueChange = { customName = it },
                                placeholder = { Text("e.g. Satoshi Nakamoto", color = Color.LightGray) },
                                label = { Text("Ecosystem Full Name", color = Color.Gray) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { selectorStep = 2 }) {
                                    Text("Back", color = Color(0xFF1A73E8))
                                }

                                Button(
                                    onClick = {
                                        val finalName = if (customName.trim().isNotEmpty()) customName.trim() else customEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                                        if (!savedGoogleAccounts.any { it.second == customEmail }) {
                                            savedGoogleAccounts.add(Pair(finalName, customEmail))
                                        }
                                        selectedEmail = customEmail
                                        selectedName = finalName
                                        selectorStep = 4 // loader
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
                                ) {
                                    Text("Create & Sign In", color = Color.White)
                                }
                            }
                        }

                        4 -> {
                            LaunchedEffect(Unit) {
                                delay(900)
                                loaderProgressText = "Requesting secure token handshake..."
                                delay(900)
                                loaderProgressText = "Verifying OAuth signature token..."
                                delay(800)
                                viewModel.performGoogleLogin(selectedName, selectedEmail)
                                showGoogleSelector = false
                                selectorStep = 1
                            }

                            CircularProgressIndicator(
                                color = Color(0xFF4285F4),
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(bottom = 12.dp)
                            )

                            Text(
                                text = "Securing OAuth Connection",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Text(
                                text = loaderProgressText,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
