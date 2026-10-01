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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hotel
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.LocalBrandTheme
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.delay

private data class DemoCard(
    val demo: Demo,
    val title: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
    val photoAsset: String? = null,
)

private val demoCards = listOf(
    DemoCard(
        demo = Demo.POS,
        title = "Punto de Venta",
        label = "POS",
        description = "Catálogo táctil, carrito de compra, escáner de código de barras y cobro con NFC.",
        icon = Icons.Filled.ShoppingCart,
        photoAsset = "file:///android_asset/demo/pos-demo-card.jpg",
    ),
    DemoCard(
        demo = Demo.SURVEY,
        title = "Encuesta de Satisfacción",
        label = "Encuesta",
        description = "Recopila opiniones con QR y genera cupones impresos con la impresora SUNMI.",
        icon = Icons.Filled.FactCheck,
        photoAsset = "file:///android_asset/demo/survey-demo-card.jpg",
    ),
    DemoCard(
        demo = Demo.HOTEL,
        title = "Reserva de hotel",
        label = "Hotel",
        description = "Elige habitación, define las fechas de estancia e imprime el comprobante en SUNMI.",
        icon = Icons.Filled.Hotel,
        photoAsset = "file:///android_asset/demo/hotel-demo-card.jpg",
    ),
)

@Composable
fun DemoHubScreen(onDemoSelected: (Demo) -> Unit) {
    val brandTheme = LocalBrandTheme.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(brandTheme.surface, brandTheme.background),
                    endY = 900f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isLarge = maxWidth >= 700.dp
        val horizontalPad = if (isLarge) 40.dp else 16.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 980.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isLarge) 28.dp else 16.dp))

            HubBrandHeader(isLarge)

            Spacer(modifier = Modifier.height(if (isLarge) 28.dp else 20.dp))

            Text(
                text = "Selecciona una demo para comenzar",
                color = TextMuted,
                fontSize = if (isLarge) 18.sp else 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLarge) 28.dp else 18.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(if (isLarge) 16.dp else 12.dp)
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

            Spacer(modifier = Modifier.height(if (isLarge) 32.dp else 24.dp))

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

@Composable
private fun AnimatedDemoCard(
    card: DemoCard,
    index: Int,
    isLarge: Boolean,
    onSelected: () -> Unit,
) {
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(28f) }

    LaunchedEffect(Unit) {
        delay(80L * index)
        alpha.animateTo(1f, animationSpec = tween(320, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(80L * index)
        translateY.animateTo(
            0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
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

@Composable
private fun DemoCardContent(
    card: DemoCard,
    isLarge: Boolean,
    onSelected: () -> Unit,
) {
    val brandTheme = LocalBrandTheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim = remember { Animatable(1f) }
    val cornerRadius = if (isLarge) 28.dp else 22.dp

    LaunchedEffect(isPressed) {
        scaleAnim.animateTo(
            if (isPressed) 0.985f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 700f)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim.value)
            .shadow(elevation = if (isPressed) 2.dp else 8.dp, shape = RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(brandTheme.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            )
    ) {
        val cardHeight = if (isLarge) 188.dp else 148.dp
        val actionWidth = (maxWidth * 0.18f).coerceIn(76.dp, 116.dp)
        val mediaWidth = (maxWidth * 0.34f).coerceIn(116.dp, 240.dp)
        val textWidth = (maxWidth - mediaWidth - actionWidth).coerceAtLeast(0.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
        ) {
            CardMedia(
                card = card,
                isLarge = isLarge,
                modifier = Modifier
                    .width(mediaWidth)
                    .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                    .width(textWidth)
                    .fillMaxHeight()
                    .padding(
                        start = if (isLarge) 22.dp else 14.dp,
                        end = if (isLarge) 18.dp else 12.dp
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.title,
                    color = brandTheme.textPrimary,
                    fontSize = if (isLarge) 26.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = if (isLarge) 30.sp else 22.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(if (isLarge) 8.dp else 6.dp))

                Text(
                    text = card.description,
                    color = TextMuted,
                    fontSize = if (isLarge) 15.sp else 13.sp,
                    lineHeight = if (isLarge) 21.sp else 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxHeight()
                    .background(brandTheme.base),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(brandTheme.highlight)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = brandTheme.onBase,
                        modifier = Modifier.size(if (isLarge) 28.dp else 22.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Entrar",
                        color = brandTheme.onBase,
                        fontSize = if (isLarge) 16.sp else 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CardMedia(
    card: DemoCard,
    isLarge: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            card.photoAsset != null -> {
                AsyncImage(
                    model = card.photoAsset,
                    contentDescription = card.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isLarge) 12.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = card.icon,
                contentDescription = card.label,
                tint = Color.White,
                modifier = Modifier.size(if (isLarge) 22.dp else 18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = card.label,
                color = Color.White,
                fontSize = if (isLarge) 13.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

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
