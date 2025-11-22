package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "localities")
data class Locality(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val city: String = "",
    val state: String = "",
    val safetyRating: Double = 0.0,
    val transportRating: Double = 0.0,
    val schoolsRating: Double = 0.0,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        name = "",
        city = "",
        state = "",
        safetyRating = 0.0,
        transportRating = 0.0,
        schoolsRating = 0.0,
        reviewCount = 0,
        isVerified = false,
        dateAdded = System.currentTimeMillis()
    )
}
