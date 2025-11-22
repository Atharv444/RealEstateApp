package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.LocalityReviewDao
import com.example.realestateapp.data.entity.LocalityReview
import kotlinx.coroutines.flow.Flow

class LocalityReviewRepository(private val reviewDao: LocalityReviewDao) {
    
    suspend fun insertReview(review: LocalityReview): Long {
        return reviewDao.insertReview(review)
    }
    
    suspend fun updateReview(review: LocalityReview) {
        reviewDao.updateReview(review)
    }
    
    suspend fun deleteReview(review: LocalityReview) {
        reviewDao.deleteReview(review)
    }
    
    suspend fun getReviewById(reviewId: String): LocalityReview? {
        return reviewDao.getReviewById(reviewId)
    }
    
    fun getLocalityReviews(localityId: String): Flow<List<LocalityReview>> {
        return reviewDao.getLocalityReviews(localityId)
    }
    
    fun getUserReviews(userId: String): Flow<List<LocalityReview>> {
        return reviewDao.getUserReviews(userId)
    }
    
    suspend fun getAverageSafetyRating(localityId: String): Double? {
        return reviewDao.getAverageSafetyRating(localityId)
    }
    
    suspend fun getAverageTransportRating(localityId: String): Double? {
        return reviewDao.getAverageTransportRating(localityId)
    }
    
    suspend fun getAverageSchoolsRating(localityId: String): Double? {
        return reviewDao.getAverageSchoolsRating(localityId)
    }
    
    suspend fun getReviewCount(localityId: String): Int {
        return reviewDao.getReviewCount(localityId)
    }
    
    suspend fun getTotalReviewCount(): Int {
        return reviewDao.getTotalReviewCount()
    }
}
