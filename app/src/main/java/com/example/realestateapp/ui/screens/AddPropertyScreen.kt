package com.example.realestateapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.ui.viewmodel.PropertyViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    onPropertyAdded: () -> Unit,
    onCancelClick: () -> Unit,
    propertyViewModel: PropertyViewModel,
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val isUploadingImage by propertyViewModel.isUploadingImage.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }
    
    var titleError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var zipCodeError by remember { mutableStateOf<String?>(null) }
    var bedroomsError by remember { mutableStateOf<String?>(null) }
    var bathroomsError by remember { mutableStateOf<String?>(null) }
    var areaError by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Property") },
                navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    titleError = null
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = price,
                onValueChange = { 
                    price = it
                    priceError = null
                },
                label = { Text("Price ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = priceError != null,
                supportingText = { priceError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = address,
                onValueChange = { 
                    address = it
                    addressError = null
                },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = addressError != null,
                supportingText = { addressError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = city,
                onValueChange = { 
                    city = it
                    cityError = null
                },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                isError = cityError != null,
                supportingText = { cityError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = zipCode,
                onValueChange = { 
                    zipCode = it
                    zipCodeError = null
                },
                label = { Text("Zip Code") },
                modifier = Modifier.fillMaxWidth(),
                isError = zipCodeError != null,
                supportingText = { zipCodeError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = bedrooms,
                onValueChange = { 
                    bedrooms = it
                    bedroomsError = null
                },
                label = { Text("Bedrooms") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = bedroomsError != null,
                supportingText = { bedroomsError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = bathrooms,
                onValueChange = { 
                    bathrooms = it
                    bathroomsError = null
                },
                label = { Text("Bathrooms") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = bathroomsError != null,
                supportingText = { bathroomsError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = area,
                onValueChange = { 
                    area = it
                    areaError = null
                },
                label = { Text("Area (sq ft)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = areaError != null,
                supportingText = { areaError?.let { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Image picker section
            Text(
                text = "Property Image",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected property image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Remove image button
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack, // Using ArrowBack as close icon
                                    contentDescription = "Remove image",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add photo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to select image from files",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "JPG, PNG, WEBP supported",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            if (selectedImageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove Image")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = {
                        if (isUploadingImage) return@Button
                        // Validate inputs
                        var isValid = true
                        
                        if (title.isBlank()) {
                            titleError = "Title is required"
                            isValid = false
                        }
                        
                        if (price.isBlank()) {
                            priceError = "Price is required"
                            isValid = false
                        }
                        
                        if (address.isBlank()) {
                            addressError = "Address is required"
                            isValid = false
                        }
                        
                        if (city.isBlank()) {
                            cityError = "City is required"
                            isValid = false
                        }
                        
                        if (zipCode.isBlank()) {
                            zipCodeError = "Zip code is required"
                            isValid = false
                        }
                        
                        if (bedrooms.isBlank()) {
                            bedroomsError = "Bedrooms is required"
                            isValid = false
                        }
                        
                        if (bathrooms.isBlank()) {
                            bathroomsError = "Bathrooms is required"
                            isValid = false
                        }
                        
                        if (area.isBlank()) {
                            areaError = "Area is required"
                            isValid = false
                        }
                        
                        if (isValid) {
                            val newProperty = Property(
                                title = title,
                                description = description,
                                price = price.toDoubleOrNull() ?: 0.0,
                                address = address,
                                city = city,
                                zipCode = zipCode,
                                bedrooms = bedrooms.toIntOrNull() ?: 0,
                                bathrooms = bathrooms.toIntOrNull() ?: 0,
                                area = area.toDoubleOrNull() ?: 0.0,
                                sellerId = currentUser?.id ?: ""
                            )
                            
                            propertyViewModel.addPropertyWithImage(newProperty, selectedImageUri)
                            onPropertyAdded()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isUploadingImage
                ) {
                    if (isUploadingImage) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Uploading...")
                        }
                    } else {
                        Text("Add Property")
                    }
                }
            }
        }
    }
}
