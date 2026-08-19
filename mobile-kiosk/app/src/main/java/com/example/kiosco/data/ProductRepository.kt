package com.example.kiosco.data

import android.content.Context
import com.example.kiosco.Product

class ProductRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).productDao()

    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(ProductSeeder.seedProducts())
        }
    }

    suspend fun getProducts(): List<Product> {
        ensureSeeded()
        return dao.getAll().map { it.toProduct() }
    }

    suspend fun createProduct(
        name: String,
        price: Double,
        category: String,
        barcode: String,
        imageUrl: String,
        description: String,
        nfcId: String = ""
    ): Product {
        val id = dao.insert(
            ProductEntity(
                name = name,
                price = price,
                category = category,
                barcode = barcode,
                nfcId = nfcId,
                imageUrl = imageUrl.ifBlank { ProductImages.PLACEHOLDER },
                description = description
            )
        )
        return Product(
            id = id.toInt(),
            name = name,
            price = price,
            category = category,
            barcode = barcode,
            nfcId = nfcId,
            imageUrl = imageUrl.ifBlank { ProductImages.PLACEHOLDER },
            description = description
        )
    }

    suspend fun updateProduct(product: Product) {
        dao.update(product.toEntity())
    }

    suspend fun deleteProduct(id: Int) {
        dao.deleteById(id)
    }

    private fun ProductEntity.toProduct() = Product(
        id = id,
        name = name,
        price = price,
        category = category,
        barcode = barcode,
        nfcId = nfcId,
        imageUrl = imageUrl,
        description = description
    )

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        price = price,
        category = category,
        barcode = barcode,
        nfcId = nfcId,
        imageUrl = imageUrl.ifBlank { ProductImages.PLACEHOLDER },
        description = description.orEmpty()
    )
}
