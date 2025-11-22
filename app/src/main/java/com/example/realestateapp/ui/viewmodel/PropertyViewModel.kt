package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PropertyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PropertyRepository
    
    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()
    
    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty: StateFlow<Property?> = _selectedProperty.asStateFlow()
    
    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        repository = PropertyRepository(database.propertyDao())
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
                val property = withContext(Dispatchers.IO) {
                    repository.getPropertyById(propertyId)
                }
                _selectedProperty.value = property
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error loading property", e)
            }
        }
    }
    
    fun addProperty(property: Property) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.insertProperty(property)
                }
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error adding property", e)
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
                
                // Save to local database
                withContext(Dispatchers.IO) {
                    repository.insertProperty(finalProperty)
                }
                
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error adding property with image", e)
            } finally {
                _isUploadingImage.value = false
            }
        }
    }
    
    fun updateProperty(property: Property) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.updateProperty(property)
                }
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error updating property", e)
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
            // Mumbai
            Property(
                id = "local_prop_1",
                title = "2BHK Apartment in Andheri West",
                description = "Spacious 2-bedroom apartment with modern amenities in prime Andheri West location.",
                price = 45000.0,
                address = "Andheri West",
                city = "Mumbai",
                zipCode = "400053",
                bedrooms = 2,
                bathrooms = 2,
                area = 950.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "local_seller_1",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_2",
                title = "3BHK Flat in Bandra",
                description = "Luxurious 3-bedroom flat with sea view in upscale Bandra locality.",
                price = 65000.0,
                address = "Bandra",
                city = "Mumbai",
                zipCode = "400050",
                bedrooms = 3,
                bathrooms = 2,
                area = 1250.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "local_seller_2",
                isSold = false,
                country = "India"
            ),
            // Pune
            Property(
                id = "local_prop_3",
                title = "3BHK Flat near Hinjewadi Phase 1",
                description = "Modern 3-bedroom flat with excellent connectivity near Hinjewadi IT Park.",
                price = 28000.0,
                address = "Hinjewadi Phase 1",
                city = "Pune",
                zipCode = "411057",
                bedrooms = 3,
                bathrooms = 2,
                area = 1400.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "local_seller_3",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_4",
                title = "2BHK Apartment in Wakad",
                description = "Well-furnished 2-bedroom apartment in the heart of Wakad tech hub.",
                price = 22000.0,
                address = "Wakad",
                city = "Pune",
                zipCode = "411057",
                bedrooms = 2,
                bathrooms = 2,
                area = 850.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "local_seller_4",
                isSold = false,
                country = "India"
            ),
            // Bengaluru
            Property(
                id = "local_prop_5",
                title = "Luxury 4BHK Villa in Whitefield",
                description = "Premium 4-bedroom villa with garden and modern amenities in Whitefield.",
                price = 75000.0,
                address = "Whitefield",
                city = "Bengaluru",
                zipCode = "560066",
                bedrooms = 4,
                bathrooms = 3,
                area = 2200.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "local_seller_5",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_6",
                title = "1RK near Kormangala",
                description = "Cozy 1-bedroom apartment near Kormangala with excellent connectivity.",
                price = 18000.0,
                address = "Kormangala",
                city = "Bengaluru",
                zipCode = "560034",
                bedrooms = 1,
                bathrooms = 1,
                area = 550.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "local_seller_6",
                isSold = false,
                country = "India"
            ),
            // Delhi
            Property(
                id = "local_prop_7",
                title = "2BHK Apartment in Connaught Place",
                description = "Prime location 2-bedroom apartment in the heart of Delhi with premium amenities.",
                price = 55000.0,
                address = "Connaught Place",
                city = "Delhi",
                zipCode = "110001",
                bedrooms = 2,
                bathrooms = 2,
                area = 1100.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "local_seller_7",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_8",
                title = "3BHK Flat in Dwarka",
                description = "Spacious 3-bedroom flat with modern facilities in planned Dwarka area.",
                price = 38000.0,
                address = "Dwarka",
                city = "Delhi",
                zipCode = "110075",
                bedrooms = 3,
                bathrooms = 2,
                area = 1300.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "local_seller_8",
                isSold = false,
                country = "India"
            ),
            // Chennai
            Property(
                id = "local_prop_9",
                title = "2BHK Apartment in T. Nagar",
                description = "Well-located 2-bedroom apartment in the bustling T. Nagar locality.",
                price = 32000.0,
                address = "T. Nagar",
                city = "Chennai",
                zipCode = "600017",
                bedrooms = 2,
                bathrooms = 2,
                area = 900.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "local_seller_9",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_10",
                title = "3BHK Villa in Velachery",
                description = "Independent 3-bedroom villa with spacious garden in Velachery.",
                price = 48000.0,
                address = "Velachery",
                city = "Chennai",
                zipCode = "600042",
                bedrooms = 3,
                bathrooms = 2,
                area = 1500.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "local_seller_10",
                isSold = false,
                country = "India"
            ),
            // Hyderabad
            Property(
                id = "local_prop_11",
                title = "2BHK Apartment in Hitech City",
                description = "Modern 2-bedroom apartment in the IT hub of Hyderabad.",
                price = 35000.0,
                address = "Hitech City",
                city = "Hyderabad",
                zipCode = "500081",
                bedrooms = 2,
                bathrooms = 2,
                area = 950.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "local_seller_11",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_12",
                title = "3BHK Flat in Jubilee Hills",
                description = "Upscale 3-bedroom flat in the premium Jubilee Hills locality.",
                price = 52000.0,
                address = "Jubilee Hills",
                city = "Hyderabad",
                zipCode = "500033",
                bedrooms = 3,
                bathrooms = 2,
                area = 1350.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "local_seller_12",
                isSold = false,
                country = "India"
            ),
            // Kolkata
            Property(
                id = "local_prop_13",
                title = "2BHK Apartment in Salt Lake",
                description = "Comfortable 2-bedroom apartment in the planned Salt Lake City.",
                price = 25000.0,
                address = "Salt Lake",
                city = "Kolkata",
                zipCode = "700091",
                bedrooms = 2,
                bathrooms = 2,
                area = 900.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "local_seller_13",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_14",
                title = "3BHK Flat in Alipore",
                description = "Heritage 3-bedroom flat in the historic Alipore area.",
                price = 40000.0,
                address = "Alipore",
                city = "Kolkata",
                zipCode = "700027",
                bedrooms = 3,
                bathrooms = 2,
                area = 1200.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "local_seller_14",
                isSold = false,
                country = "India"
            ),
            // Ahmedabad
            Property(
                id = "local_prop_15",
                title = "2BHK Apartment in Thaltej",
                description = "Modern 2-bedroom apartment in the growing Thaltej locality.",
                price = 20000.0,
                address = "Thaltej",
                city = "Ahmedabad",
                zipCode = "380054",
                bedrooms = 2,
                bathrooms = 2,
                area = 850.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "local_seller_15",
                isSold = false,
                country = "India"
            ),
            Property(
                id = "local_prop_16",
                title = "3BHK Flat in Satellite",
                description = "Spacious 3-bedroom flat in the upscale Satellite area.",
                price = 35000.0,
                address = "Satellite",
                city = "Ahmedabad",
                zipCode = "380015",
                bedrooms = 3,
                bathrooms = 2,
                area = 1300.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "local_seller_16",
                isSold = false,
                country = "India"
            )
        )
    }
}
