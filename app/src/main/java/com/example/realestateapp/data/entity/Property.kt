package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val address: String = "",
    val city: String = "",
    val zipCode: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: Double = 0.0,
    val imageUrl: String? = null,
    val sellerId: String = "",
    val isSold: Boolean = false,
    val datePosted: Long = System.currentTimeMillis(),
    val localityId: String = "",
    val country: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this(
        id = UUID.randomUUID().toString(),
        title = "",
        description = "",
        price = 0.0,
        address = "",
        city = "",
        zipCode = "",
        bedrooms = 0,
        bathrooms = 0,
        area = 0.0,
        imageUrl = null,
        sellerId = "",
        isSold = false,
        datePosted = System.currentTimeMillis(),
        localityId = "",
        country = ""
    )
}
