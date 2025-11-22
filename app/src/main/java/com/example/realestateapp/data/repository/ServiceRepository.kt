package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.ServiceDao
import com.example.realestateapp.data.entity.Service
import com.example.realestateapp.data.entity.ServiceCategory
import kotlinx.coroutines.flow.Flow

class ServiceRepository(private val serviceDao: ServiceDao) {
    
    suspend fun insertService(service: Service): Long {
        return serviceDao.insertService(service)
    }
    
    suspend fun updateService(service: Service) {
        serviceDao.updateService(service)
    }
    
    suspend fun deleteService(service: Service) {
        serviceDao.deleteService(service)
    }
    
    suspend fun getServiceById(serviceId: String): Service? {
        return serviceDao.getServiceById(serviceId)
    }
    
    fun getAllAvailableServices(): Flow<List<Service>> {
        return serviceDao.getAllAvailableServices()
    }
    
    suspend fun getAllServices(): List<Service> {
        return serviceDao.getAllServices()
    }
    
    fun getServicesByCategory(category: ServiceCategory): Flow<List<Service>> {
        return serviceDao.getServicesByCategory(category)
    }
    
    fun searchServices(searchQuery: String): Flow<List<Service>> {
        return serviceDao.searchServices(searchQuery)
    }
    
    suspend fun getServiceCount(): Int {
        return serviceDao.getServiceCount()
    }
}
