package com.example.kiosco.hotel

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private enum class HotelPayStep { Choose, Card, Cash }

@Composable
fun HotelPaymentScreen(
    reservation: HotelReservation,
    nfcDetected: Boolean,
    onCardListeningChanged: (Boolean) -> Unit,
    onCashPayment: () -> Unit,
    onPaid: () -> Unit,
    onBack: () -> Unit,
) {
    var step by remember { mutableStateOf(HotelPayStep.Choose) }
    var forwarded by remember { mutableStateOf(false) }

    LaunchedEffect(step) {
        onCardListeningChanged(step == HotelPayStep.Card)
    }
    DisposableEffect(Unit) {
        onDispose { onCardListeningChanged(false) }
    }

    LaunchedEffect(step, nfcDetected) {
        val ready = step == HotelPayStep.Cash || (step == HotelPayStep.Card && nfcDetected)
        if (!ready || forwarded) return@LaunchedEffect
        delay(900)
        forwarded = true
        onPaid()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HotelColors.Indigo)
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = {
                if (step == HotelPayStep.Card) step = HotelPayStep.Choose else onBack()
            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        if (step == HotelPayStep.Choose) {
            HotelMethodChoice(
                totalLabel = formatHotelMoney(reservation.total),
                onCash = {
                    onCashPayment()
                    step = HotelPayStep.Cash
                },
                onCard = { step = HotelPayStep.Card }
            )
        } else if (step == HotelPayStep.Cash) {
            HotelCashConfirm()
        }

        if (step == HotelPayStep.Card) BoxWithConstraints(
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
                (maxHeight * modalHeightFraction - reservedVerticalSpace).coerceAtLeast(120.dp)
            )
            val lowerCardMaximumHeight = if (compactHeight) 400.dp else 500.dp
            val stackDownshift = minOf(
                16.dp,
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
                    color = Color.White,
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
                                .background(HotelColors.Indigo.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Nfc,
                                contentDescription = null,
                                tint = HotelColors.Indigo,
                                modifier = Modifier.size(if (compactHeight) 32.dp else 38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 14.dp))
                        Text(
                            text = if (nfcDetected) "Pago aprobado" else "Pago sin contacto",
                            fontSize = if (compactHeight) 21.sp else 24.sp,
                            fontWeight = FontWeight.Black,
                            color = HotelColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = reservation.hotelName,
                            color = HotelColors.TextMuted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${reservation.roomName} · ${formatHotelMoney(reservation.total)}",
                            color = HotelColors.Indigo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = if (nfcDetected) {
                                "Listo. Preparando tu ticket…"
                            } else {
                                "Acerca tu celular al lector y mantenlo ahí."
                            },
                            fontSize = if (compactHeight) 14.sp else 16.sp,
                            color = HotelColors.TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(cardGap))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = lowerCardMaximumHeight),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = if (compactHeight) 16.dp else 24.dp,
                            vertical = if (compactHeight) 14.dp else 20.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            HotelNfcTapZone(
                                approved = nfcDetected,
                                modifier = Modifier
                                    .height(zoneHeight)
                                    .aspectRatio(240f / 320f)
                            )
                        }
                        Text(
                            text = if (nfcDetected) "Transacción completada" else "Esperando celular…",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (nfcDetected) HotelColors.PaidGreen else HotelColors.TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = if (compactHeight) 8.dp else 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelMethodChoice(
    totalLabel: String,
    onCash: () -> Unit,
    onCard: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Cómo vas a pagar?",
                    color = HotelColors.TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = totalLabel,
                    color = HotelColors.Indigo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 22.dp)
                )
                PayChoiceButton(
                    label = "Efectivo",
                    icon = Icons.Filled.Payments,
                    container = HotelColors.Yellow,
                    content = HotelColors.Indigo,
                    onClick = onCash
                )
                Spacer(modifier = Modifier.height(12.dp))
                PayChoiceButton(
                    label = "Tarjeta",
                    icon = Icons.Filled.CreditCard,
                    container = HotelColors.Indigo,
                    content = Color.White,
                    onClick = onCard
                )
            }
        }
    }
}

@Composable
private fun PayChoiceButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
        Text(
            text = label,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun HotelCashConfirm() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = HotelColors.PaidGreen,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Pago en efectivo",
                    color = HotelColors.TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Pago registrado. Preparando tu ticket…",
                    color = HotelColors.TextMuted,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun HotelNfcTapZone(approved: Boolean, modifier: Modifier = Modifier) {
    val dashColor = if (approved) HotelColors.PaidGreen else HotelColors.Indigo.copy(alpha = 0.6f)
    val bgColor = if (approved) {
        HotelColors.PaidGreen.copy(alpha = 0.12f)
    } else {
        HotelColors.Indigo.copy(alpha = 0.06f)
    }
    val transition = rememberInfiniteTransition(label = "hotelNfcPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .scale(if (approved) 1f else pulse)
            .drawBehind {
                val radius = CornerRadius(24.dp.toPx())
                drawRoundRect(color = bgColor, cornerRadius = radius)
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = radius,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (approved) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Pago aprobado",
                tint = HotelColors.PaidGreen,
                modifier = Modifier.fillMaxWidth(0.28f).aspectRatio(1f)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Nfc,
                contentDescription = null,
                tint = HotelColors.Indigo,
                modifier = Modifier.fillMaxWidth(0.33f).aspectRatio(1f)
            )
        }
    }
}
