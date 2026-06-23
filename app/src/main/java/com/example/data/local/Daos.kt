package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // --- LISTINGS ---
    @Query("SELECT * FROM listings ORDER BY dateAdded DESC")
    fun getAllListings(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingById(id: Int): Listing?

    @Query("SELECT * FROM listings WHERE isFeatured = 1 AND isSold = 0 LIMIT 10")
    fun getFeaturedListings(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE isTrending = 1 AND isSold = 0 LIMIT 10")
    fun getTrendingListings(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE type = :type AND isSold = 0 ORDER BY dateAdded DESC")
    fun getListingsByType(type: String): Flow<List<Listing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<Listing>)

    @Update
    suspend fun updateListing(listing: Listing)

    @Query("DELETE FROM listings")
    suspend fun clearAllListings()


    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)


    // --- DEAL ROOMS ---
    @Query("SELECT * FROM deal_rooms ORDER BY lastUpdated DESC")
    fun getAllDealRooms(): Flow<List<DealRoom>>

    @Query("SELECT * FROM deal_rooms WHERE id = :id LIMIT 1")
    suspend fun getDealRoomById(id: Int): DealRoom?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDealRoom(dealRoom: DealRoom): Long

    @Update
    suspend fun updateDealRoom(dealRoom: DealRoom)


    // --- DEAL MESSAGES ---
    @Query("SELECT * FROM deal_messages WHERE dealRoomId = :dealRoomId ORDER BY timestamp ASC")
    fun getMessagesForDeal(dealRoomId: Int): Flow<List<DealMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DealMessage): Long


    // --- BUYER REQ BOARD ---
    @Query("SELECT * FROM buyer_requests ORDER BY timestamp DESC")
    fun getAllBuyerRequests(): Flow<List<BuyerRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuyerRequest(buyerRequest: BuyerRequest)


    // --- COIN SHOP TICKETS ---
    @Query("SELECT * FROM coin_tickets ORDER BY timestamp DESC")
    fun getAllCoinTickets(): Flow<List<CoinTicket>>

    @Query("SELECT * FROM coin_tickets WHERE id = :id LIMIT 1")
    suspend fun getCoinTicketById(id: Int): CoinTicket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinTicket(ticket: CoinTicket)

    @Update
    suspend fun updateCoinTicket(ticket: CoinTicket)


    // --- COMMUNITY POSTS ---
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllCommunityPosts(): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getCommunityPostsByCategory(category: String): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPost(post: CommunityPost)

    @Update
    suspend fun updateCommunityPost(post: CommunityPost)


    // --- SCAM reports ---
    @Query("SELECT * FROM scam_reports ORDER BY timestamp DESC")
    fun getAllScamReports(): Flow<List<ScamReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScamReport(report: ScamReport)
    
    @Update
    suspend fun updateScamReport(report: ScamReport)


    // --- PAYMENTS & ORDERS INTEGRATION ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<NekoOrder>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): NekoOrder?

    @Query("SELECT * FROM orders WHERE status = 'PENDING_PAYMENT' ORDER BY createdAt DESC")
    fun getPendingOrdersFlow(): Flow<List<NekoOrder>>

    @Query("SELECT * FROM orders WHERE status = 'PENDING_PAYMENT' ORDER BY createdAt DESC")
    suspend fun getPendingOrdersList(): List<NekoOrder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: NekoOrder)

    @Update
    suspend fun updateOrder(order: NekoOrder)


    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<NekoPayment>>

    @Query("SELECT * FROM payments WHERE utr = :utr LIMIT 1")
    suspend fun getPaymentByUtr(utr: String): NekoPayment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: NekoPayment): Long


    @Query("SELECT * FROM transactions ORDER BY completedAt DESC")
    fun getAllTransactions(): Flow<List<CoinTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransaction)

    // --- SUPPLIER DEALS ---
    @Query("SELECT * FROM supplier_deals ORDER BY timestamp DESC")
    fun getAllSupplierDeals(): Flow<List<SupplierDeal>>

    @Query("SELECT * FROM supplier_deals WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSupplierDealsByUser(userId: String): Flow<List<SupplierDeal>>

    @Query("SELECT * FROM supplier_deals WHERE id = :id LIMIT 1")
    suspend fun getSupplierDealById(id: Int): SupplierDeal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplierDeal(deal: SupplierDeal)

    @Update
    suspend fun updateSupplierDeal(deal: SupplierDeal)
}
