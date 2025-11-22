package com.example.realestateapp

import android.app.Application
import android.util.Log
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.repository.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RealEstateApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Delete the database file completely
        deleteDatabase("real_estate_database")
        Log.d("RealEstateApp", "✓ Database file deleted")
    }
    
    private fun clearAllProperties() {
        applicationScope.launch {
            try {
                Log.d("RealEstateApp", "Clearing all existing properties...")
                
                val database = RealEstateDatabase.getDatabase(this@RealEstateApplication)
                val propertyRepository = PropertyRepository(database.propertyDao())
                
                withContext(Dispatchers.IO) {
                    val allProperties = propertyRepository.getAllProperties()
                    Log.d("RealEstateApp", "Found ${allProperties.size} properties to delete")
                    
                    allProperties.forEach { property ->
                        try {
                            propertyRepository.deleteProperty(property)
                            Log.d("RealEstateApp", "✓ Deleted property: ${property.title}")
                        } catch (e: Exception) {
                            Log.e("RealEstateApp", "Error deleting property: ${property.title}", e)
                        }
                    }
                }
                
                // Verify properties are cleared
                val propertyCount = propertyRepository.getPropertyCount()
                Log.d("RealEstateApp", "✓ Properties after clearing: $propertyCount")
                
            } catch (e: Exception) {
                Log.e("RealEstateApp", "Error during property clearing", e)
            }
        }
    }
    
}
