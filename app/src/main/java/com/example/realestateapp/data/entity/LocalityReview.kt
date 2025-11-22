package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "locality_reviews",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LocalityReview(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val localityId: String = "",
    val userId: String = "",
    val safetyRating: Double = 0.0,
    val transportRating: Double = 0.0,
    val schoolsRating: Double = 0.0,
    val comment: String = "",
    val reviewDate: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        localityId = "",
        userId = "",
        safetyRating = 0.0,
        transportRating = 0.0,
        schoolsRating = 0.0,
        comment = "",
        reviewDate = System.currentTimeMillis()
    )
}
