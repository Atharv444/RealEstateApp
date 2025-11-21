package com.example.realestateapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): Transaction?

    @Query("SELECT * FROM transactions WHERE buyerId = :userId OR sellerId = :userId")
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE propertyId = :propertyId")
    fun getTransactionsByProperty(propertyId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE status = :status")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>>

    @Update
    suspend fun updateTransactionStatus(transaction: Transaction)
}
