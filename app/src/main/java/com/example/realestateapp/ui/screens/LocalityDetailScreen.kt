package com.example.realestateapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realestateapp.data.entity.Locality
import com.example.realestateapp.data.entity.LocalityReview
import com.example.realestateapp.ui.viewmodel.LocalityViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalityDetailScreen(
    localityId: String,
    onBackClick: () -> Unit,
    localityViewModel: LocalityViewModel,
    userViewModel: UserViewModel
) {
    val locality by localityViewModel.selectedLocality.collectAsState()
    val reviews by localityViewModel.localityReviews.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    var showReviewDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(localityId) {
        localityViewModel.getLocalityById(localityId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locality Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (locality == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading locality details...")
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
                    // Locality Header
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = locality!!.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${locality!!.city}, ${locality!!.state}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            
                            if (locality!!.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    modifier = Modifier.height(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                item {
                    // Ratings Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Ratings Overview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            
                            RatingDetailRow(
                                label = "Safety",
                                rating = locality!!.safetyRating,
                                description = "Safe neighborhood"
                            )
                            RatingDetailRow(
                                label = "Transport",
                                rating = locality!!.transportRating,
                                description = "Public transport availability"
                            )
                            RatingDetailRow(
                                label = "Schools",
                                rating = locality!!.schoolsRating,
                                description = "Quality of schools"
                            )
                            
                            Text(
                                text = "${locality!!.reviewCount} reviews",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                item {
                    // Add Review Button
                    Button(
                        onClick = { showReviewDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Review")
                    }
                }
                
                item {
                    // Reviews Section
                    Text("Reviews (${reviews.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                items(reviews) { review ->
                    LocalityReviewItem(review = review)
                }
            }
        }
    }
    
    // Review Dialog
    if (showReviewDialog) {
        LocalityReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { safetyRating, transportRating, schoolsRating, comment ->
                currentUser?.let {
                    localityViewModel.addReview(
                        localityId,
                        it.id,
                        safetyRating,
                        transportRating,
                        schoolsRating,
                        comment
                    )
                    showReviewDialog = false
                }
            }
        )
    }
}

@Composable
fun RatingDetailRow(label: String, rating: Double, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        modifier = Modifier.height(16.dp),
                        tint = if (index < rating.toInt()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Text(
                    text = String.format("%.1f", rating),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = description,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LocalityReviewItem(review: LocalityReview) {
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
            // Ratings
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingBadge(label = "Safety", rating = review.safetyRating)
                RatingBadge(label = "Transport", rating = review.transportRating)
                RatingBadge(label = "Schools", rating = review.schoolsRating)
            }
            
            // Comment
            if (review.comment.isNotEmpty()) {
                Text(review.comment, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RatingBadge(label: String, rating: Double) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Star",
            modifier = Modifier.height(12.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(String.format("%.1f", rating), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LocalityReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Double, Double, Double, String) -> Unit
) {
    var safetyRating by remember { mutableStateOf(5.0) }
    var transportRating by remember { mutableStateOf(5.0) }
    var schoolsRating by remember { mutableStateOf(5.0) }
    var comment by remember { mutableStateOf("") }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Locality Review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Safety: $safetyRating")
                Text("Transport: $transportRating")
                Text("Schools: $schoolsRating")
                Text("Add your review to help others make informed decisions")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(safetyRating, transportRating, schoolsRating, comment)
                }
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
