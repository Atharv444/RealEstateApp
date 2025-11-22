package com.example.realestateapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realestateapp.data.entity.Service
import com.example.realestateapp.data.entity.ServiceReview
import com.example.realestateapp.ui.viewmodel.ServiceViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    serviceViewModel: ServiceViewModel,
    userViewModel: UserViewModel
) {
    val service by serviceViewModel.selectedService.collectAsState()
    val reviews by serviceViewModel.serviceReviews.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    var showBookingDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(serviceId) {
        serviceViewModel.getServiceById(serviceId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (service == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading service details...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Service Image
                    AsyncImage(
                        model = service!!.imageUrl,
                        contentDescription = service!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                item {
                    // Service Header
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = service!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = service!!.category.name.replace("_", " "),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        // Rating
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    modifier = Modifier.height(20.dp),
                                    tint = if (index < service!!.rating.toInt()) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            Text(
                                text = "${service!!.rating} (${service!!.reviewCount} reviews)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                item {
                    // Price
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Price", fontSize = 16.sp)
                            Text(
                                text = "₹${service!!.price}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                item {
                    // Description
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Description", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(service!!.description, fontSize = 14.sp)
                    }
                }
                
                item {
                    // Provider Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Provider Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            
                            Text(
                                text = "Name: ${service!!.providerName}",
                                fontSize = 14.sp
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Phone")
                                Text(service!!.providerPhone, fontSize = 14.sp)
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Email, contentDescription = "Email")
                                Text(service!!.providerEmail, fontSize = 14.sp)
                            }
                        }
                    }
                }
                
                item {
                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showBookingDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Book Service")
                        }
                        
                        OutlinedButton(
                            onClick = { showReviewDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Review")
                        }
                    }
                }
                
                item {
                    // Reviews Section
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Reviews (${reviews.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        
                        if (reviews.isEmpty()) {
                            Text("No reviews yet", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                
                items(reviews) { review ->
                    ReviewItem(review = review)
                }
            }
        }
    }
    
    // Booking Dialog
    if (showBookingDialog) {
        BookingDialog(
            onDismiss = { showBookingDialog = false },
            onBook = { scheduledDate, notes ->
                currentUser?.let {
                    serviceViewModel.bookService(serviceId, it.id, scheduledDate, notes)
                    showBookingDialog = false
                    onBookClick()
                }
            }
        )
    }
    
    // Review Dialog
    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                currentUser?.let {
                    serviceViewModel.addReview(serviceId, it.id, rating, comment)
                    showReviewDialog = false
                }
            }
        )
    }
}

@Composable
fun ReviewItem(review: ServiceReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        modifier = Modifier.height(16.dp),
                        tint = if (index < review.rating.toInt()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            
            Text(review.comment, fontSize = 13.sp)
        }
    }
}

@Composable
fun BookingDialog(
    onDismiss: () -> Unit,
    onBook: (Long, String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Service") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select a date and add any special requests")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onBook(System.currentTimeMillis(), notes)
                }
            ) {
                Text("Book")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Double, String) -> Unit
) {
    var rating by remember { mutableStateOf(5.0) }
    var comment by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rating: $rating")
                // Rating slider would go here
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(rating, comment)
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
