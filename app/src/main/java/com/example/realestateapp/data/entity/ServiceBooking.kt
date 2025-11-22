package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "service_bookings",
    foreignKeys = [
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceBooking(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val serviceId: String = "",
    val userId: String = "",
    val bookingDate: Long = System.currentTimeMillis(),
    val scheduledDate: Long = 0,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String = ""
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        serviceId = "",
        userId = "",
        bookingDate = System.currentTimeMillis(),
        scheduledDate = 0,
        status = BookingStatus.PENDING,
        notes = ""
    )
}

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}
