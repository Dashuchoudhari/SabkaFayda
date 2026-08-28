package com.sabkafayda.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Product(val title: String = "", val sellingPrice: Double = 0.0, val imageUrl: String = "")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var products by remember { mutableStateOf(listOf<Product>()) }
            LaunchedEffect(Unit) {
                Firebase.firestore.collection("products").addSnapshotListener { v, _ ->
                    products = v?.toObjects(Product::class.java) ?: emptyList()
                }
            }
            Scaffold(
                topBar = { TopAppBar(title = { Text("Sabka Fayda", color = Color.White) }, 
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2874F0))) }
            ) { p ->
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(p)) {
                    items(products) { product ->
                        Card(Modifier.padding(8.dp).fillMaxWidth()) {
                            Column(Modifier.padding(8.dp)) {
                                Text(product.title, maxLines = 1)
                                Text("₹${product.sellingPrice}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Button(onClick = { /* Add to Cart */ }, Modifier.fillMaxWidth()) { Text("Buy Now") }
                            }
                        }
                    }
                }
            }
        }
    }
}
