package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.User
import com.example.realestateapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UserRepository
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()
    
    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> = _registrationError.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        repository = UserRepository(database.userDao())
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val localUser = withContext(Dispatchers.IO) {
                    repository.login(username, password)
                }
                
                if (localUser != null) {
                    _currentUser.value = localUser
                    _loginError.value = null
                } else {
                    _loginError.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
            }
        }
    }
    
    fun register(username: String, name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Starting registration for username: $username")
                
                // Check local database for username
                val localUserByUsername = withContext(Dispatchers.IO) {
                    try {
                        repository.getUserByUsername(username)
                    } catch (e: Exception) {
                        Log.w("UserViewModel", "Error checking local username: ${e.message}")
                        null
                    }
                }
                if (localUserByUsername != null) {
                    Log.d("UserViewModel", "Username already exists in local database")
                    _registrationError.value = "Username already taken"
                    return@launch
                }
                
                Log.d("UserViewModel", "Username is available, proceeding with registration")
                
                val newUser = User(
                    username = username,
                    name = name,
                    email = email,
                    phone = phone,
                    password = password
                )
                
                // Save to local database
                Log.d("UserViewModel", "Saving user to local database")
                withContext(Dispatchers.IO) {
                    repository.insertUser(newUser)
                }
                Log.d("UserViewModel", "User saved to local database successfully")
                
                // Registration successful
                Log.d("UserViewModel", "Registration completed successfully")
                _currentUser.value = newUser
                _registrationError.value = null
            } catch (e: Exception) {
                Log.e("UserViewModel", "Registration failed with exception", e)
                _registrationError.value = "Registration failed: ${e.message}"
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
    }
    
    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            _currentUser.value = user
        }
    }
    
    fun getUserById(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                _currentUser.value = user
            }
        }
    }
    
    fun clearErrors() {
        _loginError.value = null
        _registrationError.value = null
    }
}
