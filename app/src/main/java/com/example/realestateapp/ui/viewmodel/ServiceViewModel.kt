package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Service
import com.example.realestateapp.data.entity.ServiceCategory
import com.example.realestateapp.data.entity.ServiceBooking
import com.example.realestateapp.data.entity.BookingStatus
import com.example.realestateapp.data.entity.ServiceReview
import com.example.realestateapp.data.repository.ServiceRepository
import com.example.realestateapp.data.repository.ServiceBookingRepository
import com.example.realestateapp.data.repository.ServiceReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    
    private val serviceRepository: ServiceRepository
    private val bookingRepository: ServiceBookingRepository
    private val reviewRepository: ServiceReviewRepository
    
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()
    
    private val _selectedService = MutableStateFlow<Service?>(null)
    val selectedService: StateFlow<Service?> = _selectedService.asStateFlow()
    
    private val _userBookings = MutableStateFlow<List<ServiceBooking>>(emptyList())
    val userBookings: StateFlow<List<ServiceBooking>> = _userBookings.asStateFlow()
    
    private val _serviceReviews = MutableStateFlow<List<ServiceReview>>(emptyList())
    val serviceReviews: StateFlow<List<ServiceReview>> = _serviceReviews.asStateFlow()
    
    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError: StateFlow<String?> = _bookingError.asStateFlow()
    
    private val _bookingSuccess = MutableStateFlow<Boolean>(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        serviceRepository = ServiceRepository(database.serviceDao())
        bookingRepository = ServiceBookingRepository(database.serviceBookingDao())
        reviewRepository = ServiceReviewRepository(database.serviceReviewDao())
        loadServices()
    }
    
    fun loadServices() {
        viewModelScope.launch {
            try {
                Log.d("ServiceViewModel", "Loading all services...")
                serviceRepository.getAllAvailableServices().collectLatest { serviceList ->
                    Log.d("ServiceViewModel", "Loaded ${serviceList.size} services")
                    _services.value = serviceList
                    
                    if (serviceList.isEmpty()) {
                        Log.d("ServiceViewModel", "No services found, seeding dummy data...")
                        seedDummyServices()
                    }
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error loading services", e)
            }
        }
    }
    
    fun getServiceById(serviceId: String) {
        viewModelScope.launch {
            try {
                val service = withContext(Dispatchers.IO) {
                    serviceRepository.getServiceById(serviceId)
                }
                _selectedService.value = service
                
                // Load reviews for this service
                if (service != null) {
                    loadServiceReviews(serviceId)
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error loading service", e)
            }
        }
    }
    
    fun getServicesByCategory(category: ServiceCategory) {
        viewModelScope.launch {
            try {
                serviceRepository.getServicesByCategory(category).collectLatest { serviceList ->
                    _services.value = serviceList
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error loading services by category", e)
            }
        }
    }
    
    fun searchServices(query: String) {
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    loadServices()
                } else {
                    serviceRepository.searchServices(query).collectLatest { serviceList ->
                        _services.value = serviceList
                    }
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error searching services", e)
            }
        }
    }
    
    fun bookService(serviceId: String, userId: String, scheduledDate: Long, notes: String = "") {
        viewModelScope.launch {
            try {
                val booking = ServiceBooking(
                    serviceId = serviceId,
                    userId = userId,
                    scheduledDate = scheduledDate,
                    status = BookingStatus.PENDING,
                    notes = notes
                )
                
                withContext(Dispatchers.IO) {
                    bookingRepository.insertBooking(booking)
                }
                
                _bookingSuccess.value = true
                _bookingError.value = null
                Log.d("ServiceViewModel", "Service booked successfully")
            } catch (e: Exception) {
                _bookingError.value = "Failed to book service: ${e.message}"
                Log.e("ServiceViewModel", "Error booking service", e)
            }
        }
    }
    
    fun getUserBookings(userId: String) {
        viewModelScope.launch {
            try {
                bookingRepository.getUserBookings(userId).collectLatest { bookings ->
                    _userBookings.value = bookings
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error loading user bookings", e)
            }
        }
    }
    
    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                val booking = withContext(Dispatchers.IO) {
                    bookingRepository.getBookingById(bookingId)
                }
                
                booking?.let {
                    val updatedBooking = it.copy(status = BookingStatus.CANCELLED)
                    withContext(Dispatchers.IO) {
                        bookingRepository.updateBooking(updatedBooking)
                    }
                    Log.d("ServiceViewModel", "Booking cancelled successfully")
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error cancelling booking", e)
            }
        }
    }
    
    fun addReview(serviceId: String, userId: String, rating: Double, comment: String) {
        viewModelScope.launch {
            try {
                val review = ServiceReview(
                    serviceId = serviceId,
                    userId = userId,
                    rating = rating.coerceIn(0.0, 5.0),
                    comment = comment
                )
                
                withContext(Dispatchers.IO) {
                    reviewRepository.insertReview(review)
                    
                    // Update service rating
                    val avgRating = reviewRepository.getAverageRating(serviceId) ?: 0.0
                    val reviewCount = reviewRepository.getReviewCount(serviceId)
                    
                    val service = serviceRepository.getServiceById(serviceId)
                    service?.let {
                        val updatedService = it.copy(rating = avgRating, reviewCount = reviewCount)
                        serviceRepository.updateService(updatedService)
                    }
                }
                
                loadServiceReviews(serviceId)
                Log.d("ServiceViewModel", "Review added successfully")
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error adding review", e)
            }
        }
    }
    
    fun loadServiceReviews(serviceId: String) {
        viewModelScope.launch {
            try {
                reviewRepository.getServiceReviews(serviceId).collectLatest { reviews ->
                    _serviceReviews.value = reviews
                }
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Error loading reviews", e)
            }
        }
    }
    
    fun resetBookingState() {
        _bookingSuccess.value = false
        _bookingError.value = null
    }
    
    private suspend fun seedDummyServices() {
        withContext(Dispatchers.IO) {
            val dummyServices = createDummyServices()
            dummyServices.forEach { service ->
                try {
                    serviceRepository.insertService(service)
                } catch (e: Exception) {
                    Log.e("ServiceViewModel", "Error inserting service: ${service.name}", e)
                }
            }
        }
    }
    
    private fun createDummyServices(): List<Service> {
        return listOf(
            Service(
                id = "service_1",
                name = "Express Packers & Movers",
                category = ServiceCategory.PACKERS_MOVERS,
                description = "Professional packing and moving services with experienced team",
                price = 5000.0,
                rating = 4.8,
                reviewCount = 45,
                providerName = "Raj Kumar",
                providerPhone = "9876543210",
                providerEmail = "raj@packers.com",
                imageUrl = "https://images.unsplash.com/photo-1552664730-d307ca884978?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_2",
                name = "Sparkle Cleaning Services",
                category = ServiceCategory.CLEANING,
                description = "Professional home and office cleaning with eco-friendly products",
                price = 2000.0,
                rating = 4.6,
                reviewCount = 32,
                providerName = "Priya Singh",
                providerPhone = "9876543211",
                providerEmail = "priya@sparkle.com",
                imageUrl = "https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_3",
                name = "Legal Consultants Pro",
                category = ServiceCategory.LEGAL_HELP,
                description = "Expert legal advice for property and real estate matters",
                price = 3000.0,
                rating = 4.9,
                reviewCount = 28,
                providerName = "Advocate Sharma",
                providerPhone = "9876543212",
                providerEmail = "sharma@legal.com",
                imageUrl = "https://images.unsplash.com/photo-1589829085787-46c078269b51?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_4",
                name = "Premium Painting Solutions",
                category = ServiceCategory.PAINTING,
                description = "Interior and exterior painting with premium quality paints",
                price = 4500.0,
                rating = 4.7,
                reviewCount = 38,
                providerName = "Arjun Patel",
                providerPhone = "9876543213",
                providerEmail = "arjun@painting.com",
                imageUrl = "https://images.unsplash.com/photo-1578926078328-123456789012?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_5",
                name = "Expert Plumbing Services",
                category = ServiceCategory.PLUMBING,
                description = "24/7 emergency plumbing services with quick response",
                price = 1500.0,
                rating = 4.5,
                reviewCount = 52,
                providerName = "Vikram Singh",
                providerPhone = "9876543214",
                providerEmail = "vikram@plumbing.com",
                imageUrl = "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_6",
                name = "Professional Electrical Works",
                category = ServiceCategory.ELECTRICAL,
                description = "Licensed electricians for all electrical installations and repairs",
                price = 2500.0,
                rating = 4.8,
                reviewCount = 41,
                providerName = "Suresh Kumar",
                providerPhone = "9876543215",
                providerEmail = "suresh@electrical.com",
                imageUrl = "https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_7",
                name = "Master Carpentry",
                category = ServiceCategory.CARPENTRY,
                description = "Custom carpentry and furniture making services",
                price = 6000.0,
                rating = 4.9,
                reviewCount = 35,
                providerName = "Ramesh Verma",
                providerPhone = "9876543216",
                providerEmail = "ramesh@carpentry.com",
                imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",
                isAvailable = true
            ),
            Service(
                id = "service_8",
                name = "Pest Control Experts",
                category = ServiceCategory.PEST_CONTROL,
                description = "Comprehensive pest control and termite treatment services",
                price = 3500.0,
                rating = 4.6,
                reviewCount = 29,
                providerName = "Anita Desai",
                providerPhone = "9876543217",
                providerEmail = "anita@pestcontrol.com",
                imageUrl = "https://images.unsplash.com/photo-1584622181563-430f63602d4b?w=400",
                isAvailable = true
            )
        )
    }
}
