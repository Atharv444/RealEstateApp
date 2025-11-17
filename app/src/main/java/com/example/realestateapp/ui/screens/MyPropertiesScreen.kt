package com.example.realestateapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.realestateapp.ui.viewmodel.PropertyViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPropertiesScreen(
    onPropertyClick: (String) -> Unit,
    onBackClick: () -> Unit,
    propertyViewModel: PropertyViewModel,
    userViewModel: UserViewModel
) {
    val properties by propertyViewModel.properties.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            propertyViewModel.getPropertiesBySeller(user.id)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Properties") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't listed any properties yet")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(properties) { property ->
                    PropertyItem(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}
