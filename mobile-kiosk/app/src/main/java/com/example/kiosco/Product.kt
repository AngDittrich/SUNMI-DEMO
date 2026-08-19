package com.example.kiosco

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val barcode: String = "",
    val nfcId: String = "",
    val imageUrl: String,
    val description: String? = null
)
