package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.ServiceReview
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ServiceReview): Long

    @Update
    suspend fun updateReview(review: ServiceReview)

    @Delete
    suspend fun deleteReview(review: ServiceReview)

    @Query("SELECT * FROM service_reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: String): ServiceReview?

    @Query("SELECT * FROM service_reviews WHERE serviceId = :serviceId ORDER BY reviewDate DESC")
    fun getServiceReviews(serviceId: String): Flow<List<ServiceReview>>

    @Query("SELECT * FROM service_reviews WHERE userId = :userId ORDER BY reviewDate DESC")
    fun getUserReviews(userId: String): Flow<List<ServiceReview>>

    @Query("SELECT AVG(rating) FROM service_reviews WHERE serviceId = :serviceId")
    suspend fun getAverageRating(serviceId: String): Double?

    @Query("SELECT COUNT(*) FROM service_reviews WHERE serviceId = :serviceId")
    suspend fun getReviewCount(serviceId: String): Int

    @Query("SELECT COUNT(*) FROM service_reviews")
    suspend fun getTotalReviewCount(): Int
}
