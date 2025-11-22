package com.example.realestateapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.ui.viewmodel.PropertyViewModel
import com.example.realestateapp.ui.viewmodel.TransactionViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel
import com.example.realestateapp.ui.viewmodel.LocalityViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onBackClick: () -> Unit,
    onBuyClick: () -> Unit,
    propertyViewModel: PropertyViewModel,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    localityViewModel: LocalityViewModel
) {
    val selectedProperty by propertyViewModel.selectedProperty.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    val transactionSuccess by transactionViewModel.transactionSuccess.collectAsState()
    val transactionError by transactionViewModel.transactionError.collectAsState()
    val selectedLocality by localityViewModel.selectedLocality.collectAsState()
    
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isProcessingPurchase by remember { mutableStateOf(false) }
    
    LaunchedEffect(propertyId) {
        propertyViewModel.getPropertyById(propertyId)
    }
    
    LaunchedEffect(selectedProperty) {
        selectedProperty?.let { property ->
            // Load locality data based on property's localityId
            if (property.localityId.isNotEmpty()) {
                localityViewModel.getLocalityByPropertyLocalityId(property.localityId)
            }
        }
    }
    
    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess) {
            isProcessingPurchase = false
            showSuccessDialog = true
            transactionViewModel.resetTransactionState()
        }
    }
    
    LaunchedEffect(transactionError) {
        if (transactionError != null) {
            isProcessingPurchase = false
            showErrorDialog = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedProperty == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val property = selectedProperty!!
            val isCurrentUserSeller = currentUser?.id == property.sellerId
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Property Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!property.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = property.imageUrl,
                            contentDescription = "Property image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "No Image Available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Property Title
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Text(
                    text = formatPrice(property.price),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Property Features
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PropertyDetailFeature(label = "Bedrooms", value = property.bedrooms.toString())
                    PropertyDetailFeature(label = "Bathrooms", value = property.bathrooms.toString())
                    PropertyDetailFeature(label = "Area", value = "${property.area} sq ft")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Address
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${property.address}, ${property.city}, ${property.zipCode}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Locality Insights
                Text(
                    text = "Locality Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (selectedLocality != null) {
                    val locality = selectedLocality!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header with name and verified badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = locality.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${locality.city}, ${locality.state}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                
                                if (locality.isVerified) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            modifier = Modifier.height(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Verified",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            // Ratings
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                LocalityRatingRow(label = "Safety", rating = locality.safetyRating)
                                LocalityRatingRow(label = "Transport", rating = locality.transportRating)
                                LocalityRatingRow(label = "Schools", rating = locality.schoolsRating)
                            }
                            
                            // Review count
                            Text(
                                text = "${locality.reviewCount} reviews",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Loading locality information...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date Posted
                Text(
                    text = "Listed on ${formatDate(property.datePosted)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buy Button
                if (!property.isSold && !isCurrentUserSeller && currentUser != null) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessingPurchase
                    ) {
                        if (isProcessingPurchase) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(16.dp).height(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processing...")
                            }
                        } else {
                            Text("Buy Property")
                        }
                    }
                } else if (property.isSold) {
                    Text(
                        text = "This property has been sold",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (isCurrentUserSeller) {
                    Text(
                        text = "You are the seller of this property",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else if (currentUser == null) {
                    Text(
                        text = "Please log in to buy this property",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Purchase") },
            text = { 
                Column {
                    Text("Are you sure you want to buy this property?")
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedProperty?.let { property ->
                        Text(
                            text = "Property: ${property.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Price: ${formatPrice(property.price)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Location: ${property.city}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        isProcessingPurchase = true
                        selectedProperty?.let { property ->
                            currentUser?.let { user ->
                                transactionViewModel.createTransaction(property, user.id)
                            }
                        }
                    }
                ) {
                    Text("Buy Now")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBuyClick()
            },
            title = { Text("Purchase Successful") },
            text = { Text("You have successfully purchased this property!") },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessDialog = false
                        onBuyClick()
                    }
                ) {
                    Text("View Transactions")
                }
            }
        )
    }
    
    // Error Dialog
    if (showErrorDialog && transactionError != null) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                transactionViewModel.resetTransactionState()
            },
            title = { Text("Error") },
            text = { Text(transactionError!!) },
            confirmButton = {
                Button(
                    onClick = { 
                        showErrorDialog = false
                        transactionViewModel.resetTransactionState()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun PropertyDetailFeature(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LocalityRatingRow(label: String, rating: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    modifier = Modifier.height(14.dp),
                    tint = if (index < rating.toInt()) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Text(
                text = String.format("%.1f", rating),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
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
