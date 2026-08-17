package com.example.kiosco

import android.net.Uri
import android.os.Bundle
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kiosco.data.ProductRepository
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.KioscoTheme
import com.example.kiosco.ui.theme.LightBg
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.delay

object NavRoutes {
    const val WELCOME = "welcome"
    const val PRODUCT_LIST = "product_list"
    const val ORDER_SUMMARY = "order_summary"
    const val ADMIN_LIST = "admin_list"
    const val ADMIN_FORM = "admin_form/{productId}/{barcode}"

    fun adminForm(productId: Int? = null, barcode: String = ""): String {
        val id = productId ?: -1
        val encoded = Uri.encode(barcode.ifBlank { "_" })
        return "admin_form/$id/$encoded"
    }
}

private data class ScanSuccessFeedback(
    val productName: String,
    val imageUrl: String
)

class MainActivity : ComponentActivity() {
    /**
     * SUNMI scanners often inject keyboard-wedge events (digits + Enter) in
     * parallel with the broadcast. Enter would activate the focused button
     * (e.g. the lock icon → PIN dialog) or dismiss dialogs. Swallow those
     * keys; scans are handled only via [BarcodeScanManager].
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_TAB -> return true
        }
        if (event.keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 ||
            event.keyCode in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9
        ) {
            return true
        }

        val device = event.device
        val fromExternalScanner =
            event.deviceId != KeyCharacterMap.VIRTUAL_KEYBOARD &&
                event.deviceId != -1 &&
                (device == null || !device.isVirtual)
        if (fromExternalScanner) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

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
                // The detail lives above the list instead of replacing it, so the
                // real catalog stays composed behind it while it slides away.
                var detailProductId by remember { mutableStateOf<Int?>(null) }
                val lastOrder = remember { mutableStateOf<List<CartItem>>(emptyList()) }
                var flyEvent by remember { mutableStateOf<AddToCartFlyEvent?>(null) }
                var bagCenter by remember { mutableStateOf<Offset?>(null) }
                var bagBounceTrigger by remember { mutableStateOf(0) }
                var flyIdSeq by remember { mutableStateOf(0L) }
                var isEmployee by remember { mutableStateOf(false) }
                var pinDialogVisible by remember { mutableStateOf(false) }
                var scanSuccess by remember { mutableStateOf<ScanSuccessFeedback?>(null) }
                var productNotFoundVisible by remember { mutableStateOf(false) }
                var productNotFoundToken by remember { mutableStateOf(0) }
                var searchResetToken by remember { mutableStateOf(0) }
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                val focusManager = LocalFocusManager.current
                val context = LocalContext.current
                val navController = rememberNavController()
                val repository = remember(context) { ProductRepository(context) }

                fun fallbackBagCenter(): Offset {
                    val widthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    val heightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
                    val horizontalPad = with(density) { 24.dp.toPx() }
                    val bottomPad = with(density) { 16.dp.toPx() }
                    val barHeight = with(density) { 72.dp.toPx() }
                    val bagX = horizontalPad + with(density) { 38.dp.toPx() }
                    val bagY = heightPx - bottomPad - barHeight / 2f
                    return Offset(bagX.coerceAtMost(widthPx / 2f), bagY)
                }

                suspend fun refreshProducts() {
                    products = repository.getProducts()
                }

                fun enterEmployeeMode() {
                    isEmployee = true
                    pinDialogVisible = false
                    cartSheetVisible = false
                    detailProductId = null
                    navController.navigate(NavRoutes.ADMIN_LIST) {
                        popUpTo(NavRoutes.PRODUCT_LIST) { inclusive = false }
                        launchSingleTop = true
                    }
                }

                fun exitEmployeeMode() {
                    isEmployee = false
                    pinDialogVisible = false
                    detailProductId = null
                    navController.navigate(NavRoutes.PRODUCT_LIST) {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                }

                val onBarcodeScanned by rememberUpdatedState<(String) -> Unit> { code ->
                    focusManager.clearFocus(force = true)
                    searchResetToken += 1

                    if (isEmployee) {
                        val match = products.find { it.barcode == code }
                        if (match != null) {
                            navController.navigate(NavRoutes.adminForm(productId = match.id)) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(NavRoutes.adminForm(barcode = code)) {
                                launchSingleTop = true
                            }
                        }
                    } else {
                        val match = products.find { it.barcode == code }
                        if (match != null) {
                            val current = cartItems.value
                            val existing = current.find { it.product.id == match.id }
                            cartItems.value = if (existing != null) {
                                current.map {
                                    if (it.product.id == match.id) {
                                        it.copy(quantity = it.quantity + 1)
                                    } else {
                                        it
                                    }
                                }
                            } else {
                                current + CartItem(match, 1)
                            }

                            val widthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                            val heightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
                            flyIdSeq += 1
                            flyEvent = AddToCartFlyEvent(
                                id = flyIdSeq,
                                imageUrl = match.imageUrl,
                                startCenter = Offset(widthPx / 2f, heightPx * 0.42f),
                                startSize = with(density) { 140.dp.toPx() },
                                endCenter = bagCenter ?: fallbackBagCenter(),
                                endSize = with(density) { 28.dp.toPx() }
                            )
                            bagBounceTrigger += 1
                            productNotFoundVisible = false
                            scanSuccess = ScanSuccessFeedback(
                                productName = match.name,
                                imageUrl = match.imageUrl
                            )
                        } else {
                            scanSuccess = null
                            pinDialogVisible = false
                            productNotFoundToken += 1
                            productNotFoundVisible = true
                        }
                    }
                }

                // Cart helpers
                val addToCart = remember(cartItems.value) {
                    { product: Product ->
                        val current = cartItems.value
                        val existing = current.find { it.product.id == product.id }
                        cartItems.value = if (existing != null) {
                            current.map {
                                if (it.product.id == product.id) {
                                    it.copy(quantity = it.quantity + 1)
                                } else {
                                    it
                                }
                            }
                        } else {
                            current + CartItem(product, 1)
                        }
                    }
                }

                val updateQuantity = remember(cartItems.value, products) {
                    { productId: Int, newQty: Int ->
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
                }

                val getQuantity = remember(cartItems.value) {
                    { productId: Int ->
                        cartItems.value.find { it.product.id == productId }?.quantity ?: 0
                    }
                }

                val totalItems by remember { derivedStateOf { cartItems.value.sumOf { it.quantity } } }
                val totalPrice by remember { derivedStateOf { cartItems.value.sumOf { it.subtotal } } }

                LaunchedEffect(Unit) {
                    try {
                        refreshProducts()
                        isLoading = false
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                        isLoading = false
                        e.printStackTrace()
                    }
                }

                LaunchedEffect(scanSuccess) {
                    if (scanSuccess != null) {
                        delay(2800)
                        scanSuccess = null
                    }
                }

                LaunchedEffect(productNotFoundToken) {
                    if (productNotFoundToken == 0) return@LaunchedEffect
                    productNotFoundVisible = true
                    delay(5000)
                    // Only auto-dismiss if this token is still the latest show
                    productNotFoundVisible = false
                }

                DisposableEffect(Unit) {
                    val scanner = BarcodeScanManager(this@MainActivity) { code ->
                        onBarcodeScanned(code)
                    }
                    scanner.register()
                    onDispose { scanner.unregister() }
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
                            val isAdminRoute =
                                currentRoute == NavRoutes.ADMIN_LIST ||
                                    currentRoute?.startsWith("admin_form/") == true ||
                                    currentRoute == NavRoutes.ADMIN_FORM

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
                                            cartItemCount = totalItems,
                                            isEmployee = isEmployee,
                                            searchResetToken = searchResetToken,
                                            onProductClick = { product ->
                                                detailProductId = product.id
                                            },
                                            onAddToCart = { product, startCenter, startSize ->
                                                val existingQty = cartItems.value
                                                    .find { it.product.id == product.id }
                                                    ?.quantity ?: 0
                                                addToCart(product)
                                                if (existingQty == 0) {
                                                    flyIdSeq += 1
                                                    val end = bagCenter ?: fallbackBagCenter()
                                                    flyEvent = AddToCartFlyEvent(
                                                        id = flyIdSeq,
                                                        imageUrl = product.imageUrl,
                                                        startCenter = startCenter,
                                                        startSize = startSize,
                                                        endCenter = end,
                                                        endSize = with(density) { 28.dp.toPx() }
                                                    )
                                                }
                                            },
                                            onCartClick = { cartSheetVisible = true },
                                            onEmployeeLockClick = {
                                                if (isEmployee) {
                                                    exitEmployeeMode()
                                                } else {
                                                    pinDialogVisible = true
                                                }
                                            }
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

                                    composable(NavRoutes.ADMIN_LIST) {
                                        AdminProductListScreen(
                                            products = products,
                                            onProductClick = { product ->
                                                navController.navigate(
                                                    NavRoutes.adminForm(productId = product.id)
                                                )
                                            },
                                            onCreateClick = {
                                                navController.navigate(NavRoutes.adminForm())
                                            },
                                            onLogout = { exitEmployeeMode() },
                                            onDeleted = { refreshProducts() }
                                        )
                                    }

                                    composable(
                                        route = NavRoutes.ADMIN_FORM,
                                        arguments = listOf(
                                            navArgument("productId") { type = NavType.IntType },
                                            navArgument("barcode") { type = NavType.StringType }
                                        )
                                    ) { backStackEntry ->
                                        val productId =
                                            backStackEntry.arguments?.getInt("productId") ?: -1
                                        val rawBarcode =
                                            backStackEntry.arguments?.getString("barcode")
                                                .orEmpty()
                                        val barcode =
                                            Uri.decode(rawBarcode).let { decoded ->
                                                if (decoded == "_") "" else decoded
                                            }
                                        AdminProductFormScreen(
                                            products = products,
                                            editingProductId =
                                                productId.takeIf { it > 0 },
                                            initialBarcode = barcode,
                                            onBack = { navController.popBackStack() },
                                            onSaved = {
                                                refreshProducts()
                                                if (!navController.popBackStack(
                                                        NavRoutes.ADMIN_LIST,
                                                        inclusive = false
                                                    )
                                                ) {
                                                    navController.navigate(NavRoutes.ADMIN_LIST) {
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = detailProductId != null,
                                    enter = slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ),
                                    // The screen slides itself out before clearing the
                                    // id, so an exit transition here would double up.
                                    exit = ExitTransition.None,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val detailId = detailProductId
                                    if (detailId != null) {
                                        ProductDetailScreen(
                                            products = products,
                                            initialProductId = detailId,
                                            getQuantity = getQuantity,
                                            onQuantityChange = { id, qty ->
                                                updateQuantity(id, qty)
                                            },
                                            cartBarVisible = totalItems > 0,
                                            onBack = { detailProductId = null },
                                            onCartClick = { cartSheetVisible = true }
                                        )
                                    }
                                }

                                if (!isWelcomeRoute && !isAdminRoute && !cartSheetVisible) {
                                    CartSummaryBar(
                                        totalItems = totalItems,
                                        totalPrice = totalPrice,
                                        onCartClick = { cartSheetVisible = true },
                                        enabled = totalItems > 0,
                                        bagBounceTrigger = bagBounceTrigger,
                                        onBagPositioned = { bagCenter = it },
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(horizontal = 24.dp)
                                            .padding(bottom = 16.dp)
                                            .navigationBarsPadding()
                                            .alpha(if (totalItems > 0) 1f else 0f)
                                    )
                                }

                                AddToCartFlyOverlay(
                                    event = flyEvent,
                                    onFinished = { finishedId ->
                                        if (flyEvent?.id == finishedId) {
                                            flyEvent = null
                                            bagBounceTrigger += 1
                                        }
                                    }
                                )

                                scanSuccess?.let { success ->
                                    ScanSuccessOverlay(
                                        feedback = success,
                                        onDismiss = { scanSuccess = null }
                                    )
                                }

                                if (productNotFoundVisible) {
                                    ProductNotFoundOverlay(
                                        onDismiss = { productNotFoundVisible = false }
                                    )
                                }
                            }

                            if (!isAdminRoute) {
                                CartScreen(
                                    cartItems = cartItems.value,
                                    onUpdateQuantity = { id, qty -> updateQuantity(id, qty) },
                                    onClearCart = { cartItems.value = emptyList() },
                                    onCheckout = {
                                        lastOrder.value = cartItems.value
                                        cartItems.value = emptyList()
                                        cartSheetVisible = false
                                        detailProductId = null
                                        navController.navigate(NavRoutes.ORDER_SUMMARY)
                                    },
                                    cartSheetVisible = cartSheetVisible,
                                    onDismiss = { cartSheetVisible = false }
                                )
                            }

                            if (pinDialogVisible) {
                                EmployeePinDialog(
                                    onUnlocked = { enterEmployeeMode() },
                                    onDismiss = { pinDialogVisible = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductNotFoundOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "¡Lo sentimos!",
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = DarkCharcoal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "El producto que escaneaste no existe",
                    color = TextMuted,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Text(
                        text = "Entendido",
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanSuccessOverlay(
    feedback: ScanSuccessFeedback,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(feedback) {
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.82f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 420f),
        label = "scanSuccessScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .scale(scale)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                ),
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AsyncImage(
                        model = feedback.imageUrl,
                        contentDescription = feedback.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF2F2F2))
                            .padding(10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarkCharcoal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "¡Agregado!",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = feedback.productName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ya está en tu carrito",
                    fontSize = 14.sp,
                    color = TextMuted.copy(alpha = 0.85f)
                )
            }
        }
    }
}

