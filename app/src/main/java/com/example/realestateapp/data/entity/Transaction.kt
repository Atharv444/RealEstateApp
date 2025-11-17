package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val propertyId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val price: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.PENDING
) {
    // No-argument constructor for Firebase
    constructor() : this(
        id = UUID.randomUUID().toString(),
        propertyId = "",
        buyerId = "",
        sellerId = "",
        price = 0.0,
        date = System.currentTimeMillis(),
        status = TransactionStatus.PENDING
    )
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}
