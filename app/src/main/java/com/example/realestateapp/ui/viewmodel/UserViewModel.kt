package com.example.realestateapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.User
import com.example.realestateapp.data.repository.UserRepository
import com.example.realestateapp.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UserRepository
    private val sessionManager: SessionManager
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()
    
    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> = _registrationError.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        repository = UserRepository(database.userDao())
        sessionManager = SessionManager(application)
        
        // Check if user is already logged in from previous session
        restoreSessionIfExists()
    }
    
    private fun restoreSessionIfExists() {
        if (sessionManager.isLoggedIn()) {
            val userId = sessionManager.getUserId()
            if (userId != null) {
                viewModelScope.launch {
                    val user = withContext(Dispatchers.IO) {
                        repository.getUserById(userId)
                    }
                    if (user != null) {
                        _currentUser.value = user
                        Log.d("UserViewModel", "Session restored for user: ${user.username}")
                    } else {
                        // User was deleted or session is stale
                        sessionManager.clearSession()
                        Log.w("UserViewModel", "Session user not found in database, clearing session")
                    }
                }
            }
        }
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Attempting login for username: $username")
                
                val localUser = withContext(Dispatchers.IO) {
                    Log.d("UserViewModel", "Querying database for user: $username")
                    val user = repository.login(username, password)
                    Log.d("UserViewModel", "Query result: ${if (user != null) "User found" else "User not found"}")
                    user
                }
                
                if (localUser != null) {
                    Log.d("UserViewModel", "Login successful for user: ${localUser.username}")
                    _currentUser.value = localUser
                    _loginError.value = null
                    
                    // Save session to SharedPreferences
                    sessionManager.saveSession(
                        userId = localUser.id,
                        username = localUser.username,
                        name = localUser.name,
                        email = localUser.email,
                        phone = localUser.phone
                    )
                    Log.d("UserViewModel", "Session saved for user: ${localUser.username}")
                } else {
                    Log.w("UserViewModel", "Login failed: Invalid username or password")
                    _loginError.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Login failed with exception", e)
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
                    Log.d("UserViewModel", "User inserted, ID: ${newUser.id}")
                    
                    // Verify the user was saved by retrieving it
                    val savedUser = repository.getUserByUsername(username)
                    if (savedUser != null) {
                        Log.d("UserViewModel", "User verified in database: ${savedUser.username}")
                    } else {
                        Log.e("UserViewModel", "User NOT found in database after insert!")
                    }
                }
                Log.d("UserViewModel", "User saved to local database successfully")
                
                // Registration successful - automatically log in the user
                Log.d("UserViewModel", "Registration completed successfully, logging in user")
                _currentUser.value = newUser
                _registrationError.value = null
                
                // Save session to SharedPreferences
                sessionManager.saveSession(
                    userId = newUser.id,
                    username = newUser.username,
                    name = newUser.name,
                    email = newUser.email,
                    phone = newUser.phone
                )
                Log.d("UserViewModel", "Session saved for newly registered user: ${newUser.username}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Registration failed with exception", e)
                _registrationError.value = "Registration failed: ${e.message}"
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
        sessionManager.clearSession()
        Log.d("UserViewModel", "User logged out and session cleared")
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
