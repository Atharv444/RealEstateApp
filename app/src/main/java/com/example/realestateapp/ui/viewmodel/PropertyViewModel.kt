package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.repository.PropertyRepository
import com.example.realestateapp.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PropertyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PropertyRepository
    private val firebaseRepository: FirebaseRepository
    
    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()
    
    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty: StateFlow<Property?> = _selectedProperty.asStateFlow()
    
    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        repository = PropertyRepository(database.propertyDao())
        firebaseRepository = FirebaseRepository()
        loadProperties()
    }
    
    fun loadProperties() {
        viewModelScope.launch {
            try {
                Log.d("PropertyViewModel", "Loading all properties...")
                // Load from local database first (more reliable)
                repository.getAllAvailableProperties().collectLatest { propertyList ->
                    Log.d("PropertyViewModel", "Loaded ${propertyList.size} properties from database")
                    propertyList.forEach { property ->
                        Log.d("PropertyViewModel", "  - ${property.title} in ${property.city}")
                    }
                    _properties.value = propertyList
                    
                    // If local database is empty, seed dummy data
                    if (propertyList.isEmpty()) {
                        Log.d("PropertyViewModel", "No properties found, seeding dummy data...")
                        seedDummyData()
                    }
                }
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error loading properties", e)
            }
        }
    }
    
    fun getPropertyById(propertyId: String) {
        viewModelScope.launch {
            try {
                // Try Firebase first
                val firebaseProperty = withContext(Dispatchers.IO) {
                    firebaseRepository.getProperty(propertyId)
                }
                
                if (firebaseProperty != null) {
                    _selectedProperty.value = firebaseProperty
                } else {
                    // Fallback to local database
                    val localProperty = withContext(Dispatchers.IO) {
                        repository.getPropertyById(propertyId)
                    }
                    _selectedProperty.value = localProperty
                }
            } catch (e: Exception) {
                // Fallback to local database
                val property = withContext(Dispatchers.IO) {
                    repository.getPropertyById(propertyId)
                }
                _selectedProperty.value = property
            }
        }
    }
    
    fun addProperty(property: Property) {
        viewModelScope.launch {
            try {
                // Save to local database first (primary storage)
                withContext(Dispatchers.IO) {
                    repository.insertProperty(property)
                }
                
                // Optionally try to sync to Firebase in background
                try {
                    withContext(Dispatchers.IO) {
                        firebaseRepository.saveProperty(property)
                    }
                } catch (e: Exception) {
                    // Firebase sync failed, but that's okay - property is saved locally
                }
            } catch (e: Exception) {
                // Even if local save fails, try again
                withContext(Dispatchers.IO) {
                    repository.insertProperty(property)
                }
            }
        }
    }
    
    fun addPropertyWithImage(property: Property, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _isUploadingImage.value = true
                
                var finalProperty = property
                
                // For local storage, just use the URI string as imageUrl
                if (imageUri != null) {
                    finalProperty = property.copy(imageUrl = imageUri.toString())
                }
                
                // Save to local database first (primary storage)
                withContext(Dispatchers.IO) {
                    repository.insertProperty(finalProperty)
                }
                
                // Optionally try to sync to Firebase in background
                try {
                    withContext(Dispatchers.IO) {
                        firebaseRepository.saveProperty(finalProperty)
                    }
                } catch (e: Exception) {
                    // Firebase sync failed, but that's okay - property is saved locally
                }
                
            } catch (e: Exception) {
                // Even if everything fails, try to save basic property
                withContext(Dispatchers.IO) {
                    repository.insertProperty(property)
                }
            } finally {
                _isUploadingImage.value = false
            }
        }
    }
    
    fun updateProperty(property: Property) {
        viewModelScope.launch {
            try {
                // Update in Firebase first
                val success = withContext(Dispatchers.IO) {
                    firebaseRepository.updateProperty(property)
                }
                
                // Always update local database
                withContext(Dispatchers.IO) {
                    repository.updateProperty(property)
                }
            } catch (e: Exception) {
                // Fallback to local database only
                withContext(Dispatchers.IO) {
                    repository.updateProperty(property)
                }
            }
        }
    }
    
    fun deleteProperty(property: Property) {
        viewModelScope.launch {
            repository.deleteProperty(property)
        }
    }
    
    fun searchProperties(query: String) {
        viewModelScope.launch {
            try {
                Log.d("PropertyViewModel", "Searching for: $query")
                
                // First, check total properties in database
                val totalCount = withContext(Dispatchers.IO) {
                    repository.getPropertyCount()
                }
                Log.d("PropertyViewModel", "Total properties in database: $totalCount")
                
                // Search in local database (primary source)
                repository.searchProperties(query).collectLatest { propertyList ->
                    Log.d("PropertyViewModel", "Found ${propertyList.size} properties for query: $query")
                    propertyList.forEach { property ->
                        Log.d("PropertyViewModel", "  ✓ ${property.title} in ${property.city}")
                    }
                    _properties.value = propertyList
                }
            } catch (e: Exception) {
                // Log error for debugging
                Log.e("PropertyViewModel", "Search error for query: $query", e)
                e.printStackTrace()
            }
        }
    }
    
    fun filterProperties(minPrice: Double, maxPrice: Double, minBedrooms: Int, minBathrooms: Int) {
        viewModelScope.launch {
            repository.filterProperties(minPrice, maxPrice, minBedrooms, minBathrooms).collectLatest { propertyList ->
                _properties.value = propertyList
            }
        }
    }
    
    fun getPropertiesBySeller(userId: String) {
        viewModelScope.launch {
            repository.getPropertiesBySeller(userId).collectLatest { propertyList ->
                _properties.value = propertyList
            }
        }
    }
    
    // Manual data seeding for testing
    fun seedDummyData() {
        viewModelScope.launch {
            try {
                // Seed local database directly (more reliable)
                seedLocalDummyData()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private suspend fun seedLocalDummyData() {
        withContext(Dispatchers.IO) {
            val dummyProperties = createLocalDummyProperties()
            dummyProperties.forEach { property ->
                repository.insertProperty(property)
            }
        }
    }
    
    private fun createLocalDummyProperties(): List<Property> {
        return listOf(
            Property(
                id = "local_prop_1",
                title = "Modern Downtown Apartment",
                description = "Beautiful 2-bedroom apartment in the heart of downtown with stunning city views.",
                price = 450000.0,
                address = "123 Main Street",
                city = "New York",
                zipCode = "10001",
                bedrooms = 2,
                bathrooms = 2,
                area = 1200.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "local_seller_1",
                isSold = false
            ),
            Property(
                id = "local_prop_2",
                title = "Cozy Suburban House",
                description = "Charming 3-bedroom family home in quiet neighborhood.",
                price = 325000.0,
                address = "456 Oak Avenue",
                city = "Austin",
                zipCode = "78701",
                bedrooms = 3,
                bathrooms = 2,
                area = 1800.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "local_seller_2",
                isSold = false
            ),
            Property(
                id = "local_prop_3",
                title = "Luxury Penthouse Suite",
                description = "Exclusive penthouse with panoramic ocean views.",
                price = 1250000.0,
                address = "789 Ocean Drive",
                city = "Miami",
                zipCode = "33139",
                bedrooms = 4,
                bathrooms = 3,
                area = 2500.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "local_seller_3",
                isSold = false
            ),
            Property(
                id = "local_prop_4",
                title = "Historic Brownstone",
                description = "Beautifully restored 19th-century brownstone.",
                price = 875000.0,
                address = "321 Heritage Lane",
                city = "Boston",
                zipCode = "02101",
                bedrooms = 3,
                bathrooms = 3,
                area = 2200.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "local_seller_4",
                isSold = false
            ),
            Property(
                id = "local_prop_5",
                title = "Beachfront Condo",
                description = "Stunning oceanfront condominium with direct beach access.",
                price = 695000.0,
                address = "987 Seaside Boulevard",
                city = "San Diego",
                zipCode = "92101",
                bedrooms = 2,
                bathrooms = 2,
                area = 1400.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "local_seller_5",
                isSold = false
            )
        )
    }
}
