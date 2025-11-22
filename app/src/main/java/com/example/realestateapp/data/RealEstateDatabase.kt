package com.example.realestateapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.realestateapp.data.dao.PropertyDao
import com.example.realestateapp.data.dao.TransactionDao
import com.example.realestateapp.data.dao.UserDao
import com.example.realestateapp.data.dao.ServiceDao
import com.example.realestateapp.data.dao.ServiceBookingDao
import com.example.realestateapp.data.dao.ServiceReviewDao
import com.example.realestateapp.data.dao.LocalityDao
import com.example.realestateapp.data.dao.LocalityReviewDao
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.User
import com.example.realestateapp.data.entity.Service
import com.example.realestateapp.data.entity.ServiceBooking
import com.example.realestateapp.data.entity.ServiceReview
import com.example.realestateapp.data.entity.Locality
import com.example.realestateapp.data.entity.LocalityReview

@Database(
    entities = [Property::class, User::class, Transaction::class, Service::class, ServiceBooking::class, ServiceReview::class, Locality::class, LocalityReview::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceBookingDao(): ServiceBookingDao
    abstract fun serviceReviewDao(): ServiceReviewDao
    abstract fun localityDao(): LocalityDao
    abstract fun localityReviewDao(): LocalityReviewDao

    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null

        fun getDatabase(context: Context): RealEstateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    "real_estate_database"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
