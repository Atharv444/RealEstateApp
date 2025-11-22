package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.ServiceReviewDao
import com.example.realestateapp.data.entity.ServiceReview
import kotlinx.coroutines.flow.Flow

class ServiceReviewRepository(private val reviewDao: ServiceReviewDao) {
    
    suspend fun insertReview(review: ServiceReview): Long {
        return reviewDao.insertReview(review)
    }
    
    suspend fun updateReview(review: ServiceReview) {
        reviewDao.updateReview(review)
    }
    
    suspend fun deleteReview(review: ServiceReview) {
        reviewDao.deleteReview(review)
    }
    
    suspend fun getReviewById(reviewId: String): ServiceReview? {
        return reviewDao.getReviewById(reviewId)
    }
    
    fun getServiceReviews(serviceId: String): Flow<List<ServiceReview>> {
        return reviewDao.getServiceReviews(serviceId)
    }
    
    fun getUserReviews(userId: String): Flow<List<ServiceReview>> {
        return reviewDao.getUserReviews(userId)
    }
    
    suspend fun getAverageRating(serviceId: String): Double? {
        return reviewDao.getAverageRating(serviceId)
    }
    
    suspend fun getReviewCount(serviceId: String): Int {
        return reviewDao.getReviewCount(serviceId)
    }
    
    suspend fun getTotalReviewCount(): Int {
        return reviewDao.getTotalReviewCount()
    }
}
