package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "service_reviews",
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
data class ServiceReview(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val serviceId: String = "",
    val userId: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val reviewDate: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        serviceId = "",
        userId = "",
        rating = 0.0,
        comment = "",
        reviewDate = System.currentTimeMillis()
    )
}
