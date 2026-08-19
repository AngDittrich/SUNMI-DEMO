package com.example.kiosco

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.LocalBrandTheme
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (Int, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    cartSheetVisible: Boolean,
    onDismiss: () -> Unit,
    showPaymentModal: Boolean,
    onShowPaymentModal: () -> Unit,
    onDismissPaymentModal: () -> Unit,
    onPaymentConfirmed: () -> Unit,
    nfcDetected: Boolean
) {
    val brandTheme = LocalBrandTheme.current
    val totalPrice = cartItems.sumOf { it.subtotal }
    val totalItems = cartItems.sumOf { it.quantity }
    var showClearDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetOffset = remember { Animatable(0f) }
    var sheetHeightPx by remember { mutableFloatStateOf(1f) }
    var lastDragDelta by remember { mutableFloatStateOf(0f) }
    val dragProgress = (sheetOffset.value / sheetHeightPx).coerceIn(0f, 1f)

    LaunchedEffect(cartSheetVisible) {
        if (cartSheetVisible) sheetOffset.snapTo(0f)
    }

    fun settleBack() {
        scope.launch {
            sheetOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = cartSheetVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 220)),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f * (1f - dragProgress)))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        AnimatedVisibility(
            visible = cartSheetVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.82f)
                    .onSizeChanged { sheetHeightPx = it.height.toFloat().coerceAtLeast(1f) }
                    .graphicsLayer { translationY = sheetOffset.value }
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = brandTheme.background,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .navigationBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        lastDragDelta = dragAmount
                                        scope.launch {
                                            sheetOffset.snapTo(
                                                (sheetOffset.value + dragAmount)
                                                    .coerceAtLeast(0f)
                                            )
                                        }
                                    },
                                    onDragEnd = {
                                        val flungDown = lastDragDelta > 22f
                                        lastDragDelta = 0f
                                        if (flungDown ||
                                            sheetOffset.value > sheetHeightPx * 0.28f
                                        ) {
                                            onDismiss()
                                        } else {
                                            settleBack()
                                        }
                                    },
                                    onDragCancel = {
                                        lastDragDelta = 0f
                                        settleBack()
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD7D7D7))
                        )
                    }

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
                        onCheckout = onShowPaymentModal
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
            nfcDetected = nfcDetected,
            onPaymentComplete = {
                onDismissPaymentModal()
                onPaymentConfirmed()
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
    val brandTheme = LocalBrandTheme.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(brandTheme.surface)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Cerrar",
                tint = brandTheme.textPrimary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tu Carrito",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = brandTheme.textPrimary
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
    val brandTheme = LocalBrandTheme.current
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
            colors = CardDefaults.cardColors(containerColor = brandTheme.surface),
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
                        color = brandTheme.textPrimary,
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
                            color = brandTheme.textPrimary
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
                    color = brandTheme.textPrimary
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
    val brandTheme = LocalBrandTheme.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(brandTheme.accent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    val brandTheme = LocalBrandTheme.current
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
                        colors = listOf(
                            brandTheme.accent.copy(alpha = 0.3f),
                            brandTheme.accent.copy(alpha = 0.1f)
                        )
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
            color = brandTheme.textPrimary
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
    val brandTheme = LocalBrandTheme.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = brandTheme.base,
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
                    color = brandTheme.onBase.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "$totalItems ${if (totalItems == 1) "item" else "items"}  •  $${String.format("%.2f", totalPrice)}",
                    color = brandTheme.onBase,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Button(
                onClick = onCheckout,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandTheme.accent,
                    disabledContainerColor = brandTheme.onBase.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = if (enabled) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        brandTheme.onBase.copy(alpha = 0.3f)
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pagar",
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        brandTheme.onBase.copy(alpha = 0.3f)
                    },
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
    val brandTheme = LocalBrandTheme.current
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
        containerColor = brandTheme.surface
    )
}

