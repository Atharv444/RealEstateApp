package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.Service
import com.example.realestateapp.data.entity.ServiceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service): Long

    @Update
    suspend fun updateService(service: Service)

    @Delete
    suspend fun deleteService(service: Service)

    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: String): Service?

    @Query("SELECT * FROM services WHERE isAvailable = 1 ORDER BY rating DESC")
    fun getAllAvailableServices(): Flow<List<Service>>

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<Service>

    @Query("SELECT * FROM services WHERE category = :category AND isAvailable = 1 ORDER BY rating DESC")
    fun getServicesByCategory(category: ServiceCategory): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE (LOWER(name) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(description) LIKE '%' || LOWER(:searchQuery) || '%') AND isAvailable = 1 ORDER BY rating DESC")
    fun searchServices(searchQuery: String): Flow<List<Service>>

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getServiceCount(): Int
}
