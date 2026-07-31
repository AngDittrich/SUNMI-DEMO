package com.example.kiosco

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.LightBg
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val HeroSecondary = Color(0xFF242424)
private val SoftGreen = Color(0xFFEFFFC1)

@Composable
fun WelcomeScreen(
    products: List<Product>,
    onGetStarted: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, LightBg)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val largeDisplay = maxWidth >= 700.dp || maxHeight >= 1000.dp
        val contentMaxWidth = if (largeDisplay) 980.dp else 560.dp
        val horizontalPadding = if (largeDisplay) 44.dp else 20.dp
        val heroHeight = if (largeDisplay) {
            (maxHeight * 0.43f).coerceIn(460.dp, 680.dp)
        } else {
            (maxHeight * 0.45f).coerceIn(320.dp, 390.dp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = contentMaxWidth)
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandHeader(largeDisplay = largeDisplay)

            Spacer(modifier = Modifier.height(if (largeDisplay) 24.dp else 14.dp))

            WelcomeHero(
                productCount = products.size,
                height = heroHeight,
                largeDisplay = largeDisplay
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 26.dp else 18.dp))

            BenefitRow(largeDisplay = largeDisplay)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Desliza y descubre tu próximo favorito",
                color = TextMuted,
                fontSize = if (largeDisplay) 18.sp else 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 14.dp else 10.dp))

            SlideToStartButton(
                largeDisplay = largeDisplay,
                onSlideComplete = onGetStarted
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 24.dp else 14.dp))
        }
    }
}

@Composable
private fun BrandHeader(largeDisplay: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (largeDisplay) 74.dp else 54.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SNACK",
                color = DarkCharcoal,
                fontSize = if (largeDisplay) 30.sp else 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(if (largeDisplay) 14.dp else 10.dp)
                    .clip(CircleShape)
                    .background(NeonGreen)
            )
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = SoftGreen
        ) {
            Text(
                text = "SELF-SERVICE KIOSK",
                color = DarkCharcoal,
                fontSize = if (largeDisplay) 13.sp else 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(
                    horizontal = if (largeDisplay) 18.dp else 12.dp,
                    vertical = if (largeDisplay) 10.dp else 7.dp
                )
            )
        }
    }
}

@Composable
private fun WelcomeHero(
    productCount: Int,
    height: Dp,
    largeDisplay: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (largeDisplay) 18.dp else 10.dp,
                shape = RoundedCornerShape(if (largeDisplay) 48.dp else 34.dp)
            )
            .clip(RoundedCornerShape(if (largeDisplay) 48.dp else 34.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkCharcoal, HeroSecondary)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = NeonGreen.copy(alpha = 0.13f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.92f, size.height * 0.08f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.04f, size.height * 0.95f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (largeDisplay) 48.dp else 26.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.09f)
            ) {
                Text(
                    text = "$productCount OPCIONES LISTAS",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = if (largeDisplay) 13.sp else 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(
                        horizontal = if (largeDisplay) 17.dp else 12.dp,
                        vertical = if (largeDisplay) 9.dp else 7.dp
                    )
                )
            }

            Spacer(modifier = Modifier.height(if (largeDisplay) 24.dp else 16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tu antojo\n")
                    withStyle(SpanStyle(color = NeonGreen)) {
                        append("empieza aquí.")
                    }
                },
                color = Color.White,
                fontSize = if (largeDisplay) 68.sp else 43.sp,
                lineHeight = if (largeDisplay) 70.sp else 45.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.2).sp
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 16.dp else 10.dp))

            Text(
                text = "Elige, agrega y disfruta. Sin filas y sin complicaciones.",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = if (largeDisplay) 20.sp else 14.sp,
                lineHeight = if (largeDisplay) 28.sp else 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            SnackShowcase(largeDisplay = largeDisplay)
        }
    }
}

