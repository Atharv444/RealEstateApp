package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.LocalityReview
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalityReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: LocalityReview): Long

    @Update
    suspend fun updateReview(review: LocalityReview)

    @Delete
    suspend fun deleteReview(review: LocalityReview)

    @Query("SELECT * FROM locality_reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: String): LocalityReview?

    @Query("SELECT * FROM locality_reviews WHERE localityId = :localityId ORDER BY reviewDate DESC")
    fun getLocalityReviews(localityId: String): Flow<List<LocalityReview>>

    @Query("SELECT * FROM locality_reviews WHERE userId = :userId ORDER BY reviewDate DESC")
    fun getUserReviews(userId: String): Flow<List<LocalityReview>>

    @Query("SELECT AVG(safetyRating) FROM locality_reviews WHERE localityId = :localityId")
    suspend fun getAverageSafetyRating(localityId: String): Double?

    @Query("SELECT AVG(transportRating) FROM locality_reviews WHERE localityId = :localityId")
    suspend fun getAverageTransportRating(localityId: String): Double?

    @Query("SELECT AVG(schoolsRating) FROM locality_reviews WHERE localityId = :localityId")
    suspend fun getAverageSchoolsRating(localityId: String): Double?

    @Query("SELECT COUNT(*) FROM locality_reviews WHERE localityId = :localityId")
    suspend fun getReviewCount(localityId: String): Int

    @Query("SELECT COUNT(*) FROM locality_reviews")
    suspend fun getTotalReviewCount(): Int
}
