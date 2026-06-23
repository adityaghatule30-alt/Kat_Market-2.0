package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // VEHICLE, PROPERTY, BUSINESS, ITEM
    val name: String,
    val price: Double,
    val category: String, // Car, Bike, Boat, Helicopter, House, Apartment, Villa, etc.
    val location: String,
    val ownerName: String,
    val description: String,
    
    // Vehicle specifics
    val mileage: Double? = null,
    val ownersCount: Int? = null,
    val plateNumber: String? = null,
    
    // Property/Business specifics
    val numberCode: String? = null, // Property number like #101
    val originalValue: Double? = null,
    
    // UI details
    val imageUrl: String? = null,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val demandScore: Int = 50,
    val dateAdded: Long = System.currentTimeMillis(),
    val isSold: Boolean = false,
    val isWatchlisted: Boolean = false
) : Serializable

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single profile row
    val name: String = "Aditya G",
    val email: String = "adityaghatule40@gmail.com",
    val walletBalance: Double = 250000.0,
    val coinBalance: Int = 150,
    val completedTrades: Int = 12,
    val reviewsCount: Int = 8,
    val traderRankScore: Int = 85, // out of 100
    val reputationRank: String = "Trusted Trader", // Rookie, Trusted, Professional, Elite, Market Legend
    val verificationLevel: String = "Verified Trader", // Basic, Verified, Trusted, Elite, Marketplace Partner
    val avatarUrl: String? = null,
    val loginCount: Int = 4,
    val lastLoginTime: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "deal_rooms")
data class DealRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val listingType: String,
    val listingPrice: Double,
    val buyerName: String,
    val sellerName: String,
    val lastOfferPrice: Double,
    val currentStatus: String, // PENDING, OFFER_MADE, COUNTER_MADE, ACCEPTED, REJECTED, SOLD
    val dateCreated: Long = System.currentTimeMillis(),
    val lastMessage: String = "Deal room opened.",
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "deal_messages")
data class DealMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dealRoomId: Int,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val isOffer: Boolean = false,
    val offerAmount: Double? = null
) : Serializable

@Entity(tableName = "buyer_requests")
data class BuyerRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requesterName: String,
    val itemType: String, // e.g. "BMW M5" or "House #42"
    val budget: Double,
    val description: String,
    val responsesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "coin_tickets")
data class CoinTicket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val coinAmount: Int,
    val price: Double,
    val transactionId: String,
    val status: String, // PENDING, APPROVED, REJECTED, MORE_INFO
    val feedback: String? = null,
    val screenshotUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorRank: String,
    val authorAvatarUrl: String? = null,
    val content: String,
    val category: String, // FEED, ALLIANCE, RECRUITMENT, EVENT, POLL
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    
    // Poll specific parameters
    val isPoll: Boolean = false,
    val pollQuestion: String? = null,
    val pollOptionsJson: String? = null, // Serialized array, e.g. ["Yes","No"]
    val pollVotesJson: String? = null, // Serialized array of counts, e.g. [5,12]
    val userVotedOptionIndex: Int? = null
) : Serializable

@Entity(tableName = "scam_reports")
data class ScamReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int?,
    val listingTitle: String?,
    val offenderName: String?,
    val reporterName: String,
    val reason: String, // Fake Listing, Scam Attempt, Payment Fraud, Other
    val details: String,
    val status: String = "PENDING", // PENDING, INVESTIGATING, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // INFO, OFFER, PURCHASE, VERIFY, ALERT
    val isRead: Boolean = false
)

@Entity(tableName = "orders")
data class NekoOrder(
    @PrimaryKey val id: String, // e.g. "ORD-123456"
    val userId: String, // User email or display name
    val amount: Double,
    val coinAmount: Int,
    val status: String, // PENDING_PAYMENT, COMPLETED, FAILED, EXPIRED
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 15 * 60 * 1000 // 15 minutes
) : Serializable

@Entity(tableName = "payments")
data class NekoPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val utr: String,
    val amount: Double,
    val timestamp: Long,
    val smsSender: String,
    val rawSms: String
) : Serializable

@Entity(tableName = "transactions")
data class CoinTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val paymentId: Int?,
    val orderId: String?,
    val coinsAdded: Int,
    val completedAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "supplier_deals")
data class SupplierDeal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // email or ID of supplier
    val gameName: String,
    val dealCode: String, // 6-digit code
    val videoUrl: String, // YouTube link
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val reputationDelta: Int = 1, // +1 or +2 decided during submission or review
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

