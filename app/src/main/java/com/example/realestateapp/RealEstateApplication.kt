package com.example.realestateapp

import android.app.Application
import android.util.Log
import com.example.realestateapp.data.RealEstateDatabase
import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.repository.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RealEstateApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Seed dummy data if needed
        seedDummyDataIfNeeded()
    }
    
    private fun seedDummyDataIfNeeded() {
        applicationScope.launch {
            try {
                Log.d("RealEstateApp", "Checking if local dummy data seeding is needed...")
                
                val database = RealEstateDatabase.getDatabase(this@RealEstateApplication)
                val propertyRepository = PropertyRepository(database.propertyDao())
                
                // Check if local database has properties
                val propertyCount = propertyRepository.getPropertyCount()
                Log.d("RealEstateApp", "Existing properties count: $propertyCount")
                
                val existingProperties = propertyRepository.getAllProperties()
                Log.d("RealEstateApp", "Retrieved ${existingProperties.size} properties from database")
                
                if (existingProperties.isEmpty()) {
                    Log.d("RealEstateApp", "Seeding local dummy data...")
                    seedLocalDummyProperties(propertyRepository)
                    Log.d("RealEstateApp", "Local dummy data seeding completed")
                } else {
                    Log.d("RealEstateApp", "Local data already exists, skipping seeding")
                }
            } catch (e: Exception) {
                Log.e("RealEstateApp", "Error during local data seeding", e)
            }
        }
    }
    
    private suspend fun seedLocalDummyProperties(repository: PropertyRepository) {
        Log.d("RealEstateApp", "Starting to seed dummy properties...")
        val dummyProperties = listOf(
            Property(
                id = "app_prop_1",
                title = "Modern Downtown Apartment",
                description = "Beautiful 2-bedroom apartment in the heart of downtown with stunning city views. Features include hardwood floors, stainless steel appliances, and a private balcony.",
                price = 450000.0,
                address = "123 Main Street",
                city = "New York",
                zipCode = "10001",
                bedrooms = 2,
                bathrooms = 2,
                area = 1200.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "app_seller_1",
                isSold = false
            ),
            Property(
                id = "app_prop_2",
                title = "Cozy Suburban House",
                description = "Charming 3-bedroom family home in quiet neighborhood. Large backyard, updated kitchen, and close to excellent schools.",
                price = 325000.0,
                address = "456 Oak Avenue",
                city = "Austin",
                zipCode = "78701",
                bedrooms = 3,
                bathrooms = 2,
                area = 1800.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "app_seller_2",
                isSold = false
            ),
            Property(
                id = "app_prop_3",
                title = "Luxury Penthouse Suite",
                description = "Exclusive penthouse with panoramic ocean views. Premium finishes throughout, private elevator access, and rooftop terrace.",
                price = 1250000.0,
                address = "789 Ocean Drive",
                city = "Miami",
                zipCode = "33139",
                bedrooms = 4,
                bathrooms = 3,
                area = 2500.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "app_seller_3",
                isSold = false
            ),
            Property(
                id = "app_prop_4",
                title = "Historic Brownstone",
                description = "Beautifully restored 19th-century brownstone with original architectural details. High ceilings, exposed brick, and modern amenities.",
                price = 875000.0,
                address = "321 Heritage Lane",
                city = "Boston",
                zipCode = "02101",
                bedrooms = 3,
                bathrooms = 3,
                area = 2200.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "app_seller_4",
                isSold = false
            ),
            Property(
                id = "app_prop_5",
                title = "Beachfront Condo",
                description = "Stunning oceanfront condominium with direct beach access. Floor-to-ceiling windows, open concept living, and resort-style amenities.",
                price = 695000.0,
                address = "987 Seaside Boulevard",
                city = "San Diego",
                zipCode = "92101",
                bedrooms = 2,
                bathrooms = 2,
                area = 1400.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "app_seller_5",
                isSold = false
            ),
            Property(
                id = "app_prop_6",
                title = "Mountain View Cabin",
                description = "Rustic log cabin with breathtaking mountain views. Perfect retreat with stone fireplace, wrap-around deck, and hiking trails nearby.",
                price = 285000.0,
                address = "654 Pine Ridge Road",
                city = "Denver",
                zipCode = "80201",
                bedrooms = 2,
                bathrooms = 1,
                area = 1100.0,
                imageUrl = "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800",
                sellerId = "app_seller_6",
                isSold = false
            ),
            Property(
                id = "app_prop_7",
                title = "Urban Townhouse",
                description = "Modern 3-story townhouse in trendy neighborhood. Rooftop deck, garage parking, and walking distance to restaurants and shops.",
                price = 625000.0,
                address = "369 Urban Street",
                city = "Seattle",
                zipCode = "98101",
                bedrooms = 3,
                bathrooms = 3,
                area = 1900.0,
                imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                sellerId = "app_seller_7",
                isSold = false
            ),
            Property(
                id = "app_prop_8",
                title = "Lakefront Retreat",
                description = "Peaceful lakefront property with private dock. Vaulted ceilings, stone fireplace, and panoramic water views from every room.",
                price = 750000.0,
                address = "741 Lakeshore Drive",
                city = "Minneapolis",
                zipCode = "55401",
                bedrooms = 4,
                bathrooms = 3,
                area = 2800.0,
                imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                sellerId = "app_seller_8",
                isSold = false
            ),
            Property(
                id = "app_prop_9",
                title = "Contemporary Glass House",
                description = "Stunning modern glass house with floor-to-ceiling windows. Minimalist design with smart home technology, infinity pool, and panoramic city views.",
                price = 1100000.0,
                address = "555 Skyline Boulevard",
                city = "Los Angeles",
                zipCode = "90001",
                bedrooms = 3,
                bathrooms = 3,
                area = 2400.0,
                imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
                sellerId = "app_seller_9",
                isSold = false
            ),
            Property(
                id = "app_prop_10",
                title = "Texas Ranch Estate",
                description = "Sprawling ranch property with 50 acres of land. Beautiful barn, horse stables, and scenic views. Perfect for equestrian enthusiasts.",
                price = 850000.0,
                address = "1000 Ranch Road",
                city = "Dallas",
                zipCode = "75201",
                bedrooms = 4,
                bathrooms = 3,
                area = 3500.0,
                imageUrl = "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800",
                sellerId = "app_seller_10",
                isSold = false
            ),
            Property(
                id = "app_prop_11",
                title = "Florida Waterfront Villa",
                description = "Luxurious waterfront villa with private beach access. Tropical landscape, infinity pool, and boat dock. Stunning sunset views.",
                price = 1350000.0,
                address = "2000 Ocean Terrace",
                city = "Tampa",
                zipCode = "33602",
                bedrooms = 5,
                bathrooms = 4,
                area = 3200.0,
                imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                sellerId = "app_seller_11",
                isSold = false
            ),
            Property(
                id = "app_prop_12",
                title = "California Desert Retreat",
                description = "Modern desert home with solar panels and sustainable design. Spacious open floor plan with panoramic desert views.",
                price = 650000.0,
                address = "3000 Desert Drive",
                city = "Phoenix",
                zipCode = "85001",
                bedrooms = 3,
                bathrooms = 2,
                area = 2100.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "app_seller_12",
                isSold = false
            ),
            Property(
                id = "app_prop_13",
                title = "New York Loft",
                description = "Industrial-style loft in trendy Manhattan neighborhood. Exposed brick, high ceilings, and modern amenities. Walking distance to subway.",
                price = 950000.0,
                address = "4000 Broadway",
                city = "New York",
                zipCode = "10002",
                bedrooms = 2,
                bathrooms = 2,
                area = 1600.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "app_seller_13",
                isSold = false
            ),
            Property(
                id = "app_prop_14",
                title = "Washington State Cottage",
                description = "Charming cottage nestled in the Pacific Northwest forest. Stone fireplace, wraparound porch, and hiking trails nearby.",
                price = 425000.0,
                address = "5000 Forest Lane",
                city = "Seattle",
                zipCode = "98102",
                bedrooms = 2,
                bathrooms = 1,
                area = 1300.0,
                imageUrl = "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800",
                sellerId = "app_seller_14",
                isSold = false
            ),
            Property(
                id = "app_prop_15",
                title = "Colorado Mountain Mansion",
                description = "Luxury mountain mansion with ski-in access. Stone and timber construction, multiple fireplaces, and panoramic mountain views.",
                price = 1500000.0,
                address = "6000 Peak Road",
                city = "Denver",
                zipCode = "80202",
                bedrooms = 6,
                bathrooms = 5,
                area = 4200.0,
                imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                sellerId = "app_seller_15",
                isSold = false
            ),
            Property(
                id = "app_prop_16",
                title = "Illinois Urban Penthouse",
                description = "Stunning Chicago penthouse with skyline views. Modern finishes, private terrace, and luxury amenities. Heart of downtown.",
                price = 1200000.0,
                address = "7000 Michigan Avenue",
                city = "Chicago",
                zipCode = "60601",
                bedrooms = 3,
                bathrooms = 3,
                area = 2300.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "app_seller_16",
                isSold = false
            ),
            Property(
                id = "app_prop_17",
                title = "Georgia Historic Manor",
                description = "Beautifully restored historic mansion with Southern charm. Grand staircase, original hardwood floors, and manicured gardens.",
                price = 725000.0,
                address = "8000 Peach Street",
                city = "Atlanta",
                zipCode = "30303",
                bedrooms = 5,
                bathrooms = 4,
                area = 3800.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "app_seller_17",
                isSold = false
            ),
            Property(
                id = "app_prop_18",
                title = "Massachusetts Colonial Home",
                description = "Classic New England colonial with updated interior. Spacious rooms, fireplace, and charming backyard. Close to excellent schools.",
                price = 575000.0,
                address = "9000 Liberty Lane",
                city = "Boston",
                zipCode = "02102",
                bedrooms = 4,
                bathrooms = 2,
                area = 2600.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "app_seller_18",
                isSold = false
            ),
            Property(
                id = "app_prop_19",
                title = "Nevada Modern Condo",
                description = "Contemporary condo with smart home features. Floor-to-ceiling windows, modern kitchen, and resort-style amenities.",
                price = 425000.0,
                address = "10000 Vegas Boulevard",
                city = "Las Vegas",
                zipCode = "89101",
                bedrooms = 2,
                bathrooms = 2,
                area = 1400.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "app_seller_19",
                isSold = false
            ),
            Property(
                id = "app_prop_20",
                title = "Bangalore Tech Hub Apartment",
                description = "Modern 2-bedroom apartment in the heart of Bangalore's tech hub. Close to IT parks, shopping malls, and restaurants. Fully furnished with AC and parking.",
                price = 5500000.0,
                address = "123 MG Road",
                city = "Bangalore",
                zipCode = "560001",
                bedrooms = 2,
                bathrooms = 2,
                area = 1100.0,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                sellerId = "app_seller_20",
                isSold = false
            ),
            Property(
                id = "app_prop_21",
                title = "Mumbai Luxury Apartment",
                description = "Stunning luxury apartment in South Mumbai with sea-facing views. Premium amenities, concierge service, and 24/7 security. Perfect investment property.",
                price = 12000000.0,
                address = "456 Marine Drive",
                city = "Mumbai",
                zipCode = "400020",
                bedrooms = 3,
                bathrooms = 3,
                area = 1800.0,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                sellerId = "app_seller_21",
                isSold = false
            ),
            Property(
                id = "app_prop_22",
                title = "Hyderabad Premium Villa",
                description = "Spacious 3-bedroom villa in upscale Hyderabad neighborhood. Modern architecture, garden, swimming pool, and guest house. Gated community with security.",
                price = 7500000.0,
                address = "789 Jubilee Hills",
                city = "Hyderabad",
                zipCode = "500033",
                bedrooms = 3,
                bathrooms = 3,
                area = 2500.0,
                imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800",
                sellerId = "app_seller_22",
                isSold = false
            ),
            Property(
                id = "app_prop_23",
                title = "Pune Residential Complex",
                description = "Beautiful 2-bedroom apartment in Pune's premium residential complex. Modern amenities, gym, swimming pool, and landscaped gardens. Near IT companies.",
                price = 4200000.0,
                address = "321 Koregaon Park",
                city = "Pune",
                zipCode = "411001",
                bedrooms = 2,
                bathrooms = 2,
                area = 1200.0,
                imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800",
                sellerId = "app_seller_23",
                isSold = false
            ),
            Property(
                id = "app_prop_24",
                title = "New Delhi Luxury Penthouse",
                description = "Exclusive penthouse in New Delhi's prime location. Panoramic city views, private terrace, smart home automation, and premium finishes throughout.",
                price = 15000000.0,
                address = "654 Connaught Place",
                city = "New Delhi",
                zipCode = "110001",
                bedrooms = 4,
                bathrooms = 4,
                area = 2800.0,
                imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                sellerId = "app_seller_24",
                isSold = false
            ),
            Property(
                id = "app_prop_25",
                title = "Florida Beach House",
                description = "Beautiful beachfront property in Florida with direct beach access. Stunning ocean views, modern amenities, and perfect for vacation or investment.",
                price = 950000.0,
                address = "987 Beach Boulevard",
                city = "Miami Beach",
                zipCode = "33139",
                bedrooms = 3,
                bathrooms = 2,
                area = 1900.0,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                sellerId = "app_seller_25",
                isSold = false
            )
        )
        
        Log.d("RealEstateApp", "Starting to insert ${dummyProperties.size} properties...")
        dummyProperties.forEach { property ->
            try {
                val result = repository.insertProperty(property)
                Log.d("RealEstateApp", "✓ Inserted property: ${property.title} in ${property.city} (ID: $result)")
            } catch (e: Exception) {
                Log.e("RealEstateApp", "✗ Error inserting property: ${property.title}", e)
            }
        }
        
        // Verify insertion
        val finalCount = repository.getPropertyCount()
        Log.d("RealEstateApp", "✓ Finished seeding. Total properties in database: $finalCount")
    }
}
