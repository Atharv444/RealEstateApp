package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property): Long

    @Update
    suspend fun updateProperty(property: Property)

    @Delete
    suspend fun deleteProperty(property: Property)

    @Query("SELECT * FROM properties WHERE id = :propertyId")
    suspend fun getPropertyById(propertyId: String): Property?

    @Query("SELECT * FROM properties WHERE isSold = 0 ORDER BY datePosted DESC")
    fun getAllAvailableProperties(): Flow<List<Property>>
    
    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<Property>
    
    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getPropertyCount(): Int

    @Query("SELECT * FROM properties WHERE sellerId = :userId")
    fun getPropertiesBySeller(userId: String): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE (LOWER(city) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(address) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(title) LIKE '%' || LOWER(:searchQuery) || '%') ORDER BY datePosted DESC")
    fun searchProperties(searchQuery: String): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE price BETWEEN :minPrice AND :maxPrice AND bedrooms >= :minBedrooms AND bathrooms >= :minBathrooms AND isSold = 0")
    fun filterProperties(minPrice: Double, maxPrice: Double, minBedrooms: Int, minBathrooms: Int): Flow<List<Property>>
}
