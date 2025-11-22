package com.example.realestateapp.data

import androidx.room.TypeConverter
import com.example.realestateapp.data.entity.TransactionStatus
import com.example.realestateapp.data.entity.ServiceCategory
import com.example.realestateapp.data.entity.BookingStatus

class Converters {
    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus {
        return TransactionStatus.valueOf(value)
    }

    @TypeConverter
    fun fromServiceCategory(category: ServiceCategory): String {
        return category.name
    }

    @TypeConverter
    fun toServiceCategory(value: String): ServiceCategory {
        return ServiceCategory.valueOf(value)
    }

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus {
        return BookingStatus.valueOf(value)
    }
}
