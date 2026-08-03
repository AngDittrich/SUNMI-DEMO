package com.example.kiosco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.NeonGreenV2
import java.util.Locale
import kotlin.math.abs

private val DetailBackground = Color(0xFFF8F8F8)
private val DetailSheet = Color(0xFFFFFFFF)
private val ControlBackground = Color(0xFFF2F2F2)

@Composable
fun ProductDetailScreen(
    products: List<Product>,
    initialProductId: Int,
    getQuantity: (Int) -> Int,
    onQuantityChange: (Int, Int) -> Unit,
    cartBarVisible: Boolean,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    val initialIndex = products.indexOfFirst { it.id == initialProductId }
    if (initialIndex < 0) return

    val pagerState = rememberPagerState(initialPage = initialIndex) { products.size }
    val hapticFeedback = LocalHapticFeedback.current
    var lastSettledPage by remember { mutableIntStateOf(initialIndex) }

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != lastSettledPage) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            lastSettledPage = pagerState.settledPage
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeightPx = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }
    val dismissThreshold = 0.15f
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDismissing by remember { mutableStateOf(false) }
    val animatedOffset = remember { Animatable(0f) }

    LaunchedEffect(isDismissing) {
        if (isDismissing) {
            animatedOffset.snapTo(dragOffset)
            animatedOffset.animateTo(
                targetValue = screenHeightPx,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            onBack()
        }
    }

    LaunchedEffect(dragOffset) {
        if (!isDismissing && dragOffset == 0f && animatedOffset.value != 0f) {
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
            .pointerInput(onBack) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (!isDismissing) {
                            if (dragOffset > screenHeightPx * dismissThreshold) {
                                isDismissing = true
                            } else {
                                dragOffset = 0f
                            }
                        }
                    },
                    onDragCancel = {
                        if (!isDismissing) {
                            dragOffset = 0f
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (!isDismissing) {
                            change.consume()
                            dragOffset = (dragOffset + dragAmount).coerceAtLeast(0f)
                        }
                    }
                )
            },
        contentAlignment = Alignment.TopCenter
    ) {
        val currentOffset = if (isDismissing) animatedOffset.value else dragOffset
        val dragProgress = (currentOffset / screenHeightPx).coerceIn(0f, 1f)
        val contentScale = 1f - 0.05f * dragProgress
        val contentAlpha = 1f - 0.4f * dragProgress

        val largeDisplay = maxWidth >= 700.dp || maxHeight >= 1000.dp
        val contentMaxWidth = if (largeDisplay) 900.dp else 520.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = currentOffset
                    scaleX = contentScale
                    scaleY = contentScale
                    alpha = contentAlpha
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = contentMaxWidth)
                    .background(DetailBackground)
                    .statusBarsPadding()
            ) {
                DetailTopBar(
                    largeDisplay = largeDisplay,
                    onBack = onBack,
                    onCartClick = onCartClick
                )

                ProductPagerIndicator(
                    pageCount = products.size,
                    currentPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                    largeDisplay = largeDisplay
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    pageSpacing = 8.dp,
                    beyondViewportPageCount = 0,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val product = products[page]
                    val quantity = getQuantity(product.id)

                    val pageOffset =
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                            .coerceIn(-1.25f, 1.25f)
                    val distance = abs(pageOffset).coerceIn(0f, 1f)
                    val focusProgress = FastOutSlowInEasing.transform(1f - distance)
                    val scale = 0.88f + 0.12f * focusProgress
                    val alpha = 0.45f + 0.55f * focusProgress

                    var pageHeightPx by remember { mutableIntStateOf(0) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { pageHeightPx = it.height }
                            .zIndex(focusProgress)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                                translationX = pageOffset * -20.dp.toPx()
                                translationY = distance * 18.dp.toPx()
                                rotationZ = pageOffset * -2.5f
                                rotationY = pageOffset * 7f
                                cameraDistance = 16f * density
                            }
                    ) {
                        val imageHeight = with(LocalDensity.current) {
                            (pageHeightPx * if (largeDisplay) 0.43f else 0.35f).toDp()
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            ProductHero(
                                product = product,
                                quantity = quantity,
                                imageHeight = imageHeight,
                                largeDisplay = largeDisplay,
                                onIncrease = {
                                    onQuantityChange(product.id, getQuantity(product.id) + 1)
                                },
                                onDecrease = {
                                    val current = getQuantity(product.id)
                                    if (current > 0) onQuantityChange(product.id, current - 1)
                                }
                            )

                            Spacer(modifier = Modifier.height(if (largeDisplay) 32.dp else 16.dp))

                            ProductInformationSheet(
                                product = product,
                                cartBarVisible = cartBarVisible,
                                largeDisplay = largeDisplay,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductPagerIndicator(
    pageCount: Int,
    currentPosition: Float,
    largeDisplay: Boolean
) {
    if (pageCount <= 1) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (largeDisplay) 30.dp else 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val proximity = (1f - abs(currentPosition - index)).coerceIn(0f, 1f)
            val baseWidth = if (largeDisplay) 10.dp else 7.dp
            val activeWidth = if (largeDisplay) 26.dp else 18.dp
            val width = baseWidth + (activeWidth * proximity)
            val color = lerp(Color(0xFFD7D7D7), NeonGreen, proximity)

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(width)
                    .height(if (largeDisplay) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun DetailTopBar(
    largeDisplay: Boolean,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (largeDisplay) 78.dp else 58.dp)
            .padding(horizontal = if (largeDisplay) 24.dp else 14.dp)
    ) {
        DetailTopButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            onClick = onBack,
            largeDisplay = largeDisplay,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = "Snack Information",
            color = DarkCharcoal,
            fontSize = if (largeDisplay) 24.sp else 17.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center)
        )

        DetailTopButton(
            icon = Icons.Default.MoreVert,
            contentDescription = "Más opciones",
            onClick = onCartClick,
            largeDisplay = largeDisplay,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun DetailTopButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    largeDisplay: Boolean,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(if (largeDisplay) 58.dp else 42.dp)
            .clip(CircleShape)
            .background(ControlBackground)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = DarkCharcoal,
            modifier = Modifier.size(if (largeDisplay) 29.dp else 21.dp)
        )
    }
}

@Composable
private fun ProductHero(
    product: Product,
    quantity: Int,
    imageHeight: Dp,
    largeDisplay: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(12.dp),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityButton(
                icon = Icons.Default.Remove,
                contentDescription = "Quitar",
                onClick = onDecrease,
                enabled = quantity > 0,
                largeDisplay = largeDisplay
            )

            AnimatedContent(
                targetState = quantity,
                transitionSpec = {
                    if (targetState > initialState) {
                        (
                            slideInVertically { it / 2 } +
                                fadeIn() +
                                scaleIn(initialScale = 0.8f)
                            ).togetherWith(
                            slideOutVertically { -it / 2 } +
                                fadeOut() +
                                scaleOut(targetScale = 0.8f)
                        )
                    } else {
                        (
                            slideInVertically { -it / 2 } +
                                fadeIn() +
                                scaleIn(initialScale = 0.8f)
                            ).togetherWith(
                            slideOutVertically { it / 2 } +
                                fadeOut() +
                                scaleOut(targetScale = 0.8f)
                        )
                    }
                },
                label = "quantity"
            ) { currentQuantity ->
                Text(
                    text = currentQuantity.toString(),
                    color = DarkCharcoal,
                    fontSize = if (largeDisplay) 78.sp else 60.sp,
                    lineHeight = if (largeDisplay) 78.sp else 60.sp,
                    fontWeight = FontWeight.Black
                )
            }

            QuantityButton(
                icon = Icons.Default.Add,
                contentDescription = "Agregar",
                onClick = onIncrease,
                enabled = true,
                largeDisplay = largeDisplay
            )
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = DarkCharcoal,
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
        ) {
            Text(
                text = String.format(Locale.US, "$%.2f", product.price),
                color = Color.White,
                fontSize = if (largeDisplay) 36.sp else 28.sp,
                lineHeight = if (largeDisplay) 42.sp else 34.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(
                    horizontal = if (largeDisplay) 30.dp else 22.dp,
                    vertical = if (largeDisplay) 11.dp else 8.dp
                )
            )
        }
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
    largeDisplay: Boolean
) {
    // Fondo usando NeonGreenV2 con opacidad reducida si está deshabilitado
    val backgroundColor = if (enabled) NeonGreenV2 else NeonGreenV2.copy(alpha = 0.30f)
    // Mantenemos el ícono legible ajustando también su opacidad
    val iconTint = DarkCharcoal.copy(alpha = if (enabled) 1f else 0.35f)
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(if (largeDisplay) 104.dp else 80.dp)
            .clip(CircleShape)
            .background(NeonGreen)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = DarkCharcoal.copy(alpha = if (enabled) 1f else 0.25f),
            modifier = Modifier.size(if (largeDisplay) 58.dp else 46.dp)
        )
    }
}