@Composable
private fun SnackShowcase(largeDisplay: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (largeDisplay) 18.dp else 10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        SnackTile(
            drawableRes = R.drawable.ic_cookie,
            label = "Cookies",
            color = Color(0xFFFFE4B8),
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
        SnackTile(
            drawableRes = R.drawable.ic_soda,
            label = "Drinks",
            color = Color(0xFFFFCFCF),
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
        SnackTile(
            drawableRes = R.drawable.ic_candy,
            label = "Candy",
            color = Color(0xFFFFD9EA),
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
        SnackTile(
            drawableRes = R.drawable.ic_chips,
            label = "Chips",
            color = Color(0xFFFFF0A8),
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SnackTile(
    drawableRes: Int,
    label: String,
    color: Color,
    largeDisplay: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(if (largeDisplay) 26.dp else 18.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(vertical = if (largeDisplay) 18.dp else 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (largeDisplay) 84.dp else 52.dp)
                .clip(RoundedCornerShape(if (largeDisplay) 23.dp else 15.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = label,
                modifier = Modifier.size(if (largeDisplay) 56.dp else 36.dp)
            )
        }

        Spacer(modifier = Modifier.height(if (largeDisplay) 10.dp else 6.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = if (largeDisplay) 15.sp else 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BenefitRow(largeDisplay: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (largeDisplay) 16.dp else 8.dp)
    ) {
        BenefitPill(
            title = "Rápido",
            subtitle = "Ordena en segundos",
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
        BenefitPill(
            title = "Fácil",
            subtitle = "Todo a tu alcance",
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
        BenefitPill(
            title = "Delicioso",
            subtitle = "Para cada antojo",
            largeDisplay = largeDisplay,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BenefitPill(
    title: String,
    subtitle: String,
    largeDisplay: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(if (largeDisplay) 24.dp else 18.dp))
            .background(Color.White)
            .padding(
                horizontal = if (largeDisplay) 20.dp else 10.dp,
                vertical = if (largeDisplay) 16.dp else 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (largeDisplay) 12.dp else 8.dp)
                .clip(CircleShape)
                .background(NeonGreen)
        )
        Spacer(modifier = Modifier.width(if (largeDisplay) 12.dp else 7.dp))
        Column {
            Text(
                text = title,
                color = DarkCharcoal,
                fontSize = if (largeDisplay) 16.sp else 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = if (largeDisplay) 12.sp else 8.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SlideToStartButton(
    largeDisplay: Boolean,
    onSlideComplete: () -> Unit
) {
    val trackHeight = if (largeDisplay) 100.dp else 74.dp
    val thumbSize = if (largeDisplay) 86.dp else 62.dp
    val trackInset = (trackHeight - thumbSize) / 2
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (largeDisplay) 760.dp else 560.dp)
    ) {
        val maxDrag = with(density) {
            (maxWidth - thumbSize - (trackInset * 2)).toPx()
        }.coerceAtLeast(0f)

        var dragOffset by remember { mutableFloatStateOf(0f) }
        var isDragging by remember { mutableStateOf(false) }
        var thresholdHapticPlayed by remember { mutableStateOf(false) }
        var completed by remember { mutableStateOf(false) }

        LaunchedEffect(maxDrag) {
            dragOffset = dragOffset.coerceIn(0f, maxDrag)
        }

        val animatedOffset by animateFloatAsState(
            targetValue = dragOffset,
            animationSpec = if (isDragging) {
                snap()
            } else {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            },
            label = "slideOffset"
        )

        val progress = if (maxDrag == 0f) 0f else (animatedOffset / maxDrag).coerceIn(0f, 1f)
        val progressWidth = with(density) {
            (animatedOffset + thumbSize.toPx() + (trackInset.toPx() * 2)).toDp()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(50))
                .background(DarkCharcoal)
        ) {
            Box(
                modifier = Modifier
                    .width(progressWidth)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                NeonGreen.copy(alpha = 0.20f),
                                NeonGreen.copy(alpha = 0.08f)
                            )
                        )
                    )
            )

            Text(
                text = "DESLIZA PARA COMENZAR   ›››",
                color = Color.White.copy(alpha = (0.68f - progress * 0.5f).coerceAtLeast(0.15f)),
                fontSize = if (largeDisplay) 18.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = if (largeDisplay) 1.4.sp else 0.9.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (trackInset.toPx() + animatedOffset).roundToInt(),
                            y = 0
                        )
                    }
                    .align(Alignment.CenterStart)
                    .size(thumbSize)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(NeonGreen)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            if (!completed) {
                                dragOffset = (dragOffset + delta).coerceIn(0f, maxDrag)
                                val thresholdReached = dragOffset >= maxDrag * 0.82f
                                if (thresholdReached && !thresholdHapticPlayed) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    thresholdHapticPlayed = true
                                } else if (!thresholdReached) {
                                    thresholdHapticPlayed = false
                                }
                            }
                        },
                        onDragStarted = {
                            isDragging = true
                        },
                        onDragStopped = {
                            isDragging = false
                            if (dragOffset >= maxDrag * 0.82f && !completed) {
                                completed = true
                                dragOffset = maxDrag
                                scope.launch {
                                    delay(180)
                                    onSlideComplete()
                                }
                            } else {
                                dragOffset = 0f
                                thresholdHapticPlayed = false
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Deslizar para comenzar",
                    tint = DarkCharcoal,
                    modifier = Modifier.size(if (largeDisplay) 38.dp else 28.dp)
                )
            }
        }
    }
}
