package com.example.kiosco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (Int, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    cartSheetVisible: Boolean,
    onDismiss: () -> Unit
) {
    val totalPrice = cartItems.sumOf { it.subtotal }
    val totalItems = cartItems.sumOf { it.quantity }
    var showClearDialog by remember { mutableStateOf(false) }
    var showPaymentModal by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = cartSheetVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.82f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = LightBg,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .navigationBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(48.dp)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD7D7D7))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CartTopBar(
                        itemCount = cartItems.size,
                        onDismiss = onDismiss,
                        onClear = if (cartItems.isNotEmpty()) {
                            { showClearDialog = true }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (cartItems.isEmpty()) {
                        EmptyCartState(modifier = Modifier.weight(1f))
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(cartItems, key = { it.product.id }) { item ->
                                CartItemRow(
                                    item = item,
                                    onIncrease = { onUpdateQuantity(item.product.id, item.quantity + 1) },
                                    onDecrease = { onUpdateQuantity(item.product.id, item.quantity - 1) },
                                    onRemove = { onUpdateQuantity(item.product.id, 0) }
                                )
                            }
                        }

                        Text(
                            text = "Desliza a la izquierda para eliminar",
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    CartBottomBar(
                        totalItems = totalItems,
                        totalPrice = totalPrice,
                        enabled = cartItems.isNotEmpty(),
                        onCheckout = { showPaymentModal = true }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        ClearCartDialog(
            onConfirm = {
                showClearDialog = false
                onClearCart()
            },
            onDismiss = { showClearDialog = false }
        )
    }

    if (showPaymentModal) {
        PaymentModal(
            onPaymentComplete = {
                showPaymentModal = false
                onCheckout()
            }
        )
    }
}

@Composable
private fun CartTopBar(
    itemCount: Int,
    onDismiss: () -> Unit,
    onClear: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Cerrar",
                tint = DarkCharcoal
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tu Carrito",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = DarkCharcoal
            )
            Text(
                text = if (itemCount == 0) "Sin productos" else "$itemCount ${if (itemCount == 1) "producto" else "productos"}",
                fontSize = 14.sp,
                color = TextMuted
            )
        }

        if (onClear != null) {
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE5E5))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Vaciar carrito",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onRemove()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFFCDD2))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.product.imageUrl,
                    contentDescription = item.product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${String.format("%.2f", item.product.price)} c/u",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SmallQuantityButton(
                            icon = Icons.Default.Remove,
                            onClick = onDecrease
                        )
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkCharcoal
                        )
                        SmallQuantityButton(
                            icon = Icons.Default.Add,
                            onClick = onIncrease
                        )
                    }
                }

                Text(
                    text = "$${String.format("%.2f", item.subtotal)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal
                )
            }
        }
    }
}

@Composable
private fun SmallQuantityButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(NeonGreen)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkCharcoal,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonGreen.copy(alpha = 0.3f), NeonGreen.copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingBag,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Tu carrito está vacío",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkCharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Agrega productos para comenzar tu pedido",
            fontSize = 16.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CartBottomBar(
    totalItems: Int,
    totalPrice: Double,
    enabled: Boolean,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = DarkCharcoal,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "$totalItems ${if (totalItems == 1) "item" else "items"}  •  $${String.format("%.2f", totalPrice)}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Button(
                onClick = onCheckout,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    disabledContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = if (enabled) DarkCharcoal else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pagar",
                    color = if (enabled) DarkCharcoal else Color.White.copy(alpha = 0.3f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ClearCartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Vaciar carrito?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Se eliminarán todos los productos de tu carrito. Esta acción no se puede deshacer.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Sí, vaciar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cancelar", color = TextMuted, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
private fun PaymentModal(
    onPaymentComplete: () -> Unit
) {
    var phase by remember { mutableStateOf(PaymentPhase.Paying) }
    var approved by remember { mutableStateOf(false) }

    LaunchedEffect(phase) {
        when (phase) {
            PaymentPhase.Paying -> {
                delay(2400)
                phase = PaymentPhase.Paid
            }
            PaymentPhase.Paid -> {
                delay(1400)
                onPaymentComplete()
            }
        }
    }

    LaunchedEffect(phase) {
        if (phase == PaymentPhase.Paid) {
            delay(120)
            approved = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "paymentPhase"
            ) { currentPhase ->
                when (currentPhase) {
                    PaymentPhase.Paying -> PayingContent()
                    PaymentPhase.Paid -> PaidContent(approved = approved)
                }
            }
        }
    }
}

private enum class PaymentPhase { Paying, Paid }

@Composable
private fun PayingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val transition = rememberInfiniteTransition(label = "payingPulse")
        val pulse by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 700),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        Box(
            modifier = Modifier
                .size(128.dp)
                .scale(pulse)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonGreen, NeonGreen.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CreditCard,
                contentDescription = null,
                tint = DarkCharcoal,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Acerque su tarjeta al terminal",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkCharcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Procesando pago...",
            fontSize = 15.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaidContent(approved: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val scale by animateFloatAsState(
            targetValue = if (approved) 1f else 0.4f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "checkScale"
        )

        Box(
            modifier = Modifier
                .size(128.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonGreen, NeonGreen.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = DarkCharcoal,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Pago aprobado",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = DarkCharcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Preparando tu pedido...",
            fontSize = 15.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}
