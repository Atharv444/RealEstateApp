package com.example.realestateapp.data.repository

import com.example.realestateapp.data.dao.TransactionDao
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.TransactionStatus
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun getTransactionById(transactionId: String): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }
    
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUser(userId)
    }
    
    fun getTransactionsByProperty(propertyId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByProperty(propertyId)
    }
    
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByStatus(status)
    }
    
    suspend fun updateTransactionStatus(transaction: Transaction) {
        transactionDao.updateTransactionStatus(transaction)
    }
}
