package com.example.realestateapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.realestateapp.data.dao.PropertyDao
import com.example.realestateapp.data.dao.TransactionDao
import com.example.realestateapp.data.dao.UserDao
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.User

@Database(
    entities = [Property::class, User::class, Transaction::class],
    version = 2,
    exportSchema = false
)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