@Composable
private fun ProductInformationSheet(
    product: Product,
    cartBarVisible: Boolean,
    largeDisplay: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = if (largeDisplay) 32.dp else 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = if (largeDisplay) 42.dp else 30.dp,
            topEnd = if (largeDisplay) 42.dp else 30.dp
        ),
        color = DetailSheet,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (largeDisplay) 48.dp else 32.dp,
                    end = if (largeDisplay) 48.dp else 32.dp,
                    top = if (largeDisplay) 14.dp else 8.dp,
                    bottom = if (cartBarVisible) {
                        if (largeDisplay) 132.dp else 104.dp
                    } else {
                        20.dp
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(if (largeDisplay) 68.dp else 48.dp)
                    .height(if (largeDisplay) 7.dp else 5.dp)
                    .clip(CircleShape)
                    .background(DarkCharcoal)
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 20.dp else 13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    color = DarkCharcoal,
                    fontSize = if (largeDisplay) 30.sp else 20.sp,
                    fontWeight = FontWeight.Black
                )

                Box(
                    modifier = Modifier
                        .size(if (largeDisplay) 58.dp else 40.dp)
                        .clip(CircleShape)
                        .background(ControlBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Guardar",
                        tint = DarkCharcoal,
                        modifier = Modifier.size(if (largeDisplay) 28.dp else 20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (largeDisplay) 14.dp else 8.dp))

            Text(
                text = product.description.orEmpty(),
                color = Color(0xFF454545),
                fontSize = if (largeDisplay) 21.sp else 15.sp,
                lineHeight = if (largeDisplay) 29.sp else 20.sp,
                maxLines = if (largeDisplay) 7 else 5
            )
        }
    }
}
