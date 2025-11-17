package com.example.realestateapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.TransactionStatus
import com.example.realestateapp.ui.viewmodel.TransactionViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel
) {
    val transactions by transactionViewModel.userTransactions.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            transactionViewModel.getUserTransactions(user.id)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Transactions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions found")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        isUserBuyer = transaction.buyerId == currentUser?.id
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    isUserBuyer: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isUserBuyer) "Purchase" else "Sale",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                val statusColor = when (transaction.status) {
                    TransactionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    TransactionStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                    TransactionStatus.CANCELLED -> MaterialTheme.colorScheme.error
                }
                
                Text(
                    text = transaction.status.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Property ID: ${transaction.propertyId}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = formatPrice(transaction.price),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Transaction Date",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatDate(transaction.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Transaction ID",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = transaction.id.take(8),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(price)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
