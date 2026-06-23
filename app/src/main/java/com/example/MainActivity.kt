package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MarketViewModel
import com.example.ui.screens.*
import com.example.ui.theme.DarkMidnight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkMidnight
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "main_screen_nav"
                    ) { screen ->
                        when (screen) {
                            "Splash" -> SplashScreen(viewModel)
                            "Onboarding" -> OnboardingScreen(viewModel)
                            "Login" -> LoginScreen(viewModel)
                            "Home" -> HomeScreen(viewModel)
                            "Market" -> MarketScreen(viewModel)
                            "Create" -> CreateScreen(viewModel)
                            "Community" -> CommunityScreen(viewModel)
                            "Profile" -> ProfileScreen(viewModel)
                            "More" -> MoreScreen(viewModel)
                            "Admin" -> AdminPanelScreen(viewModel)
                            "ScammerShield" -> ScammerShieldScreen(viewModel)
                            "Notifications" -> NotificationsScreen(viewModel)
                            "MarketIntel" -> MarketIntelScreen(viewModel)
                            "ListingDetail" -> ListingDetailScreen(viewModel)
                            "DealRoom" -> DealRoomScreen(viewModel)
                            "BuyerBoard" -> BuyerRequestsBoardScreen(viewModel)
                            "CoinStore" -> NekoCoinStoreScreen(viewModel)
                            "PaymentScreen" -> NekoPaymentScreen(viewModel)
                            "SuccessScreen" -> NekoSuccessScreen(viewModel)
                            "AdminPayments" -> NekoAdminPaymentsScreen(viewModel)
                            else -> HomeScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
