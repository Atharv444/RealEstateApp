package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.LocalityDao
import com.example.realestateapp.data.entity.Locality
import kotlinx.coroutines.flow.Flow

class LocalityRepository(private val localityDao: LocalityDao) {
    
    suspend fun insertLocality(locality: Locality): Long {
        return localityDao.insertLocality(locality)
    }
    
    suspend fun updateLocality(locality: Locality) {
        localityDao.updateLocality(locality)
    }
    
    suspend fun deleteLocality(locality: Locality) {
        localityDao.deleteLocality(locality)
    }
    
    suspend fun getLocalityById(localityId: String): Locality? {
        return localityDao.getLocalityById(localityId)
    }
    
    suspend fun getLocalityByNameAndCity(name: String, city: String): Locality? {
        return localityDao.getLocalityByNameAndCity(name, city)
    }
    
    fun getLocalitiesByCity(city: String): Flow<List<Locality>> {
        return localityDao.getLocalitiesByCity(city)
    }
    
    fun getAllLocalities(): Flow<List<Locality>> {
        return localityDao.getAllLocalities()
    }
    
    fun getVerifiedLocalities(): Flow<List<Locality>> {
        return localityDao.getVerifiedLocalities()
    }
    
    fun searchLocalities(searchQuery: String): Flow<List<Locality>> {
        return localityDao.searchLocalities(searchQuery)
    }
    
    suspend fun getLocalityCount(): Int {
        return localityDao.getLocalityCount()
    }
}
