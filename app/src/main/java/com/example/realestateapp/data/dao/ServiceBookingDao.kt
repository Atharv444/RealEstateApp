package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.ServiceBooking
import com.example.realestateapp.data.entity.BookingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceBookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: ServiceBooking): Long

    @Update
    suspend fun updateBooking(booking: ServiceBooking)

    @Delete
    suspend fun deleteBooking(booking: ServiceBooking)

    @Query("SELECT * FROM service_bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): ServiceBooking?

    @Query("SELECT * FROM service_bookings WHERE userId = :userId ORDER BY bookingDate DESC")
    fun getUserBookings(userId: String): Flow<List<ServiceBooking>>

    @Query("SELECT * FROM service_bookings WHERE serviceId = :serviceId ORDER BY bookingDate DESC")
    fun getServiceBookings(serviceId: String): Flow<List<ServiceBooking>>

    @Query("SELECT * FROM service_bookings WHERE status = :status ORDER BY bookingDate DESC")
    fun getBookingsByStatus(status: BookingStatus): Flow<List<ServiceBooking>>

    @Query("SELECT COUNT(*) FROM service_bookings")
    suspend fun getBookingCount(): Int
}
