package com.example.kiosco

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onUpdateQuantity: (Int, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit
) {
    val totalPrice = cartItems.sumOf { it.subtotal }
    val totalItems = cartItems.sumOf { it.quantity }
    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Top bar
            CartTopBar(
                itemCount = cartItems.size,
                onBack = onBack,
                onClear = if (cartItems.isNotEmpty()) {
                    { showClearDialog = true }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                EmptyCartState(modifier = Modifier.weight(1f))
            } else {
                // Items list
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
            }

            // Bottom summary
            CartBottomBar(
                totalItems = totalItems,
                totalPrice = totalPrice,
                enabled = cartItems.isNotEmpty(),
                onCheckout = onCheckout
            )
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
}

@Composable
private fun CartTopBar(
    itemCount: Int,
    onBack: () -> Unit,
    onClear: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
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
                    tint = Color(0xFFE53935)
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
    val animatedElevation by animateFloatAsState(
        targetValue = if (item.quantity > 0) 4f else 1f,
        animationSpec = tween(300),
        label = "rowElevation"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 16.sp,
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
                Spacer(modifier = Modifier.height(6.dp))

                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallQuantityButton(
                        icon = Icons.Default.Remove,
                        onClick = onDecrease
                    )

                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    }

                    SmallQuantityButton(
                        icon = Icons.Default.Add,
                        onClick = onIncrease
                    )
                }
            }

            // Subtotal + remove
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.2f", item.subtotal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallQuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
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
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingBag,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
            .padding(vertical = 16.dp),
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
