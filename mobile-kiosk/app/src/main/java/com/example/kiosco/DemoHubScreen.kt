package com.example.kiosco

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.LocalBrandTheme
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Data model for a single demo card
// ─────────────────────────────────────────────────────────────────────────────

private data class DemoCard(
    val demo: Demo,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
)

private val HotelBlue = Color(0xFF1565C0)
private val HotelGold = Color(0xFFFFD54F)
private val SurveyAmber = Color(0xFFFF8F00)
private val SurveyAmberLight = Color(0xFFFFCC80)
private val PosGreen = Color(0xFF00C853)
private val PosGreenDark = Color(0xFF1B5E20)

private val demoCards = listOf(
    DemoCard(
        demo = Demo.POS,
        title = "Punto de Venta",
        subtitle = "POS Demo",
        description = "Catálogo táctil, carrito de compra, escáner de código de barras y cobro con NFC.",
        icon = Icons.Filled.ShoppingCart,
        accentColor = PosGreen,
        gradientStart = Color(0xFF1B5E20),
        gradientEnd = Color(0xFF2E7D32),
    ),
    DemoCard(
        demo = Demo.SURVEY,
        title = "Encuesta de Satisfacción",
        subtitle = "Survey Demo",
        description = "Recopila opiniones con QR, genera cupones impresos con la impresora SUNMI.",
        icon = Icons.Filled.FactCheck,
        accentColor = SurveyAmber,
        gradientStart = Color(0xFF4E2A00),
        gradientEnd = Color(0xFF7B4800),
    ),
    DemoCard(
        demo = Demo.HOTEL,
        title = "Reserva de hotel",
        subtitle = "Hotel Demo",
        description = "Elige habitación, define las fechas de estancia e imprime el comprobante en SUNMI.",
        icon = Icons.Filled.Hotel,
        accentColor = HotelBlue,
        gradientStart = Color(0xFFE3F2FD),
        gradientEnd = Color(0xFFBBDEFB),
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// DemoHubScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DemoHubScreen(onDemoSelected: (Demo) -> Unit) {
    val brandTheme = LocalBrandTheme.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(brandTheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isLarge = maxWidth >= 700.dp
        val horizontalPad = if (isLarge) 48.dp else 20.dp
        val useRow = isLarge && maxWidth >= 900.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isLarge) 32.dp else 20.dp))

            // ── Header: logos ────────────────────────────────────────────────
            HubBrandHeader(isLarge)

            Spacer(modifier = Modifier.height(if (isLarge) 28.dp else 20.dp))

            // ── Tagline ──────────────────────────────────────────────────────
            Text(
                text = "Selecciona una demo para comenzar",
                color = brandTheme.textPrimary.copy(alpha = 0.6f),
                fontSize = if (isLarge) 22.sp else 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLarge) 36.dp else 28.dp))

            // ── Demo cards ───────────────────────────────────────────────────
            if (useRow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    demoCards.forEachIndexed { index, card ->
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedDemoCard(
                                card = card,
                                index = index,
                                isLarge = isLarge,
                                onSelected = { onDemoSelected(card.demo) }
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    demoCards.forEachIndexed { index, card ->
                        AnimatedDemoCard(
                            card = card,
                            index = index,
                            isLarge = isLarge,
                            onSelected = { onDemoSelected(card.demo) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isLarge) 36.dp else 24.dp))

            // ── Footer ───────────────────────────────────────────────────────
            Text(
                text = "SYSCOM + SUNMI · Demo Suite",
                color = brandTheme.textPrimary.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated card with staggered entrance
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnimatedDemoCard(
    card: DemoCard,
    index: Int,
    isLarge: Boolean,
    onSelected: () -> Unit,
) {
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(60f) }

    LaunchedEffect(Unit) {
        delay(120L * index)
        alpha.animateTo(1f, animationSpec = tween(380, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(120L * index)
        translateY.animateTo(
            0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .graphicsLayer { translationY = translateY.value }
    ) {
        DemoCardContent(card = card, isLarge = isLarge, onSelected = onSelected)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Single demo card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DemoCardContent(
    card: DemoCard,
    isLarge: Boolean,
    onSelected: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        scaleAnim.animateTo(
            if (isPressed) 0.965f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 600f)
        )
    }

    val brandTheme = LocalBrandTheme.current
    val cornerRadius: Dp = if (isLarge) 32.dp else 24.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim.value)
            .shadow(
                elevation = if (isPressed) 2.dp else 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = card.accentColor,
                spotColor = card.accentColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            )
    ) {
        // Subtle background gradient
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            card.gradientStart.copy(alpha = 0.3f),
                            Color.White
                        )
                    )
                )
        )

        // Glowing circle accent (decorative)
        Box(
            modifier = Modifier
                .size(if (isLarge) 240.dp else 180.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 80f; translationY = -60f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            card.accentColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLarge) 32.dp else 24.dp)
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(if (isLarge) 72.dp else 58.dp)
                    .clip(RoundedCornerShape(if (isLarge) 22.dp else 18.dp))
                    .background(card.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = card.accentColor,
                    modifier = Modifier.size(if (isLarge) 40.dp else 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isLarge) 24.dp else 20.dp))

            // Subtitle chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(card.accentColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = card.subtitle,
                    color = card.accentColor,
                    fontSize = if (isLarge) 13.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.title,
                color = brandTheme.textPrimary,
                fontSize = if (isLarge) 28.sp else 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = if (isLarge) 34.sp else 28.sp
            )

            Spacer(modifier = Modifier.height(if (isLarge) 12.dp else 10.dp))

            Text(
                text = card.description,
                color = brandTheme.textPrimary.copy(alpha = 0.6f),
                fontSize = if (isLarge) 16.sp else 14.sp,
                lineHeight = if (isLarge) 24.sp else 20.sp
            )

            Spacer(modifier = Modifier.height(if (isLarge) 32.dp else 24.dp))

            // CTA row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(card.accentColor)
                        .padding(horizontal = if (isLarge) 28.dp else 20.dp, vertical = if (isLarge) 14.dp else 10.dp)
                ) {
                    Text(
                        text = "Iniciar demo",
                        color = Color.White,
                        fontSize = if (isLarge) 16.sp else 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Brand header (logos)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HubBrandHeader(isLarge: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLarge) 80.dp else 54.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = if (isLarge) 32.dp else 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AsyncImage(
                model = "file:///android_asset/brand/syscom-large-logo.png",
                contentDescription = "Logotipo de SYSCOM",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLarge) 50.dp else 34.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart
            )
        }

        Box(
            modifier = Modifier.weight(0.72f),
            contentAlignment = Alignment.CenterEnd
        ) {
            AsyncImage(
                model = "file:///android_asset/brand/sunmi.webp",
                contentDescription = "Logotipo de SUNMI",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLarge) 54.dp else 36.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterEnd
            )
        }
    }
}
