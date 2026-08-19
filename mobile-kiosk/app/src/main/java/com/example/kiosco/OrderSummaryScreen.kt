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
import androidx.compose.material.icons.filled.Nfc
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
import com.example.kiosco.ui.theme.LocalBrandTheme
import com.example.kiosco.ui.theme.TextMuted
import kotlin.math.roundToInt

@Composable
fun OrderSummaryScreen(
    orderItems: List<CartItem>,
    paymentConfirmed: Boolean,
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onDone: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    val totalPrice = orderItems.sumOf { it.subtotal }
    val totalItems = orderItems.sumOf { it.quantity }

    LaunchedEffect(paymentConfirmed) {
        if (paymentConfirmed) onPrint()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(brandTheme.surface, brandTheme.background)
                )
            )
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            if (paymentConfirmed) {
                SuccessCheck()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "¡Pago exitoso!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = brandTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gracias por tu compra",
                    fontSize = 18.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            } else {
                WaitingForPaymentAnimation()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Acerca tu tarjeta para pagar",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = brandTheme.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Toca el lector NFC con tu tarjeta",
                    fontSize = 16.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            OrderDetailsCard(
                orderItems = orderItems,
                totalPrice = totalPrice,
                totalItems = totalItems
            )

            if (paymentConfirmed) {
                Spacer(modifier = Modifier.height(20.dp))

                TicketCard(printState = printState)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (paymentConfirmed) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                when (printState) {
                    TicketPrintState.Printed -> {
                        PrintActionButton(
                            text = "Listo",
                            onClick = onDone
                        )
                    }

                    TicketPrintState.Idle,
                    TicketPrintState.Printing -> {
                        ContinueWithoutPrintingButton(onClick = onDone)
                    }

                    is TicketPrintState.Failed -> {
                        if (printState.retryable) {
                            PrintActionButton(
                                text = "Imprimir de nuevo",
                                onClick = onPrint
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        ContinueWithoutPrintingButton(onClick = onDone)
                    }
                }
            }
        }
    }
}

@Composable
private fun WaitingForPaymentAnimation() {
    val brandTheme = LocalBrandTheme.current
    val transition = rememberInfiniteTransition(label = "nfcPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(pulse)
            .clip(CircleShape)
            .background(brandTheme.base),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Nfc,
            contentDescription = null,
            tint = brandTheme.onBase,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
private fun SuccessCheck() {
    val brandTheme = LocalBrandTheme.current
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
            .background(brandTheme.base),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = brandTheme.onBase,
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
    val brandTheme = LocalBrandTheme.current
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = brandTheme.surface),
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
                    color = brandTheme.textPrimary
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
                            color = brandTheme.textPrimary,
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
                        color = brandTheme.textPrimary
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
                    color = brandTheme.textPrimary
                )
                Text(
                    text = "$${String.format("%.2f", totalPrice)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = brandTheme.textPrimary
                )
            }
        }
    }
}

@Composable
private fun TicketCard(printState: TicketPrintState) {
    val brandTheme = LocalBrandTheme.current
    val message = when (printState) {
        TicketPrintState.Idle,
        TicketPrintState.Printing -> "Imprimiendo…"

        TicketPrintState.Printed ->
            "Tu ticket se imprimió correctamente. Por favor recógelo."

        is TicketPrintState.Failed -> if (printState.submittedUnconfirmed) {
            printState.message
        } else {
            "No se pudo imprimir el ticket: ${printState.message}"
        }
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = brandTheme.base),
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
                text = message,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandTheme.onBase,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            if (printState == TicketPrintState.Printed) {
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
                    tint = brandTheme.onBase,
                    modifier = Modifier
                        .offset {
                            IntOffset(0, bounce.roundToInt())
                        }
                        .size(44.dp)
                )
            }
        }
    }
}

@Composable
private fun PrintActionButton(
    text: String,
    onClick: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = brandTheme.accent,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(26.dp),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ContinueWithoutPrintingButton(onClick: () -> Unit) {
    val brandTheme = LocalBrandTheme.current
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = brandTheme.textPrimary),
        shape = RoundedCornerShape(26.dp),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Text(
            text = "Continuar sin imprimir",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
}
