package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.Locality
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocality(locality: Locality): Long

    @Update
    suspend fun updateLocality(locality: Locality)

    @Delete
    suspend fun deleteLocality(locality: Locality)

    @Query("SELECT * FROM localities WHERE id = :localityId")
    suspend fun getLocalityById(localityId: String): Locality?

    @Query("SELECT * FROM localities WHERE LOWER(name) = LOWER(:name) AND LOWER(city) = LOWER(:city)")
    suspend fun getLocalityByNameAndCity(name: String, city: String): Locality?

    @Query("SELECT * FROM localities WHERE LOWER(city) = LOWER(:city) ORDER BY safetyRating DESC")
    fun getLocalitiesByCity(city: String): Flow<List<Locality>>

    @Query("SELECT * FROM localities ORDER BY safetyRating DESC")
    fun getAllLocalities(): Flow<List<Locality>>

    @Query("SELECT * FROM localities WHERE isVerified = 1 ORDER BY safetyRating DESC")
    fun getVerifiedLocalities(): Flow<List<Locality>>

    @Query("SELECT * FROM localities WHERE (LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(city) LIKE '%' || LOWER(:searchQuery) || '%') ORDER BY safetyRating DESC")
    fun searchLocalities(searchQuery: String): Flow<List<Locality>>

    @Query("SELECT COUNT(*) FROM localities")
    suspend fun getLocalityCount(): Int
}
