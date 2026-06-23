package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MarketDatabase
import com.example.data.model.*
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MarketDatabase.getDatabase(application)
    private val repository = MarketRepository(database.marketplaceDao(), application)

    // Streams of State
    val listings = repository.allListings.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val featuredListings = repository.featuredListings.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val trendingListings = repository.trendingListings.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val userProfile = repository.userProfileFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val dealRooms = repository.allDealRooms.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val buyerRequests = repository.allBuyerRequests.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val coinTickets = repository.allCoinTickets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val communityPosts = repository.allCommunityPosts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val scamReports = repository.allScamReports.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Payment matching flows
    val allOrders = repository.allOrders.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allPayments = repository.allPayments.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val pendingOrders = repository.pendingOrders.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentActiveOrder = MutableStateFlow<NekoOrder?>(null)
    val currentActiveOrder = _currentActiveOrder.asStateFlow()

    val watchlistListings = repository.allListings
        .map { list -> list.filter { it.isWatchlisted } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // UI state controllers
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL") // ALL, VEHICLE, PROPERTY, BUSINESS, ITEM
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _priceFilterMin = MutableStateFlow<Double?>(null)
    val priceFilterMin = _priceFilterMin.asStateFlow()
    
    private val _priceFilterMax = MutableStateFlow<Double?>(null)
    val priceFilterMax = _priceFilterMax.asStateFlow()

    private val _selectedListing = MutableStateFlow<Listing?>(null)
    val selectedListing = _selectedListing.asStateFlow()

    private val _activeDealRoomId = MutableStateFlow<Int?>(null)
    val activeDealRoomId = _activeDealRoomId.asStateFlow()

    val currentDealMessages: Flow<List<DealMessage>> = _activeDealRoomId.flatMapLatest { id ->
        if (id != null) repository.getMessagesForDeal(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Selected deal room details
    val activeDealRoom: Flow<DealRoom?> = _activeDealRoomId.map { id ->
        if (id != null) repository.allDealRooms.firstOrNull()?.find { it.id == id } else null
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Notifications state
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications = _notifications.asStateFlow()
    val unreadNotifications = _notifications.asStateFlow()

    // Screen navigation controller (custom navigation backstack)
    // Splash -> Onboarding -> Login -> Home -> Market -> Create -> Community -> Profile -> More -> Admin -> DealRoom -> Detail
    private val _currentScreen = MutableStateFlow("Splash")
    val currentScreen = _currentScreen.asStateFlow()

    private val _navigationHistory = mutableListOf<String>()

    // Init
    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            
            // Populate initial notifications
            _notifications.value = listOf(
                NotificationItem(
                    id = "init_1",
                    title = "Kat Market Rebuilt V2 Active",
                    message = "Redesigned with premium Dark cyber themes and robust scam mitigation.",
                    type = "INFO"
                ),
                NotificationItem(
                    id = "init_2",
                    title = "Welcome Bonus Coins Added",
                    message = "We appended 150 NEO coins to your balance for local testing.",
                    type = "PURCHASE"
                )
            )
        }
    }

    // --- NAVIGATION API ---
    fun navigateTo(screen: String) {
        if (_currentScreen.value != screen) {
            _navigationHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        if (_navigationHistory.isNotEmpty()) {
            _currentScreen.value = _navigationHistory.removeAt(_navigationHistory.size - 1)
        } else {
            _currentScreen.value = "Home"
        }
    }

    // --- SEARCH / FILTERS API ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setPriceRange(min: Double?, max: Double?) {
        _priceFilterMin.value = min
        _priceFilterMax.value = max
    }

    // Get filtered listings depending on inputs
    val filteredListings: StateFlow<List<Listing>> = combine(
        listings, searchQuery, selectedCategory, priceFilterMin, priceFilterMax
    ) { list, query, cat, min, max ->
        list.filter { item ->
            val matchesCategory = if (cat == "ALL") true else item.type.equals(cat, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                item.name.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.ownerName.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            }
            val matchesMinPrice = if (min == null) true else item.price >= min
            val matchesMaxPrice = if (max == null) true else item.price <= max
            matchesCategory && matchesQuery && matchesMinPrice && matchesMaxPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selection helper
    fun selectListing(listing: Listing?) {
        _selectedListing.value = listing
        if (listing != null) {
            navigateTo("ListingDetail")
        }
    }

    // --- COIN PURCHASE & PAYMENT VERIFICATION SYSTEM API ---
    fun selectPackageAndCreateOrder(amount: Double, coinAmount: Int) {
        viewModelScope.launch {
            val userProfile = repository.userProfileFlow.firstOrNull() ?: UserProfile()
            val userId = userProfile.email
            val order = repository.createOrder(userId, amount, coinAmount)
            _currentActiveOrder.value = order
            navigateTo("PaymentScreen")
        }
    }

    fun makeActiveOrder(order: NekoOrder) {
        _currentActiveOrder.value = order
    }

    fun activeOrderTimedOut(orderId: String) {
        viewModelScope.launch {
            repository.cancelOrExpireOrder(orderId)
            if (_currentActiveOrder.value?.id == orderId) {
                _currentActiveOrder.value = _currentActiveOrder.value?.copy(status = "EXPIRED")
            }
            pushNotification("Order Expired", "Order $orderId of ₹${_currentActiveOrder.value?.amount} has expired.", "ALERT")
        }
    }

    fun refreshActiveOrderStatus() {
        val active = _currentActiveOrder.value ?: return
        viewModelScope.launch {
            val isCompleted = repository.verifyOrderCompletion(active.id)
            if (isCompleted) {
                val updated = repository.getOrder(active.id)
                _currentActiveOrder.value = updated
                pushNotification(
                    "Payment Received",
                    "${active.coinAmount} Coins Added Successfully!",
                    "PURCHASE"
                )
                navigateTo("SuccessScreen")
            } else {
                pushNotification(
                    "Payment Status Refreshing",
                    "Awaiting KatPay Bridge signal for Order ID ${active.id} (₹${active.amount}).",
                    "INFO"
                )
            }
        }
    }

    fun simulateKatPayBridgeSms(amount: Double, utr: String, sender: String = "BOI") {
        viewModelScope.launch {
            val rawSms = "INR $amount credited via UPI Ref $utr"
            val result = repository.processIncomingPaymentSms(
                amount = amount,
                utr = utr,
                timestamp = System.currentTimeMillis(),
                sender = sender,
                rawSms = rawSms
            )
            when (result) {
                is com.example.data.repository.MatchingResult.Success -> {
                    pushNotification(
                        "Payment Auto-Verified",
                        "KatPay Bridge matched ₹$amount payment with Order ${result.order.id}. ${result.coinsAdded} Coins Credited!",
                        "PURCHASE"
                    )
                    // If the active screen represents this order, refresh it
                    if (_currentActiveOrder.value?.id == result.order.id) {
                        _currentActiveOrder.value = result.order.copy(status = "COMPLETED")
                        navigateTo("SuccessScreen")
                    }
                }
                is com.example.data.repository.MatchingResult.Duplicate -> {
                    pushNotification(
                        "Bridge Alert",
                        "Duplicate payment SMS detected with UTR $utr. Rejected to prevent double-crediting.",
                        "ALERT"
                    )
                }
                is com.example.data.repository.MatchingResult.NoMatchingOrder -> {
                    pushNotification(
                        "Unmatched Payment Ingested",
                        "₹$amount (UTR $utr) received, but no matching order was found. Logged in Unmatched Queue for manual assign.",
                        "ALERT"
                    )
                }
            }
        }
    }

    fun adminApproveOrder(orderId: String) {
        viewModelScope.launch {
            val success = repository.manualApproveOrder(orderId)
            if (success) {
                pushNotification("Order Approved", "Admin manually approved and credited order $orderId.", "PURCHASE")
                if (_currentActiveOrder.value?.id == orderId) {
                    val updated = repository.getOrder(orderId)
                    _currentActiveOrder.value = updated
                    navigateTo("SuccessScreen")
                }
            }
        }
    }

    fun adminRejectOrder(orderId: String) {
        viewModelScope.launch {
            repository.forceRejectOrder(orderId)
            pushNotification("Order Rejected", "Order $orderId was manually rejected by Admin.", "ALERT")
            if (_currentActiveOrder.value?.id == orderId) {
                val updated = repository.getOrder(orderId)
                _currentActiveOrder.value = updated
            }
        }
    }

    fun adminAssignPayment(paymentId: Int, orderId: String) {
        viewModelScope.launch {
            val success = repository.manualAssignPayment(paymentId, orderId)
            if (success) {
                pushNotification("Payment Manually Assigned", "Payment #$paymentId was manually linked to Order $orderId. Coins Credited!", "PURCHASE")
                if (_currentActiveOrder.value?.id == orderId) {
                    val updated = repository.getOrder(orderId)
                    _currentActiveOrder.value = updated
                    navigateTo("SuccessScreen")
                }
            }
        }
    }

    // --- AUTHENTICATION & ONBOARDING API ---
    fun performGoogleLogin(
        name: String = "Aditya G",
        email: String = "adityaghatule40@gmail.com",
        avatarUrl: String? = null
    ) {
        viewModelScope.launch {
            repository.simulateGoogleLogin(name, email, avatarUrl)
            pushNotification("Google Sync Complete", "Synced profile for $email. Verified Trader level loaded.", "VERIFY")
            navigateTo("Home")
        }
    }

    fun performEmailLogin(email: String) {
        viewModelScope.launch {
            repository.simulateEmailLogin(email)
            pushNotification("Account Authenticated", "Injected account credentials for $email.", "INFO")
            navigateTo("Home")
        }
    }

    fun logOut() {
        viewModelScope.launch {
            database.marketplaceDao().clearAllListings() // Reset to force re-seed
            navigateTo("Login")
        }
    }

    // --- WATCHLIST ---
    fun toggleWatchlist(listingId: Int) {
        viewModelScope.launch {
            repository.toggleWatchlist(listingId)
            
            val updatedListings = listings.value
            val item = updatedListings.find { it.id == listingId }
            if (item != null) {
                if (!item.isWatchlisted) {
                    pushNotification("Added to Watchlist", "${item.name} will now alert you upon price changes.", "ALERT")
                } else {
                    pushNotification("Removed Watchlist", "Stopped tracking ${item.name}.", "INFO")
                }
            }
        }
    }

    // --- CREATE LISTINGS ---
    fun addNewListing(
        type: String,
        name: String,
        price: Double,
        category: String,
        location: String,
        description: String,
        // Optional variables
        mileage: Double? = null,
        ownersCount: Int? = null,
        plateNumber: String? = null,
        numberCode: String? = null
    ) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val newListing = Listing(
                type = type,
                name = name,
                price = price,
                category = category,
                location = location,
                ownerName = profile.name,
                description = description,
                mileage = mileage,
                ownersCount = ownersCount,
                plateNumber = plateNumber,
                numberCode = numberCode,
                isFeatured = price > 5000000,
                demandScore = (50..85).random()
            )
            repository.createNewListing(newListing)
            pushNotification("Listing Created", "Your item '$name' is now live on Kat Market!", "INFO")
            navigateTo("Market")
        }
    }

    // --- DEAL ROOM PROCESS ---
    fun openDealRoomForListing(listingId: Int) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val roomId = repository.initiateDeal(profile.name, listingId)
            if (roomId > 0) {
                _activeDealRoomId.value = roomId
                navigateTo("DealRoom")
            }
        }
    }

    fun viewDealRoom(roomId: Int) {
        _activeDealRoomId.value = roomId
        navigateTo("DealRoom")
    }

    fun sendChatMessage(text: String) {
        val roomId = _activeDealRoomId.value ?: return
        val profile = userProfile.value ?: UserProfile()
        viewModelScope.launch {
            repository.sendDealMessage(roomId, profile.name, text)
        }
    }

    fun makeNegotiationOffer(amount: Double) {
        val roomId = _activeDealRoomId.value ?: return
        val profile = userProfile.value ?: UserProfile()
        viewModelScope.launch {
            val text = "💡 OFFER MADE: Proposed price: $${String.format("%,.2f", amount)}"
            repository.sendDealMessage(roomId, profile.name, text, offerAmount = amount)
            pushNotification("Offer Dispatched", "Transmitted an offer of $${String.format("%,.0f", amount)}.", "OFFER")
        }
    }

    fun acceptNegotiationDeal() {
        val roomId = _activeDealRoomId.value ?: return
        viewModelScope.launch {
            repository.updateDealStatus(roomId, "ACCEPTED")
            pushNotification("Trade Sealed!", "The negotiation has been accepted and trade records are archived.", "VERIFY")
            navigateBack()
        }
    }

    fun rejectNegotiationDeal() {
        val roomId = _activeDealRoomId.value ?: return
        viewModelScope.launch {
            repository.updateDealStatus(roomId, "REJECTED")
            pushNotification("Offer Rejected", "The proposta was turned down.", "INFO")
        }
    }

    // --- COIN SHOP BUYING FLOW ---
    fun submitCoinsPurchaseTicket(pkgName: String, coins: Int, price: Double, txId: String) {
        viewModelScope.launch {
            repository.purchaseCoins(pkgName, coins, price, txId)
            pushNotification(
                title = "Payment Ticket Dispatched",
                message = "UPI Ticket ID $txId is pending Owner verification.",
                type = "PURCHASE"
            )
            navigateTo("Profile") // Returns profile/tickets page to view status
        }
    }

    // OWNER / ADMIN TICKET ACCESS
    fun approveCoinsTicket(ticketId: Int) {
        viewModelScope.launch {
            repository.resolveCoinTicket(ticketId, approve = true, "Transaction manually verified by Admin Panel.")
            pushNotification("NEO Coins credited", "Your wallet balance was updated.", "PURCHASE")
        }
    }

    fun rejectCoinsTicket(ticketId: Int, reason: String) {
        viewModelScope.launch {
            repository.resolveCoinTicket(ticketId, approve = false, "Rejected: $reason")
            pushNotification("Ticket Declined", "Purchase ticket rejected by moderator: $reason", "ALERT")
        }
    }

    // --- COMMUNITY FEED API ---
    fun submitCommunityPost(content: String, category: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val newPost = CommunityPost(
                authorName = profile.name,
                authorRank = profile.reputationRank,
                content = content,
                category = category
            )
            repository.createCommunityPost(newPost)
            pushNotification("Post Dispatched", "Your content has been added to the $category channel.", "INFO")
        }
    }

    fun submitCommunityPoll(question: String, option1: String, option2: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val oJson = "[\"$option1\",\"$option2\"]"
            val vJson = "[\"0\",\"0\"]"
            val newPost = CommunityPost(
                authorName = profile.name,
                authorRank = profile.reputationRank,
                content = "📊 POLL: $question",
                category = "POLL",
                isPoll = true,
                pollQuestion = question,
                pollOptionsJson = oJson,
                pollVotesJson = vJson
            )
            repository.createCommunityPost(newPost)
            pushNotification("Poll Initialized", "Your poll is active in discussion boards.", "INFO")
        }
    }

    fun voteOnPoll(postId: Int, selectedOptionIndex: Int) {
        viewModelScope.launch {
            repository.voteOnPostPoll(postId, selectedOptionIndex)
        }
    }

    fun toggleLikePost(postId: Int) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
        }
    }

    // --- SCAM REPORT API ---
    fun fileScamReport(listingId: Int?, listingTitle: String?, offender: String?, reason: String, details: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val newReport = ScamReport(
                listingId = listingId,
                listingTitle = listingTitle,
                offenderName = offender,
                reporterName = profile.name,
                reason = reason,
                details = details
            )
            repository.submitScamReport(newReport)
            pushNotification("Scam Report Logged", "Our trust moderators are investigating '$offender'. Ticket logged.", "ALERT")
        }
    }

    // --- BUYER REQ BOARD API ---
    fun postBuyerRequest(item: String, budget: Double, desc: String) {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfile()
            val req = BuyerRequest(
                requesterName = profile.name,
                itemType = item,
                budget = budget,
                description = desc
            )
            repository.createBuyerRequest(req)
            pushNotification("Buyer Request Filed", "Seeking responses for '$item' under $${String.format("%,.0f", budget)}.", "INFO")
        }
    }

    // --- NOTIFICATIONS WRAPPER ---
    fun pushNotification(title: String, message: String, type: String = "INFO") {
        val item = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            type = type
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // --- ADMINISTRATIVE ACTIONS & SECURITY CONTROLS ---
    fun submitScamShieldReport(suspect: String, reason: String) {
        fileScamReport(null, null, suspect, "USER_FRAUD", reason)
    }

    fun closeScamShieldReport(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateScamStatus(id, status)
            pushNotification("Incident Solved", "Anti-abuse action '$status' applied to ticket ID #$id.", "ALERT")
        }
    }

    fun approveUpiPaymentSlip(id: Int) {
        approveCoinsTicket(id)
    }

    fun rejectUpiPaymentSlip(id: Int) {
        rejectCoinsTicket(id, "Incorrect Transaction Hash Code.")
    }

    fun submitUpiPaymentTicket(pkgName: String, coins: Int, txnId: String) {
        submitCoinsPurchaseTicket(pkgName, coins, 0.0, txnId)
    }

    fun selectDealRoom(room: DealRoom) {
        _activeDealRoomId.value = room.id
        navigateTo("DealRoom")
    }

    fun performLogout() {
        logOut()
    }

    // --- SUPPLIER DEALS ENGINE ---
    val allSupplierDeals = repository.allSupplierDeals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun submitSupplierDeal(gameName: String, dealCode: String, videoUrl: String, reputationDelta: Int) {
        viewModelScope.launch {
            val email = userProfile.value?.email ?: "adityaghatule40@gmail.com"
            repository.createSupplierDeal(email, gameName, dealCode, videoUrl, reputationDelta)
            pushNotification(
                title = "Deal Uploaded Successfully",
                message = "Your $gameName deal (#$dealCode) was submitted with YouTube proof. Awaiting admin review.",
                type = "INFO"
            )
        }
    }

    fun approveSupplierDeal(dealId: Int, valToIncrease: Int) {
        viewModelScope.launch {
            repository.updateSupplierDealStatus(dealId, "APPROVED", valToIncrease)
            pushNotification(
                title = "Supplier Deal Approved",
                message = "Admin confirmed deal #$dealId. Reputation increased by +$valToIncrease",
                type = "VERIFY"
            )
        }
    }

    fun rejectSupplierDeal(dealId: Int) {
        viewModelScope.launch {
            repository.updateSupplierDealStatus(dealId, "REJECTED", 0)
            pushNotification(
                title = "Supplier Deal Rejected",
                message = "Admin rejected deal #$dealId.",
                type = "ALERT"
            )
        }
    }
}

