package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.UserDao
import com.example.realestateapp.data.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
    
    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
}
