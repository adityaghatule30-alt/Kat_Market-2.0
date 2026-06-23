package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        Listing::class,
        UserProfile::class,
        DealRoom::class,
        DealMessage::class,
        BuyerRequest::class,
        CoinTicket::class,
        CommunityPost::class,
        ScamReport::class,
        NekoOrder::class,
        NekoPayment::class,
        CoinTransaction::class,
        SupplierDeal::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MarketDatabase : RoomDatabase() {

    abstract fun marketplaceDao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: MarketDatabase? = null

        fun getDatabase(context: Context): MarketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarketDatabase::class.java,
                    "kat_market_neo_v2_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