@Composable
private fun PaymentModal(
    nfcDetected: Boolean,
    onPaymentComplete: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    val successGreen = Color(0xFF1B8F3A)
    var phase by remember { mutableStateOf(PaymentPhase.WaitingForNfc) }

    LaunchedEffect(nfcDetected) {
        if (nfcDetected && phase == PaymentPhase.WaitingForNfc) {
            phase = PaymentPhase.Approved
        }
    }

    LaunchedEffect(phase) {
        if (phase == PaymentPhase.Approved) {
            delay(1400)
            onPaymentComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            val compactHeight = maxHeight < 700.dp
            val modalHeightFraction = if (compactHeight) 0.94f else 0.90f
            val cardGap = if (compactHeight) {
                (maxHeight * 0.045f).coerceIn(24.dp, 28.dp)
            } else {
                (maxHeight * 0.045f).coerceIn(28.dp, 36.dp)
            }
            val maximumZoneHeight = if (compactHeight) 330.dp else 420.dp
            val reservedVerticalSpace = if (compactHeight) 250.dp else 312.dp
            val zoneHeight = minOf(
                maximumZoneHeight,
                (maxHeight * modalHeightFraction - reservedVerticalSpace)
                    .coerceAtLeast(120.dp)
            )
            val lowerCardMaximumHeight = if (compactHeight) 400.dp else 500.dp
            val stackDownshift = minOf(
                8.dp,
                maxHeight * ((1f - modalHeightFraction) / 2f)
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(modalHeightFraction)
                    .offset(y = stackDownshift),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = brandTheme.surface,
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = if (compactHeight) 18.dp else 24.dp,
                            vertical = if (compactHeight) 16.dp else 22.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (compactHeight) 56.dp else 68.dp)
                                .clip(CircleShape)
                                .background(brandTheme.accent.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = null,
                                tint = brandTheme.accent,
                                modifier = Modifier.size(if (compactHeight) 32.dp else 38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 14.dp))

                        Text(
                            text = if (phase == PaymentPhase.Approved) {
                                "Pago aprobado"
                            } else {
                                "Pago sin contacto"
                            },
                            fontSize = if (compactHeight) 21.sp else 24.sp,
                            fontWeight = FontWeight.Black,
                            color = brandTheme.textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (phase == PaymentPhase.Approved) {
                                "Procesando tu pedido…"
                            } else {
                                "Acerca tu tarjeta o celular al lector y mantenlo ahí."
                            },
                            fontSize = if (compactHeight) 14.sp else 16.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(cardGap))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = lowerCardMaximumHeight),
                    shape = RoundedCornerShape(28.dp),
                    color = brandTheme.surface,
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                horizontal = if (compactHeight) 16.dp else 24.dp,
                                vertical = if (compactHeight) 14.dp else 20.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            NfcTapZone(
                                approved = phase == PaymentPhase.Approved,
                                modifier = Modifier
                                    .height(zoneHeight)
                                    .aspectRatio(240f / 320f)
                            )
                        }

                        Spacer(modifier = Modifier.height(if (compactHeight) 8.dp else 12.dp))

                        Text(
                            text = if (phase == PaymentPhase.Approved) {
                                "Transacción completada"
                            } else {
                                "Esperando tarjeta…"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (phase == PaymentPhase.Approved) {
                                successGreen
                            } else {
                                TextMuted
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private enum class PaymentPhase { WaitingForNfc, Approved }

@Composable
private fun NfcTapZone(
    approved: Boolean,
    modifier: Modifier = Modifier
) {
    val brandTheme = LocalBrandTheme.current
    val successGreen = Color(0xFF1B8F3A)
    val dashColor = if (approved) successGreen.copy(alpha = 0.9f) else {
        brandTheme.accent.copy(alpha = 0.6f)
    }
    val bgColor = if (approved) successGreen.copy(alpha = 0.12f) else {
        brandTheme.accent.copy(alpha = 0.06f)
    }
    val ringProgress = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }

    LaunchedEffect(approved) {
        if (approved) {
            ringProgress.snapTo(0f)
            checkProgress.snapTo(0f)
            launch {
                ringProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 560)
                )
            }
            delay(220)
            checkProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        } else {
            ringProgress.snapTo(0f)
            checkProgress.snapTo(0f)
        }
    }

    val transition = rememberInfiniteTransition(label = "nfcPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val borderStroke = Stroke(
        width = 3.dp.value,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(16f, 12f), 0f
        )
    )

    Box(
        modifier = modifier
            .scale(if (approved) 1f else pulse)
            .drawBehind {
                drawRoundRect(
                    color = bgColor,
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = borderStroke
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (approved) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.44f)
                    .aspectRatio(1f)
                    .drawBehind {
                        val ringWidth = 7.dp.toPx()
                        val inset = ringWidth / 2f
                        drawCircle(
                            color = successGreen.copy(alpha = 0.18f),
                            radius = size.minDimension / 2f - inset
                        )
                        drawArc(
                            color = successGreen,
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress.value,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                            size = androidx.compose.ui.geometry.Size(
                                width = size.width - ringWidth,
                                height = size.height - ringWidth
                            ),
                            style = Stroke(width = ringWidth, cap = StrokeCap.Round)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.78f)
                        .graphicsLayer {
                            alpha = ringProgress.value
                            scaleX = 0.9f + (0.1f * ringProgress.value)
                            scaleY = 0.9f + (0.1f * ringProgress.value)
                        }
                        .clip(CircleShape)
                        .background(successGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Pago aprobado",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize(0.58f)
                            .graphicsLayer {
                                alpha = checkProgress.value
                                scaleX = 0.72f + (0.28f * checkProgress.value)
                                scaleY = 0.72f + (0.28f * checkProgress.value)
                            }
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = null,
                tint = brandTheme.accent,
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .aspectRatio(1f)
            )
        }
    }
}
