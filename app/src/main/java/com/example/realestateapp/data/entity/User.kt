package com.example.realestateapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "", // In a real app, this should be hashed
    val dateJoined: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this(
        id = UUID.randomUUID().toString(),
        username = "",
        name = "",
        email = "",
        phone = "",
        password = "",
        dateJoined = System.currentTimeMillis()
    )
}
