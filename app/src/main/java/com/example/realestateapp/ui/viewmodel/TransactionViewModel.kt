package com.example.realestateapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.TransactionStatus
import com.example.realestateapp.data.repository.PropertyRepository
import com.example.realestateapp.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val transactionRepository: TransactionRepository
    private val propertyRepository: PropertyRepository
    
    private val _userTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val userTransactions: StateFlow<List<Transaction>> = _userTransactions.asStateFlow()
    
    private val _transactionError = MutableStateFlow<String?>(null)
    val transactionError: StateFlow<String?> = _transactionError.asStateFlow()
    
    private val _transactionSuccess = MutableStateFlow<Boolean>(false)
    val transactionSuccess: StateFlow<Boolean> = _transactionSuccess.asStateFlow()
    
    init {
        val database = RealEstateDatabase.getDatabase(application)
        transactionRepository = TransactionRepository(database.transactionDao())
        propertyRepository = PropertyRepository(database.propertyDao())
    }
    
    fun createTransaction(property: Property, buyerId: String) {
        viewModelScope.launch {
            try {
                // Validation checks
                if (property.isSold) {
                    _transactionError.value = "Property is already sold"
                    return@launch
                }
                
                if (buyerId == property.sellerId) {
                    _transactionError.value = "You cannot buy your own property"
                    return@launch
                }
                
                if (buyerId.isEmpty()) {
                    _transactionError.value = "User not logged in"
                    return@launch
                }
                
                // Check property availability from local database
                val currentProperty = withContext(Dispatchers.IO) {
                    propertyRepository.getPropertyById(property.id)
                }
                
                if (currentProperty == null) {
                    _transactionError.value = "Property not found"
                    return@launch
                }
                
                if (currentProperty.isSold) {
                    _transactionError.value = "Property was just sold by another buyer"
                    return@launch
                }
                
                // Create transaction
                val transaction = Transaction(
                    propertyId = property.id,
                    buyerId = buyerId,
                    sellerId = property.sellerId,
                    price = property.price,
                    status = TransactionStatus.COMPLETED
                )
                
                // Save to local database
                val localTransactionId = withContext(Dispatchers.IO) {
                    transactionRepository.insertTransaction(transaction)
                }
                
                // Update property status
                val updatedProperty = currentProperty.copy(isSold = true)
                
                withContext(Dispatchers.IO) {
                    propertyRepository.updateProperty(updatedProperty)
                }
                
                _transactionSuccess.value = true
                _transactionError.value = null
                
            } catch (e: Exception) {
                _transactionError.value = "Failed to complete purchase: ${e.message}"
            }
        }
    }
    
    fun getUserTransactions(userId: String) {
        viewModelScope.launch {
            try {
                // Load from local database
                transactionRepository.getTransactionsByUser(userId).collectLatest { transactions ->
                    _userTransactions.value = transactions
                }
            } catch (e: Exception) {
                _transactionError.value = "Failed to load transactions: ${e.message}"
            }
        }
    }
    
    fun updateTransactionStatus(transactionId: String, status: TransactionStatus) {
        viewModelScope.launch {
            val transaction = transactionRepository.getTransactionById(transactionId)
            transaction?.let {
                val updatedTransaction = it.copy(status = status)
                transactionRepository.updateTransactionStatus(updatedTransaction)
                
                // If transaction is cancelled, update property status back to available
                if (status == TransactionStatus.CANCELLED) {
                    val property = propertyRepository.getPropertyById(it.propertyId)
                    property?.let { prop ->
                        val updatedProperty = prop.copy(isSold = false)
                        propertyRepository.updateProperty(updatedProperty)
                    }
                }
            }
        }
    }
    
    fun resetTransactionState() {
        _transactionSuccess.value = false
        _transactionError.value = null
    }
}
