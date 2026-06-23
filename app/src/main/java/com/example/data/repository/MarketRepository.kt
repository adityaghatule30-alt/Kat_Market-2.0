package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.MarketplaceDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MarketRepository(
    private val dao: MarketplaceDao,
    private val context: Context
) {

    // Streams
    val allListings: Flow<List<Listing>> = dao.getAllListings()
    val featuredListings: Flow<List<Listing>> = dao.getFeaturedListings()
    val trendingListings: Flow<List<Listing>> = dao.getTrendingListings()
    val userProfileFlow: Flow<UserProfile?> = dao.getUserProfileFlow()
    val allDealRooms: Flow<List<DealRoom>> = dao.getAllDealRooms()
    val allBuyerRequests: Flow<List<BuyerRequest>> = dao.getAllBuyerRequests()
    val allCoinTickets: Flow<List<CoinTicket>> = dao.getAllCoinTickets()
    val allCommunityPosts: Flow<List<CommunityPost>> = dao.getAllCommunityPosts()
    val allScamReports: Flow<List<ScamReport>> = dao.getAllScamReports()

    fun getListingsByType(type: String): Flow<List<Listing>> = dao.getListingsByType(type)
    fun getMessagesForDeal(dealRoomId: Int): Flow<List<DealMessage>> = dao.getMessagesForDeal(dealRoomId)

    // Seeding check and execution
    suspend fun checkAndSeedDatabase() {
        withContext(Dispatchers.IO) {
            val existingProg = dao.getUserProfile()
            if (existingProg == null) {
                Log.d("MarketRepository", "Seeding database from CSVs and templates...")
                
                // 1. Create Default Profile
                val defaultProfile = UserProfile()
                dao.insertProfile(defaultProfile)

                // 2. Parse CSV Databases and seed
                val properties = parsePropertiesCsv()
                val apartments = parseApartmentsCsv()
                val businesses = parseBusinessesCsv()
                
                // Convert Property/Apartment/Business models to Listings
                val seededListings = mutableListOf<Listing>()
                
                properties.forEach {
                    seededListings.add(
                        Listing(
                            type = "PROPERTY",
                            name = "${it.type} ${it.number}",
                            price = it.value,
                            category = it.type, // House, Villa
                            location = it.location,
                            ownerName = it.owner,
                            description = "Authoritative luxury property in ${it.location}. Exclusive listings with beautiful architecture, premium amenities, high security security gating.",
                            numberCode = it.number,
                            originalValue = it.value,
                            imageUrl = null, // UI will allocate procedurally or show standard images
                            isFeatured = it.value > 10000000,
                            isTrending = it.value in 2000000.0..10000000.0,
                            demandScore = (70..95).random()
                        )
                    )
                }

                apartments.forEach {
                    seededListings.add(
                        Listing(
                            type = "PROPERTY",
                            name = "Apartment ${it.number}",
                            price = it.value,
                            category = "Apartment",
                            location = it.location,
                            ownerName = it.owner,
                            description = "Authoritative apartment unit ${it.number} located in ${it.location}. Premium urban living, close-by transit services, sky terrace view.",
                            numberCode = it.number,
                            originalValue = it.value,
                            imageUrl = null,
                            isFeatured = it.value > 3000000,
                            isTrending = true,
                            demandScore = (65..90).random()
                        )
                    )
                }

                businesses.forEach {
                    seededListings.add(
                        Listing(
                            type = "BUSINESS",
                            name = it.name,
                            price = it.value,
                            category = it.category,
                            location = it.location,
                            ownerName = it.owner,
                            description = "Profitable enterprise: ${it.name} (${it.category}), operating at ${it.location}. Solid revenue sheets, high customer retention rate.",
                            numberCode = null,
                            originalValue = it.value,
                            imageUrl = null,
                            isFeatured = it.value > 10000000,
                            isTrending = it.value < 5000000,
                            demandScore = (80..99).random()
                        )
                    )
                }

                // 3. Seed Default Vehicles
                seededListings.addAll(getDefaultVehicles())

                // 4. Seed Default Items
                seededListings.addAll(getDefaultItems())

                // Insert Listings
                dao.insertListings(seededListings)

                // 5. Seed community posts
                seedCommunityPosts()

                // 6. Seed buyer requests
                seedBuyerRequests()
                
                Log.d("MarketRepository", "Seeding complete with ${seededListings.size} listings.")
            }
        }
    }

    private fun getDefaultVehicles(): List<Listing> {
        return listOf(
            Listing(
                type = "VEHICLE",
                name = "Pegassi Osiris",
                price = 1950000.0,
                category = "Car",
                location = "Rockford Hills",
                ownerName = "Michael De Santa",
                description = "Breathtaking electric gullwing hypercar. Unmatched acceleration, active aero spoiler, luxury carbon weave chassis. Spotless condition.",
                mileage = 450.0,
                ownersCount = 1,
                plateNumber = "M1CHA3L",
                isFeatured = true,
                isTrending = true,
                demandScore = 96
            ),
            Listing(
                type = "VEHICLE",
                name = "Bati 801RR",
                price = 15000.0,
                category = "Bike",
                location = "Sandy Shores",
                ownerName = "Trevor Philips",
                description = "Street-legal track racing superbike. Custom military-grade paint, exhaust tuning, high-speed gear ratio. Fast and loud.",
                mileage = 5400.0,
                ownersCount = 2,
                plateNumber = "TReV0R",
                isFeatured = false,
                isTrending = true,
                demandScore = 88
            ),
            Listing(
                type = "VEHICLE",
                name = "Shitzu Jetmax",
                price = 299000.0,
                category = "Boat",
                location = "Vespucci Marina",
                ownerName = "Franklin Clinton",
                description = "Luxury fiberglass powerboat. High-performance dual V8 outboards, premium leather deck lounge, integrated sound system.",
                mileage = 85.0,
                ownersCount = 1,
                plateNumber = "F2ANKL1N",
                isFeatured = true,
                isTrending = false,
                demandScore = 75
            ),
            Listing(
                type = "VEHICLE",
                name = "Buckingham Luxor",
                price = 1625000.0,
                category = "Helicopter",
                location = "LSIA Hangar 3",
                ownerName = "Devin Weston",
                description = "Ultra Elite executive private helicopter. Gold accents, luxury conference lounge inside, champagne bar, sound-proof windows.",
                mileage = 320.0,
                ownersCount = 1,
                plateNumber = "DEv1N1",
                isFeatured = true,
                isTrending = false,
                demandScore = 92
            )
        )
    }

    private fun getDefaultItems(): List<Listing> {
        return listOf(
            Listing(
                type = "ITEM",
                name = "Vintage Diamond Watch",
                price = 45000.0,
                category = "Accessory",
                location = "Pillbox Hill",
                ownerName = "Lester Crest",
                description = "Solid platinum diamond-crusted mechanical watch. 1978 hand-wound movement, certified genuine appraisal certificate included.",
                isFeatured = false,
                isTrending = true,
                demandScore = 72
            ),
            Listing(
                type = "ITEM",
                name = "Custom Carbon Rifle Replica",
                price = 8500.0,
                category = "Collectible",
                location = "Sandy Shores",
                ownerName = "Trevor Philips",
                description = "Showpiece carbon fiber air-powered replica. Stunning build, non-firing, wall-mountable, signed by custom forge builders.",
                isFeatured = false,
                isTrending = false,
                demandScore = 55
            )
        )
    }

    private suspend fun seedCommunityPosts() {
        val posts = listOf(
            CommunityPost(
                authorName = "MarketMaster",
                authorRank = "Elite Trader",
                content = "🚨 REDESIGN COMPLETED: Kat Market Neo V2 is officially LIVE! Enjoy improved security systems, seamless deal rooms, and the all-new Scammer Shield dashboard.",
                category = "FEED",
                likesCount = 42,
                isLiked = true
            ),
            CommunityPost(
                authorName = "Trevor Philips",
                authorRank = "Market Legend",
                content = "Looking for gunsmiths to join the TP Industries Alliance! High payouts, free flight transport, and unlimited premium drinks at Vanilla Unicorn.",
                category = "ALLIANCE",
                likesCount = 19
            ),
            CommunityPost(
                authorName = "Aditya G",
                authorRank = "Trusted Trader",
                content = "Recruiting active high-tier sellers for the Neo Syndicate Alliance. Must have completed at least 5 certified verified trades.",
                category = "RECRUITMENT",
                likesCount = 28
            ),
            CommunityPost(
                authorName = "Lester Crest",
                authorRank = "Elite Trader",
                content = "🎲 THE BIG AUCTION EVENT: We're hosting a premier auction next Saturday at Rockford Hills. Rare classic sports cars and elite properties up for bidding. Be there!",
                category = "EVENT",
                likesCount = 55
            ),
            CommunityPost(
                authorName = "Franklin Clinton",
                authorRank = "Trusted Trader",
                content = "Wait, what's everyone's favorite ride category for daily driving? Vote now, let's see where the demand score is heading!",
                category = "POLL",
                isPoll = true,
                pollQuestion = "Favorite Vehicle Class?",
                pollOptionsJson = "[\"Supercars\",\"Bikes\",\"Boats\",\"Helis\"]",
                pollVotesJson = "[\"124\",\"42\",\"14\",\"8\"]"
            )
        )
        posts.forEach { dao.insertCommunityPost(it) }
    }

    private suspend fun seedBuyerRequests() {
        val requests = listOf(
            BuyerRequest(
                requesterName = "Aditya G",
                itemType = "BMW M5 F90 LCI",
                budget = 125000.0,
                description = "Looking for a clean BMW M5, prefer Snapper Rocks Blue metallic. Low mileage, stock setup, certified service record.",
                responsesCount = 3
            ),
            BuyerRequest(
                requesterName = "Lamar Davis",
                itemType = "House #42 near Forum Drive",
                budget = 350000.0,
                description = "Willing to pay premium value for House #42. Contact me instantly inside Deal Room if you are the current owner.",
                responsesCount = 1
            ),
            BuyerRequest(
                requesterName = "Donald Love",
                itemType = "Luxury Helicopter",
                budget = 2500000.0,
                description = "Seeking Buckingham SuperVolito or Swift Deluxe with premium interior. Must have landing clearance.",
                responsesCount = 0
            )
        )
        requests.forEach { dao.insertBuyerRequest(it) }
    }

    // --- CSV PARSING UTILITIES ---
    private fun parsePropertiesCsv(): List<CsvProperty> {
        val list = mutableListOf<CsvProperty>()
        try {
            val reader = BufferedReader(InputStreamReader(context.assets.open("properties.csv")))
            var line: String? = reader.readLine() // Get header
            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size >= 5) {
                    list.add(
                        CsvProperty(
                            number = tokens[0].trim(),
                            type = tokens[1].trim(),
                            location = tokens[2].trim(),
                            owner = tokens[3].trim(),
                            value = tokens[4].trim().toDoubleOrNull() ?: 0.0
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MarketRepository", "Error parsing properties.csv", e)
        }
        return list
    }

    private fun parseApartmentsCsv(): List<CsvApartment> {
        val list = mutableListOf<CsvApartment>()
        try {
            val reader = BufferedReader(InputStreamReader(context.assets.open("apartments.csv")))
            var line: String? = reader.readLine() // Get header
            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size >= 5) {
                    list.add(
                        CsvApartment(
                            number = tokens[0].trim(),
                            type = tokens[1].trim(),
                            location = tokens[2].trim(),
                            owner = tokens[3].trim(),
                            value = tokens[4].trim().toDoubleOrNull() ?: 0.0
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MarketRepository", "Error parsing apartments.csv", e)
        }
        return list
    }

    private fun parseBusinessesCsv(): List<CsvBusiness> {
        val list = mutableListOf<CsvBusiness>()
        try {
            val reader = BufferedReader(InputStreamReader(context.assets.open("businesses.csv")))
            var line: String? = reader.readLine() // Get header
            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size >= 5) {
                    list.add(
                        CsvBusiness(
                            name = tokens[0].trim(),
                            category = tokens[1].trim(),
                            owner = tokens[2].trim(),
                            location = tokens[3].trim(),
                            value = tokens[4].trim().toDoubleOrNull() ?: 0.0
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MarketRepository", "Error parsing businesses.csv", e)
        }
        return list
    }


    // --- OPERATIONS FROM CONTROLLER ---

    // General Insert listing
    suspend fun createNewListing(listing: Listing) {
        dao.insertListing(listing)
    }

    suspend fun toggleWatchlist(listingId: Int) {
        val listing = dao.getListingById(listingId)
        if (listing != null) {
            val updated = listing.copy(isWatchlisted = !listing.isWatchlisted)
            dao.updateListing(updated)
        }
    }

    // Sell / Mark as sold
    suspend fun markListingAsSold(listingId: Int) {
        val listing = dao.getListingById(listingId)
        if (listing != null) {
            val updated = listing.copy(isSold = true)
            dao.updateListing(updated)
        }
    }

    // Account login simulation
    suspend fun simulateGoogleLogin(
        name: String = "Aditya G",
        email: String = "adityaghatule40@gmail.com",
        avatarUrl: String? = null
    ) {
        val current = dao.getUserProfile() ?: UserProfile()
        val updated = current.copy(
            name = name,
            email = email,
            avatarUrl = avatarUrl,
            loginCount = current.loginCount + 1,
            lastLoginTime = System.currentTimeMillis()
        )
        dao.insertProfile(updated)
    }

    suspend fun simulateEmailLogin(email: String) {
        val current = dao.getUserProfile() ?: UserProfile()
        val formattedName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val updated = current.copy(
            name = formattedName,
            email = email,
            loginCount = current.loginCount + 1,
            lastLoginTime = System.currentTimeMillis()
        )
        dao.insertProfile(updated)
    }

    // Creating deal rooms
    suspend fun initiateDeal(buyerName: String, listingId: Int): Int {
        val listing = dao.getListingById(listingId) ?: return 0
        
        // Check if deal already exists for this listing and buyer
        val rooms = dao.getAllDealRooms().firstOrNull() ?: emptyList()
        val existing = rooms.find { it.listingId == listingId && it.buyerName == buyerName }
        if (existing != null) {
            return existing.id
        }

        val newRoom = DealRoom(
            listingId = listingId,
            listingTitle = listing.name,
            listingType = listing.type,
            listingPrice = listing.price,
            buyerName = buyerName,
            sellerName = listing.ownerName,
            lastOfferPrice = listing.price,
            currentStatus = "PENDING",
            dateCreated = System.currentTimeMillis(),
            lastMessage = "Deal room initiated.",
            lastUpdated = System.currentTimeMillis()
        )

        val roomId = dao.insertDealRoom(newRoom).toInt()
        
        // Prepopulate welcome messages
        dao.insertMessage(
            DealMessage(
                dealRoomId = roomId,
                senderName = "System Bot",
                messageText = "Welcome to the private Deal Room for ${listing.name}! Use this clean space to chat, request pictures, make offers, and negotiate details safely."
            )
        )
        
        return roomId
    }

    // Chat activity / Offers
    suspend fun sendDealMessage(roomId: Int, sender: String, text: String, offerAmount: Double? = null) {
        val room = dao.getDealRoomById(roomId) ?: return
        
        var isOffer = false
        var updatedStatus = room.currentStatus
        
        if (offerAmount != null) {
            isOffer = true
            updatedStatus = "OFFER_MADE"
        }
        
        dao.insertMessage(
            DealMessage(
                dealRoomId = roomId,
                senderName = sender,
                messageText = text,
                isOffer = isOffer,
                offerAmount = offerAmount
            )
        )

        val updatedRoom = room.copy(
            lastMessage = text,
            lastOfferPrice = offerAmount ?: room.lastOfferPrice,
            currentStatus = updatedStatus,
            lastUpdated = System.currentTimeMillis()
        )
        dao.updateDealRoom(updatedRoom)
    }

    // Accept / Reject Deals
    suspend fun updateDealStatus(roomId: Int, status: String) {
        val room = dao.getDealRoomById(roomId) ?: return
        val updatedRoom = room.copy(
            currentStatus = status,
            lastUpdated = System.currentTimeMillis(),
            lastMessage = "Deal status updated to: $status"
        )
        dao.updateDealRoom(updatedRoom)
        
        if (status == "ACCEPTED") {
            // Mark listing as sold
            markListingAsSold(room.listingId)
            
            // Add trade reward / stats increment
            val profile = dao.getUserProfile()
            if (profile != null) {
                // If current user is buyer, deduct wallet and complete trade
                val isBuyer = profile.name == room.buyerName
                val isSeller = profile.name == room.sellerName
                
                val currentWallet = if (isBuyer) profile.walletBalance - room.lastOfferPrice else profile.walletBalance
                val updatedWallet = if (isSeller) profile.walletBalance + room.lastOfferPrice else currentWallet
                
                val completed = profile.completedTrades + 1
                val score = profile.traderRankScore + 5
                
                // Advance reputation rank depending on completed trades
                val newRep = when {
                    completed >= 25 -> "Market Legend"
                    completed >= 15 -> "Elite Trader"
                    completed >= 10 -> "Professional Trader"
                    completed >= 5 -> "Trusted Trader"
                    else -> "Rookie Trader"
                }
                
                val newVerify = when {
                    completed >= 25 -> "Marketplace Partner"
                    completed >= 15 -> "Elite Client"
                    completed >= 10 -> "Trusted Trader"
                    completed >= 5 -> "Verified Trader"
                    else -> "Basic User"
                }

                dao.updateProfile(
                    profile.copy(
                        walletBalance = updatedWallet,
                        completedTrades = completed,
                        reputationRank = newRep,
                        verificationLevel = newVerify,
                        traderRankScore = score.coerceAtMost(100)
                    )
                )
            }
        }
    }

    // Coin purchase Ticket Submissions
    suspend fun purchaseCoins(packageName: String, coins: Int, price: Double, txId: String) {
        val newTicket = CoinTicket(
            packageName = packageName,
            coinAmount = coins,
            price = price,
            transactionId = txId,
            status = "PENDING"
        )
        dao.insertCoinTicket(newTicket)
    }

    // Owner Panel handles tickets
    suspend fun resolveCoinTicket(ticketId: Int, approve: Boolean, feedback: String? = null) {
        val ticket = dao.getCoinTicketById(ticketId) ?: return
        val newStatus = if (approve) "APPROVED" else "REJECTED"
        
        dao.updateCoinTicket(
            ticket.copy(
                status = newStatus,
                feedback = feedback
            )
        )

        // If approved, add coin transactions!
        if (approve) {
            val profile = dao.getUserProfile()
            if (profile != null) {
                dao.updateProfile(
                    profile.copy(
                        coinBalance = profile.coinBalance + ticket.coinAmount,
                        walletBalance = profile.walletBalance - ticket.price // Simulating debiting cash in exchange
                    )
                )
            }
        }
    }

    // Submit reports
    suspend fun submitScamReport(report: ScamReport) {
        dao.insertScamReport(report)
    }

    suspend fun updateScamStatus(id: Int, status: String) {
        dao.getAllScamReports().firstOrNull()?.find { it.id == id }?.let { report ->
            dao.updateScamReport(report.copy(status = status))
        }
    }

    // Vote on Community Polls
    suspend fun voteOnPostPoll(postId: Int, selectedIndex: Int) {
        dao.getAllCommunityPosts().firstOrNull()?.find { it.id == postId }?.let { post ->
            if (post.isPoll && post.pollOptionsJson != null && post.pollVotesJson != null) {
                // Parsing mock vote options and counts
                // In a production setup, we'd use serialization. Let's do simple parsing of elements.
                try {
                    val cleanedValues = post.pollVotesJson.replace("[", "").replace("]", "").replace("\"", "")
                    val votesList = cleanedValues.split(",").map { it.trim().toIntOrNull() ?: 0 }.toMutableList()
                    
                    if (selectedIndex in votesList.indices) {
                        votesList[selectedIndex] = votesList[selectedIndex] + 1
                        val updatedVotesJson = votesList.map { "\"$it\"" }.toString()
                        
                        dao.updateCommunityPost(
                            post.copy(
                                pollVotesJson = updatedVotesJson,
                                userVotedOptionIndex = selectedIndex
                            )
                        )
                    }
                } catch(e: Exception) {
                    Log.e("Repository", "Poll vote update failure", e)
                }
            }
        }
    }

    // Add likes to feed post
    suspend fun toggleLikePost(postId: Int) {
        dao.getAllCommunityPosts().firstOrNull()?.find { it.id == postId }?.let { post ->
            val isLiked = !post.isLiked
            val delta = if (isLiked) 1 else -1
            dao.updateCommunityPost(
                post.copy(
                    isLiked = isLiked,
                    likesCount = (post.likesCount + delta).coerceAtLeast(0)
                )
            )
        }
    }

    // Add general community post
    suspend fun createCommunityPost(post: CommunityPost) {
        dao.insertCommunityPost(post)
    }

    // Submit new buyer board requests
    suspend fun createBuyerRequest(request: BuyerRequest) {
        dao.insertBuyerRequest(request)
    }

    // --- PAYMENTS, ORDERS & TRANSACTION LEDGER SYSTEM API ---
    val allOrders: Flow<List<NekoOrder>> = dao.getAllOrders()
    val allPayments: Flow<List<NekoPayment>> = dao.getAllPayments()
    val allTransactions: Flow<List<CoinTransaction>> = dao.getAllTransactions()
    val pendingOrders: Flow<List<NekoOrder>> = dao.getPendingOrdersFlow()

    suspend fun createOrder(userId: String, amount: Double, coinAmount: Int): NekoOrder {
        return withContext(Dispatchers.IO) {
            val orderId = "ORD-" + (100000 + (Math.random() * 900000).toInt()).toString()
            val expiresAt = System.currentTimeMillis() + 15 * 60 * 1000 // 15 mins
            val order = NekoOrder(
                id = orderId,
                userId = userId,
                amount = amount,
                coinAmount = coinAmount,
                status = "PENDING_PAYMENT",
                createdAt = System.currentTimeMillis(),
                expiresAt = expiresAt
            )
            dao.insertOrder(order)
            order
        }
    }

    suspend fun getOrder(orderId: String): NekoOrder? = dao.getOrderById(orderId)

    suspend fun cancelOrExpireOrder(orderId: String) {
        withContext(Dispatchers.IO) {
            val order = dao.getOrderById(orderId)
            if (order != null && order.status == "PENDING_PAYMENT") {
                dao.updateOrder(order.copy(status = "EXPIRED"))
            }
        }
    }

    suspend fun verifyOrderCompletion(orderId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val order = dao.getOrderById(orderId)
            order?.status == "COMPLETED"
        }
    }

    suspend fun manualApproveOrder(orderId: String, adminNotes: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            val order = dao.getOrderById(orderId)
            if (order != null && (order.status == "PENDING_PAYMENT" || order.status == "EXPIRED")) {
                // Mark order completed
                dao.updateOrder(order.copy(status = "COMPLETED"))
                // Create manual transaction
                val tx = CoinTransaction(
                    userId = order.userId,
                    paymentId = null,
                    orderId = order.id,
                    coinsAdded = order.coinAmount,
                    completedAt = System.currentTimeMillis()
                )
                dao.insertTransaction(tx)
                // Credit profile coins
                val profile = dao.getUserProfile()
                if (profile != null) {
                    dao.updateProfile(profile.copy(coinBalance = profile.coinBalance + order.coinAmount))
                }
                true
            } else {
                false
            }
        }
    }

    suspend fun manualAssignPayment(paymentId: Int, orderId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val order = dao.getOrderById(orderId)
            if (order != null && order.status == "PENDING_PAYMENT") {
                // Check if payment was already matched
                val updatedOrder = order.copy(status = "COMPLETED")
                dao.updateOrder(updatedOrder)
                
                val tx = CoinTransaction(
                    userId = order.userId,
                    paymentId = paymentId,
                    orderId = order.id,
                    coinsAdded = order.coinAmount,
                    completedAt = System.currentTimeMillis()
                )
                dao.insertTransaction(tx)

                val profile = dao.getUserProfile()
                if (profile != null) {
                    dao.updateProfile(profile.copy(coinBalance = profile.coinBalance + order.coinAmount))
                }
                true
            } else {
                false
            }
        }
    }

    suspend fun forceRejectOrder(orderId: String) {
        withContext(Dispatchers.IO) {
            val order = dao.getOrderById(orderId)
            if (order != null) {
                dao.updateOrder(order.copy(status = "FAILED"))
            }
        }
    }

    suspend fun processIncomingPaymentSms(
        amount: Double,
        utr: String,
        timestamp: Long,
        sender: String,
        rawSms: String
    ): MatchingResult {
        return withContext(Dispatchers.IO) {
            // 1. Duplicate Protection (UTR check)
            val existingPayment = dao.getPaymentByUtr(utr)
            if (existingPayment != null) {
                return@withContext MatchingResult.Duplicate
            }

            // 2. Insert new payment
            val payment = NekoPayment(
                utr = utr,
                amount = amount,
                timestamp = timestamp,
                smsSender = sender,
                rawSms = rawSms
            )
            val paymentId = dao.insertPayment(payment).toInt()

            // 3. Search Pending Orders for same price and closest timestamp (before expiration)
            val now = System.currentTimeMillis()
            val pendingOrdersList = dao.getPendingOrdersList().filter {
                it.amount == amount && it.expiresAt > now
            }

            if (pendingOrdersList.isNotEmpty()) {
                // Find order closest to payment timestamp, but created before or shortly around it
                // Since SMS can arrive slightly after creation, we find min timestamp difference
                val matchedOrder = pendingOrdersList.minByOrNull { Math.abs(it.createdAt - timestamp) }
                if (matchedOrder != null) {
                    // Update Order
                    dao.updateOrder(matchedOrder.copy(status = "COMPLETED"))
                    
                    // Create Transaction record
                    val tx = CoinTransaction(
                        userId = matchedOrder.userId,
                        paymentId = paymentId,
                        orderId = matchedOrder.id,
                        coinsAdded = matchedOrder.coinAmount,
                        completedAt = System.currentTimeMillis()
                    )
                    dao.insertTransaction(tx)

                    // Credit user coins
                    val profile = dao.getUserProfile()
                    if (profile != null) {
                        dao.updateProfile(profile.copy(coinBalance = profile.coinBalance + matchedOrder.coinAmount))
                    }

                    return@withContext MatchingResult.Success(matchedOrder, matchedOrder.coinAmount)
                }
            }

            MatchingResult.NoMatchingOrder
        }
    }

    // --- SUPPLIER DEALS ---
    val allSupplierDeals: Flow<List<SupplierDeal>> = dao.getAllSupplierDeals()

    fun getSupplierDealsByUser(userId: String): Flow<List<SupplierDeal>> = dao.getSupplierDealsByUser(userId)

    suspend fun createSupplierDeal(userId: String, gameName: String, dealCode: String, videoUrl: String, reputationDelta: Int) {
        withContext(Dispatchers.IO) {
            val deal = SupplierDeal(
                userId = userId,
                gameName = gameName,
                dealCode = dealCode,
                videoUrl = videoUrl,
                reputationDelta = reputationDelta,
                status = "PENDING"
            )
            dao.insertSupplierDeal(deal)
        }
    }

    suspend fun updateSupplierDealStatus(dealId: Int, status: String, reputationDelta: Int) {
        withContext(Dispatchers.IO) {
            val deal = dao.getSupplierDealById(dealId) ?: return@withContext
            val updatedDeal = deal.copy(status = status, reputationDelta = reputationDelta)
            dao.updateSupplierDeal(updatedDeal)

            if (status == "APPROVED") {
                val profile = dao.getUserProfile()
                if (profile != null) {
                    val completed = profile.completedTrades + 1
                    val newScore = (profile.traderRankScore + reputationDelta).coerceAtMost(100)
                    
                    val newRep = when {
                        completed >= 25 -> "Market Legend"
                        completed >= 15 -> "Elite Trader"
                        completed >= 10 -> "Professional Trader"
                        completed >= 5 -> "Trusted Trader"
                        else -> "Rookie Trader"
                    }
                    val newVerify = when {
                        completed >= 25 -> "Marketplace Partner"
                        completed >= 15 -> "Elite Client"
                        completed >= 10 -> "Trusted Trader"
                        completed >= 5 -> "Verified Trader"
                        else -> "Basic User"
                    }
                    dao.updateProfile(
                        profile.copy(
                            completedTrades = completed,
                            traderRankScore = newScore,
                            reputationRank = newRep,
                            verificationLevel = newVerify
                        )
                    )
                }
            }
        }
    }
}

sealed class MatchingResult {
    data class Success(val order: NekoOrder, val coinsAdded: Int) : MatchingResult()
    object Duplicate : MatchingResult()
    object NoMatchingOrder : MatchingResult()
}

// Data holder classes for CSV files
data class CsvProperty(val number: String, val type: String, val location: String, val owner: String, val value: Double)
data class CsvApartment(val number: String, val type: String, val location: String, val owner: String, val value: Double)
data class CsvBusiness(val name: String, val category: String, val owner: String, val location: String, val value: Double)
