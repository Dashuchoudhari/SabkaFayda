package com.sabkafayda.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Product(val title: String = "", val sellingPrice: Double = 0.0, val imageUrl: String = "")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var products by remember { mutableStateOf(listOf<Product>()) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        Firebase.firestore.collection("products").addSnapshotListener { v, error ->
                            if (error != null) {
                                Log.e("FirestoreError", "Database error", error)
                                errorMessage = error.message
                                return@addSnapshotListener
                            }
                            products = v?.toObjects(Product::class.java) ?: emptyList()
                        }
                    } catch (e: Exception) {
                        Log.e("FirebaseInitError", "Firebase failed to initialize", e)
                        errorMessage = "Firebase configuration issue: ${e.localizedMessage}"
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Sabka Fayda", color = Color.White) },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2874F0))
                        )
                    }
                ) { p ->
                    Column(modifier = Modifier.padding(p)) {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else if (products.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(products) { product ->
                                    Card(Modifier.padding(8.dp).fillMaxWidth()) {
                                        Column(Modifier.padding(8.dp)) {
                                            Text(product.title, maxLines = 1)
                                            Text(
                                                "₹${product.sellingPrice}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Button(
                                                onClick = { /* Add to Cart */ },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Buy Now")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
