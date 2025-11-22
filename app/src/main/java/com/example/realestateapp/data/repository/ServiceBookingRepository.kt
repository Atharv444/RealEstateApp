package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.ServiceBookingDao
import com.example.realestateapp.data.entity.ServiceBooking
import com.example.realestateapp.data.entity.BookingStatus
import kotlinx.coroutines.flow.Flow

class ServiceBookingRepository(private val bookingDao: ServiceBookingDao) {
    
    suspend fun insertBooking(booking: ServiceBooking): Long {
        return bookingDao.insertBooking(booking)
    }
    
    suspend fun updateBooking(booking: ServiceBooking) {
        bookingDao.updateBooking(booking)
    }
    
    suspend fun deleteBooking(booking: ServiceBooking) {
        bookingDao.deleteBooking(booking)
    }
    
    suspend fun getBookingById(bookingId: String): ServiceBooking? {
        return bookingDao.getBookingById(bookingId)
    }
    
    fun getUserBookings(userId: String): Flow<List<ServiceBooking>> {
        return bookingDao.getUserBookings(userId)
    }
    
    fun getServiceBookings(serviceId: String): Flow<List<ServiceBooking>> {
        return bookingDao.getServiceBookings(serviceId)
    }
    
    fun getBookingsByStatus(status: BookingStatus): Flow<List<ServiceBooking>> {
        return bookingDao.getBookingsByStatus(status)
    }
    
    suspend fun getBookingCount(): Int {
        return bookingDao.getBookingCount()
    }
}
