package com.example.realestateapp.data.repository

import com.example.realestateapp.data.entity.Property
import com.example.realestateapp.data.entity.Transaction
import com.example.realestateapp.data.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import android.net.Uri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {
    private val database: DatabaseReference = Firebase.database.reference
    private val storage = Firebase.storage.reference
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // User operations
    suspend fun saveUser(user: User): Boolean {
        return try {
            database.child("users").child(user.id).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = database.child("users").child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return try {
            val snapshot = database.child("users")
                .orderByChild("username")
                .equalTo(username)
                .get()
                .await()
            
            snapshot.children.firstOrNull()?.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return try {
            val snapshot = database.child("users")
                .orderByChild("email")
                .equalTo(email)
                .get()
                .await()
            
            snapshot.children.firstOrNull()?.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun loginUser(username: String, password: String): User? {
        return try {
            val snapshot = database.child("users")
                .orderByChild("username")
                .equalTo(username)
                .get()
                .await()
            
            val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
            if (user?.password == password) user else null
        } catch (e: Exception) {
            null
        }
    }
    
    // Property operations
    suspend fun saveProperty(property: Property): String? {
        return try {
            val propertyRef = if (property.id.isEmpty()) {
                database.child("properties").push()
            } else {
                database.child("properties").child(property.id)
            }
            
            val propertyId = propertyRef.key ?: return null
            val propertyWithId = property.copy(id = propertyId)
            
            propertyRef.setValue(propertyWithId).await()
            propertyId
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getProperty(propertyId: String): Property? {
        return try {
            val snapshot = database.child("properties").child(propertyId).get().await()
            snapshot.getValue(Property::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getProperties(): List<Property> {
        return try {
            val snapshot = database.child("properties").get().await()
            snapshot.children.mapNotNull { it.getValue(Property::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getPropertiesFlow(): Flow<List<Property>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val properties = snapshot.children.mapNotNull { 
                    it.getValue(Property::class.java) 
                }
                trySend(properties)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        database.child("properties").addValueEventListener(listener)
        
        awaitClose {
            database.child("properties").removeEventListener(listener)
        }
    }
    
    suspend fun updateProperty(property: Property): Boolean {
        return try {
            database.child("properties").child(property.id).setValue(property).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteProperty(propertyId: String): Boolean {
        return try {
            database.child("properties").child(propertyId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getPropertiesByUser(userId: String): List<Property> {
        return try {
            val snapshot = database.child("properties")
                .orderByChild("sellerId")
                .equalTo(userId)
                .get()
                .await()
            
            snapshot.children.mapNotNull { it.getValue(Property::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun searchProperties(query: String): List<Property> {
        return try {
            val allProperties = getProperties()
            allProperties.filter { property ->
                property.title.contains(query, ignoreCase = true) ||
                property.description.contains(query, ignoreCase = true) ||
                property.city.contains(query, ignoreCase = true) ||
                property.address.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Transaction operations
    suspend fun saveTransaction(transaction: Transaction): String? {
        return try {
            val transactionRef = if (transaction.id.isEmpty()) {
                database.child("transactions").push()
            } else {
                database.child("transactions").child(transaction.id)
            }
            
            val transactionId = transactionRef.key ?: return null
            val transactionWithId = transaction.copy(id = transactionId)
            
            transactionRef.setValue(transactionWithId).await()
            transactionId
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getTransaction(transactionId: String): Transaction? {
        return try {
            val snapshot = database.child("transactions").child(transactionId).get().await()
            snapshot.getValue(Transaction::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getTransactionsByUser(userId: String): List<Transaction> {
        return try {
            val buyerSnapshot = database.child("transactions")
                .orderByChild("buyerId")
                .equalTo(userId)
                .get()
                .await()
            
            val sellerSnapshot = database.child("transactions")
                .orderByChild("sellerId")
                .equalTo(userId)
                .get()
                .await()
            
            val buyerTransactions = buyerSnapshot.children.mapNotNull { 
                it.getValue(Transaction::class.java) 
            }
            val sellerTransactions = sellerSnapshot.children.mapNotNull { 
                it.getValue(Transaction::class.java) 
            }
            
            (buyerTransactions + sellerTransactions).distinctBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getTransactionsByUserFlow(userId: String): Flow<List<Transaction>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Launch a coroutine to handle the suspend function
                scope.launch {
                    try {
                        val transactions = getTransactionsByUser(userId)
                        trySend(transactions)
                    } catch (e: Exception) {
                        // Handle error silently or log it
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        // Listen to both buyer and seller transactions
        database.child("transactions").orderByChild("buyerId").equalTo(userId)
            .addValueEventListener(listener)
        database.child("transactions").orderByChild("sellerId").equalTo(userId)
            .addValueEventListener(listener)
        
        awaitClose {
            database.child("transactions").removeEventListener(listener)
        }
    }
    
    // Image upload operations
    suspend fun uploadPropertyImage(imageUri: Uri, propertyId: String): String? {
        return try {
            val imageRef = storage.child("property_images/${propertyId}_${System.currentTimeMillis()}.jpg")
            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun deletePropertyImage(imageUrl: String): Boolean {
        return try {
            val imageRef = storage.storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Dummy data functions
    suspend fun seedDummyData(): Boolean {
        return try {
            val dummyProperties = createDummyProperties()
            dummyProperties.forEach { property ->
                saveProperty(property)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun createDummyProperties(): List<Property> {
        return listOf(
            Property(
                id = "prop_1",
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
                sellerId = "seller_1",
                isSold = false
            ),
            Property(
                id = "prop_2",
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
                sellerId = "seller_2",
                isSold = false
            ),
            Property(
                id = "prop_3",
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
                sellerId = "seller_3",
                isSold = false
            ),
            Property(
                id = "prop_4",
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
                sellerId = "seller_4",
                isSold = false
            ),
            Property(
                id = "prop_5",
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
                sellerId = "seller_5",
                isSold = false
            ),
            Property(
                id = "prop_6",
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
                sellerId = "seller_6",
                isSold = false
            ),
            Property(
                id = "prop_7",
                title = "Contemporary Loft",
                description = "Industrial-style loft in converted warehouse. Exposed beams, polished concrete floors, and artist studio space included.",
                price = 520000.0,
                address = "147 Industrial Way",
                city = "Portland",
                zipCode = "97201",
                bedrooms = 1,
                bathrooms = 2,
                area = 1600.0,
                imageUrl = "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800",
                sellerId = "seller_7",
                isSold = false
            ),
            Property(
                id = "prop_8",
                title = "Garden Estate",
                description = "Magnificent estate home on 2 acres of landscaped gardens. Grand foyer, gourmet kitchen, and separate guest house.",
                price = 1850000.0,
                address = "258 Estate Drive",
                city = "Nashville",
                zipCode = "37201",
                bedrooms = 5,
                bathrooms = 4,
                area = 4200.0,
                imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800",
                sellerId = "seller_8",
                isSold = false
            ),
            Property(
                id = "prop_9",
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
                sellerId = "seller_9",
                isSold = false
            ),
            Property(
                id = "prop_10",
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
                sellerId = "seller_10",
                isSold = false
            )
        )
    }
    
    suspend fun checkIfDataExists(): Boolean {
        return try {
            val snapshot = database.child("properties").get().await()
            snapshot.exists() && snapshot.childrenCount > 0
        } catch (e: Exception) {
            false
        }
    }
}
