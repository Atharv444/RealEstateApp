package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "services")
data class Service(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: ServiceCategory = ServiceCategory.PACKERS_MOVERS,
    val description: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val providerName: String = "",
    val providerPhone: String = "",
    val providerEmail: String = "",
    val imageUrl: String? = null,
    val isAvailable: Boolean = true,
    val dateAdded: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        name = "",
        category = ServiceCategory.PACKERS_MOVERS,
        description = "",
        price = 0.0,
        rating = 0.0,
        reviewCount = 0,
        providerName = "",
        providerPhone = "",
        providerEmail = "",
        imageUrl = null,
        isAvailable = true,
        dateAdded = System.currentTimeMillis()
    )
}

enum class ServiceCategory {
    PACKERS_MOVERS,
    CLEANING,
    LEGAL_HELP,
    PAINTING,
    PLUMBING,
    ELECTRICAL,
    CARPENTRY,
    PEST_CONTROL
}
