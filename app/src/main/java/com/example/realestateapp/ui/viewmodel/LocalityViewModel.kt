package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Locality
import com.example.realestateapp.data.entity.LocalityReview
import com.example.realestateapp.data.repository.LocalityRepository
import com.example.realestateapp.data.repository.LocalityReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LocalityViewModel(application: Application) : AndroidViewModel(application) {
    
    private val localityRepository: LocalityRepository
    private val reviewRepository: LocalityReviewRepository
    
    private val _localities = MutableStateFlow<List<Locality>>(emptyList())
    val localities: StateFlow<List<Locality>> = _localities.asStateFlow()
    
    private val _selectedLocality = MutableStateFlow<Locality?>(null)
    val selectedLocality: StateFlow<Locality?> = _selectedLocality.asStateFlow()
    
    private val _localityReviews = MutableStateFlow<List<LocalityReview>>(emptyList())
    val localityReviews: StateFlow<List<LocalityReview>> = _localityReviews.asStateFlow()
    
    private val _reviewError = MutableStateFlow<String?>(null)
    val reviewError: StateFlow<String?> = _reviewError.asStateFlow()
    
    private val _reviewSuccess = MutableStateFlow<Boolean>(false)
    val reviewSuccess: StateFlow<Boolean> = _reviewSuccess.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        localityRepository = LocalityRepository(database.localityDao())
        reviewRepository = LocalityReviewRepository(database.localityReviewDao())
        loadLocalities()
    }
    
    fun loadLocalities() {
        viewModelScope.launch {
            try {
                Log.d("LocalityViewModel", "Loading all localities...")
                localityRepository.getAllLocalities().collectLatest { localityList ->
                    Log.d("LocalityViewModel", "Loaded ${localityList.size} localities")
                    _localities.value = localityList
                    
                    if (localityList.isEmpty()) {
                        Log.d("LocalityViewModel", "No localities found, seeding dummy data...")
                        seedDummyLocalities()
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error loading localities", e)
            }
        }
    }
    
    fun getLocalityById(localityId: String) {
        viewModelScope.launch {
            try {
                val locality = withContext(Dispatchers.IO) {
                    localityRepository.getLocalityById(localityId)
                }
                _selectedLocality.value = locality
                
                if (locality != null) {
                    loadLocalityReviews(localityId)
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error loading locality", e)
            }
        }
    }
    
    fun getLocalitiesByCity(city: String) {
        viewModelScope.launch {
            try {
                localityRepository.getLocalitiesByCity(city).collectLatest { localityList ->
                    _localities.value = localityList
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error loading localities by city", e)
            }
        }
    }
    
    fun getLocalityByPropertyLocalityId(localityId: String) {
        viewModelScope.launch {
            try {
                if (localityId.isEmpty()) {
                    Log.w("LocalityViewModel", "Empty localityId provided")
                    _selectedLocality.value = null
                    return@launch
                }
                
                val locality = withContext(Dispatchers.IO) {
                    localityRepository.getLocalityById(localityId)
                }
                
                if (locality != null) {
                    _selectedLocality.value = locality
                    Log.d("LocalityViewModel", "Selected locality: ${locality.name}")
                } else {
                    Log.w("LocalityViewModel", "Locality not found for ID: $localityId")
                    _selectedLocality.value = null
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error loading locality by ID", e)
            }
        }
    }
    
    fun searchLocalities(query: String) {
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    loadLocalities()
                } else {
                    localityRepository.searchLocalities(query).collectLatest { localityList ->
                        _localities.value = localityList
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error searching localities", e)
            }
        }
    }
    
    fun addReview(localityId: String, userId: String, safetyRating: Double, transportRating: Double, schoolsRating: Double, comment: String) {
        viewModelScope.launch {
            try {
                val review = LocalityReview(
                    localityId = localityId,
                    userId = userId,
                    safetyRating = safetyRating.coerceIn(0.0, 5.0),
                    transportRating = transportRating.coerceIn(0.0, 5.0),
                    schoolsRating = schoolsRating.coerceIn(0.0, 5.0),
                    comment = comment
                )
                
                withContext(Dispatchers.IO) {
                    reviewRepository.insertReview(review)
                    
                    // Update locality ratings
                    val avgSafety = reviewRepository.getAverageSafetyRating(localityId) ?: 0.0
                    val avgTransport = reviewRepository.getAverageTransportRating(localityId) ?: 0.0
                    val avgSchools = reviewRepository.getAverageSchoolsRating(localityId) ?: 0.0
                    val reviewCount = reviewRepository.getReviewCount(localityId)
                    
                    val locality = localityRepository.getLocalityById(localityId)
                    locality?.let {
                        val isVerified = reviewCount >= 10
                        val updatedLocality = it.copy(
                            safetyRating = avgSafety,
                            transportRating = avgTransport,
                            schoolsRating = avgSchools,
                            reviewCount = reviewCount,
                            isVerified = isVerified
                        )
                        localityRepository.updateLocality(updatedLocality)
                    }
                }
                
                loadLocalityReviews(localityId)
                _reviewSuccess.value = true
                _reviewError.value = null
                Log.d("LocalityViewModel", "Review added successfully")
            } catch (e: Exception) {
                _reviewError.value = "Failed to add review: ${e.message}"
                Log.e("LocalityViewModel", "Error adding review", e)
            }
        }
    }
    
    fun loadLocalityReviews(localityId: String) {
        viewModelScope.launch {
            try {
                reviewRepository.getLocalityReviews(localityId).collectLatest { reviews ->
                    _localityReviews.value = reviews
                }
            } catch (e: Exception) {
                Log.e("LocalityViewModel", "Error loading reviews", e)
            }
        }
    }
    
    fun resetReviewState() {
        _reviewSuccess.value = false
        _reviewError.value = null
    }
    
    private suspend fun seedDummyLocalities() {
        withContext(Dispatchers.IO) {
            val dummyLocalities = createDummyLocalities()
            dummyLocalities.forEach { locality ->
                try {
                    localityRepository.insertLocality(locality)
                } catch (e: Exception) {
                    Log.e("LocalityViewModel", "Error inserting locality: ${locality.name}", e)
                }
            }
        }
    }
    
    private fun createDummyLocalities(): List<Locality> {
        return listOf(
            Locality(
                id = "locality_1",
                name = "Bandra",
                city = "Mumbai",
                state = "Maharashtra",
                safetyRating = 4.5,
                transportRating = 4.7,
                schoolsRating = 4.6,
                reviewCount = 15,
                isVerified = true
            ),
            Locality(
                id = "locality_2",
                name = "Whitefield",
                city = "Bangalore",
                state = "Karnataka",
                safetyRating = 4.3,
                transportRating = 4.2,
                schoolsRating = 4.8,
                reviewCount = 12,
                isVerified = true
            ),
            Locality(
                id = "locality_3",
                name = "Koramangala",
                city = "Bangalore",
                state = "Karnataka",
                safetyRating = 4.4,
                transportRating = 4.5,
                schoolsRating = 4.4,
                reviewCount = 18,
                isVerified = true
            ),
            Locality(
                id = "locality_4",
                name = "Indiranagar",
                city = "Bangalore",
                state = "Karnataka",
                safetyRating = 4.2,
                transportRating = 4.1,
                schoolsRating = 4.3,
                reviewCount = 11,
                isVerified = true
            ),
            Locality(
                id = "locality_5",
                name = "Connaught Place",
                city = "Delhi",
                state = "Delhi",
                safetyRating = 4.1,
                transportRating = 4.6,
                schoolsRating = 4.2,
                reviewCount = 9,
                isVerified = false
            ),
            Locality(
                id = "locality_6",
                name = "Sector 7",
                city = "Chandigarh",
                state = "Chandigarh",
                safetyRating = 4.6,
                transportRating = 4.4,
                schoolsRating = 4.7,
                reviewCount = 14,
                isVerified = true
            ),
            Locality(
                id = "locality_7",
                name = "Jubilee Hills",
                city = "Hyderabad",
                state = "Telangana",
                safetyRating = 4.5,
                transportRating = 4.3,
                schoolsRating = 4.5,
                reviewCount = 16,
                isVerified = true
            ),
            Locality(
                id = "locality_8",
                name = "Salt Lake",
                city = "Kolkata",
                state = "West Bengal",
                safetyRating = 4.2,
                transportRating = 4.0,
                schoolsRating = 4.4,
                reviewCount = 8,
                isVerified = false
            )
        )
    }
}
