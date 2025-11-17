package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.PropertyDao
import com.example.realestateapp.data.entity.Property
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {
    
    suspend fun insertProperty(property: Property): Long {
        return propertyDao.insertProperty(property)
    }
    
    suspend fun updateProperty(property: Property) {
        propertyDao.updateProperty(property)
    }
    
    suspend fun deleteProperty(property: Property) {
        propertyDao.deleteProperty(property)
    }
    
    suspend fun getPropertyById(propertyId: String): Property? {
        return propertyDao.getPropertyById(propertyId)
    }
    
    fun getAllAvailableProperties(): Flow<List<Property>> {
        return propertyDao.getAllAvailableProperties()
    }
    
    suspend fun getAllProperties(): List<Property> {
        return propertyDao.getAllProperties()
    }
    
    suspend fun getPropertyCount(): Int {
        return propertyDao.getPropertyCount()
    }
    
    fun getPropertiesBySeller(userId: String): Flow<List<Property>> {
        return propertyDao.getPropertiesBySeller(userId)
    }
    
    fun searchProperties(searchQuery: String): Flow<List<Property>> {
        return propertyDao.searchProperties(searchQuery)
    }
    
    fun filterProperties(minPrice: Double, maxPrice: Double, minBedrooms: Int, minBathrooms: Int): Flow<List<Property>> {
        return propertyDao.filterProperties(minPrice, maxPrice, minBedrooms, minBathrooms)
    }
}
