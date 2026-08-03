package com.example.kiosco

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class ProductPayload(
    val name: String,
    val price: Double,
    val category: String,
    val barcode: String,
    val imageUrl: String,
    val description: String = ""
)

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: ProductPayload): Product

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductPayload
    ): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    companion object {
        private const val BASE_URL = "http://192.168.10.4:3000/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
