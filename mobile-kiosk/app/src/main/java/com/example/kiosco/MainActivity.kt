package com.example.kiosco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kiosco.ui.theme.KioscoTheme
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.LightBg
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.launch

object NavRoutes {
    const val WELCOME = "welcome"
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val ORDER_SUMMARY = "order_summary"

    fun productDetail(productId: Int) = "product_detail/$productId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KioscoTheme {
                var products by remember { mutableStateOf<List<Product>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                val cartItems = remember { mutableStateOf<List<CartItem>>(emptyList()) }
                var cartSheetVisible by remember { mutableStateOf(false) }
                val lastOrder = remember { mutableStateOf<List<CartItem>>(emptyList()) }
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()

                // Cart helpers
                val addToCart: (Product) -> Unit = { product ->
                    val current = cartItems.value
                    val existing = current.find { it.product.id == product.id }
                    cartItems.value = if (existing != null) {
                        current.map {
                            if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                        }
                    } else {
                        current + CartItem(product, 1)
                    }
                }

                val updateQuantity: (Int, Int) -> Unit = { productId, newQty ->
                    if (newQty <= 0) {
                        cartItems.value = cartItems.value.filter { it.product.id != productId }
                    } else {
                        val existing = cartItems.value.find { it.product.id == productId }
                        if (existing != null) {
                            cartItems.value = cartItems.value.map {
                                if (it.product.id == productId) it.copy(quantity = newQty) else it
                            }
                        } else {
                            val product = products.find { it.id == productId }
                            if (product != null) {
                                cartItems.value = cartItems.value + CartItem(product, newQty)
                            }
                        }
                    }
                }

                val getQuantity: (Int) -> Int = { productId ->
                    cartItems.value.find { it.product.id == productId }?.quantity ?: 0
                }

                val totalItems = cartItems.value.sumOf { it.quantity }
                val totalPrice = cartItems.value.sumOf { it.subtotal }

                LaunchedEffect(Unit) {
                    scope.launch {
                        try {
                            val api = ApiService.create()
                            products = api.getProducts()
                            isLoading = false
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                            isLoading = false
                            e.printStackTrace()
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightBg
                ) {
                    when {
                        isLoading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(LightBg),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = NeonGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando productos...",
                                    color = TextMuted,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        errorMessage != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(LightBg)
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No se pudieron cargar los productos",
                                    color = DarkCharcoal,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = TextMuted,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {
                            val currentDestination = navController.currentBackStackEntryAsState()
                            val currentRoute = currentDestination.value?.destination?.route
                            val isWelcomeRoute = currentRoute == NavRoutes.WELCOME

                            Box(modifier = Modifier.fillMaxSize()) {
                                NavHost(
                                    navController = navController,
                                    startDestination = NavRoutes.WELCOME
                                ) {
                                    composable(NavRoutes.WELCOME) {
                                        WelcomeScreen(
                                            products = products,
                                            onGetStarted = {
                                                navController.navigate(NavRoutes.PRODUCT_LIST) {
                                                    popUpTo(NavRoutes.WELCOME) { inclusive = true }
                                                }
                                            }
                                        )
                                    }

                                    composable(NavRoutes.PRODUCT_LIST) {
                                        SnackKioskScreen(
                                            products = products,
                                            onProductClick = { product ->
                                                navController.navigate(NavRoutes.productDetail(product.id))
                                            },
                                            onAddToCart = { product, _, _ -> addToCart(product) },
                                            onCartClick = { cartSheetVisible = true }
                                        )
                                    }

                                    composable(
                                        route = NavRoutes.PRODUCT_DETAIL,
                                        arguments = listOf(
                                            navArgument("productId") { type = NavType.IntType }
                                        )
                                    ) { backStackEntry ->
                                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                                        ProductDetailScreen(
                                            products = products,
                                            initialProductId = productId,
                                            getQuantity = getQuantity,
                                            onQuantityChange = { id, qty -> updateQuantity(id, qty) },
                                            cartBarVisible = totalItems > 0,
                                            onBack = { navController.popBackStack() },
                                            onCartClick = { cartSheetVisible = true }
                                        )
                                    }

                                    composable(NavRoutes.ORDER_SUMMARY) {
                                        OrderSummaryScreen(
                                            orderItems = lastOrder.value,
                                            onDone = {
                                                navController.navigate(NavRoutes.WELCOME) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        )
                                    }
                                }

                                if (totalItems > 0 && !isWelcomeRoute && !cartSheetVisible) {
                                    CartSummaryBar(
                                        totalItems = totalItems,
                                        totalPrice = totalPrice,
                                        onCartClick = { cartSheetVisible = true },
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(horizontal = 24.dp)
                                            .padding(bottom = 16.dp)
                                            .navigationBarsPadding()
                                    )
                                }
                            }

                            CartScreen(
                                cartItems = cartItems.value,
                                onUpdateQuantity = { id, qty -> updateQuantity(id, qty) },
                                onClearCart = { cartItems.value = emptyList() },
                                onCheckout = {
                                    lastOrder.value = cartItems.value
                                    cartItems.value = emptyList()
                                    cartSheetVisible = false
                                    navController.navigate(NavRoutes.ORDER_SUMMARY)
                                    // TODO: Emit WebSocket event to terminal
                                },
                                cartSheetVisible = cartSheetVisible,
                                onDismiss = { cartSheetVisible = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
