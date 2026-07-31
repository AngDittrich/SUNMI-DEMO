package com.example.kiosco

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.DarkCardBg
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.LightBg
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.TextMuted
import kotlin.math.roundToInt

@Composable
fun OrderSummaryScreen(
    orderItems: List<CartItem>,
    onDone: () -> Unit
) {
    val totalPrice = orderItems.sumOf { it.subtotal }
    val totalItems = orderItems.sumOf { it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, LightBg)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        SuccessCheck()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Pago exitoso!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = DarkCharcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gracias por tu compra",
            fontSize = 18.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        OrderDetailsCard(
            orderItems = orderItems,
            totalPrice = totalPrice,
            totalItems = totalItems
        )

        Spacer(modifier = Modifier.height(20.dp))

        TicketCard()

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = DarkCharcoal
            ),
            shape = RoundedCornerShape(26.dp),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(
                text = "Listo",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SuccessCheck() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(
                scale = animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "checkScale"
                ).value
            )
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(NeonGreen, NeonGreen.copy(alpha = 0.8f))
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
}

@Composable
private fun OrderDetailsCard(
    orderItems: List<CartItem>,
    totalPrice: Double,
    totalItems: Int
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumen del pedido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
                Text(
                    text = "$totalItems ${if (totalItems == 1) "item" else "items"}",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            orderItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkCharcoal,
                            maxLines = 1
                        )
                        Text(
                            text = "x${item.quantity}  •  $${String.format("%.2f", item.product.price)}",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "$${String.format("%.2f", item.subtotal)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFECECEC))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal
                )
                Text(
                    text = "$${String.format("%.2f", totalPrice)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal
                )
            }
        }
    }
}

@Composable
private fun TicketCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Por favor recoja su ticket recién impreso",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val transition = rememberInfiniteTransition(label = "arrowBounce")
            val bounce by transition.animateFloat(
                initialValue = 0f,
                targetValue = 12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bounceOffset"
            )

            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Recoja su ticket",
                tint = NeonGreen,
                modifier = Modifier
                    .offset {
                        IntOffset(0, bounce.roundToInt())
                    }
                    .size(44.dp)
            )
        }
    }
}
