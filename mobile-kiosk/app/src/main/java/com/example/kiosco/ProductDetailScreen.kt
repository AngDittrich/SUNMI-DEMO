package com.example.kiosco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    products: List<Product>,
    initialProductId: Int,
    getQuantity: (Int) -> Int,
    onQuantityChange: (Int, Int) -> Unit,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    val initialIndex = products.indexOfFirst { it.id == initialProductId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialIndex) { products.size }
    val scope = rememberCoroutineScope()
    val currentProduct = products[pagerState.currentPage]
    val currentQuantity = getQuantity(currentProduct.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkCharcoal,
                        Color(0xFF1A1A1A),
                        DarkCharcoal
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            DetailTopBar(
                onBack = onBack,
                onCartClick = onCartClick
            )

            // Carousel with HorizontalPager
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 80.dp),
                    pageSpacing = 24.dp
                ) { page ->
                    val product = products[page]
                    val quantity = getQuantity(product.id)
                    val pageOffset = (pagerState.currentPage - page).absoluteValue

                    ProductDetailPage(
                        product = product,
                        quantity = quantity,
                        scale = 1f - (pageOffset * 0.1f).coerceAtMost(0.2f),
                        onIncrease = { onQuantityChange(product.id, quantity + 1) },
                        onDecrease = { if (quantity > 0) onQuantityChange(product.id, quantity - 1) }
                    )
                }
            }

            // Page indicator
            PagerIndicator(
                pageCount = products.size,
                currentPage = pagerState.currentPage
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailTopBar(
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Text(
            text = "Snack Information",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onCartClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Más opciones",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ProductDetailPage(
    product: Product,
    quantity: Int,
    scale: Float,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pageScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scale(animatedScale)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product name (top)
        Spacer(modifier = Modifier.height(8.dp))

        // Product image with minus/plus buttons on sides
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Minus button (left)
                QuantityButton(
                    icon = Icons.Default.Remove,
                    onClick = onDecrease,
                    enabled = quantity > 0
                )

                // Product image (center)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Plus button (right)
                QuantityButton(
                    icon = Icons.Default.Add,
                    onClick = onIncrease,
                    enabled = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product name
        Text(
            text = product.name,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price
        Text(
            text = "$${String.format("%.2f", product.price)}",
            color = NeonGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quantity display
        QuantityDisplay(quantity = quantity)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.3f,
        animationSpec = tween(200),
        label = "btnAlpha"
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f * backgroundColor))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun QuantityDisplay(quantity: Int) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 32.dp, vertical = 12.dp)
    ) {
        AnimatedContent(
            targetState = quantity,
            transitionSpec = {
                (scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn())
                    .togetherWith(scaleOut(animationSpec = tween(150)) + fadeOut(tween(150)))
            },
            label = "quantityAnim"
        ) { qty ->
            Text(
                text = qty.toString(),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateFloatAsState(
                targetValue = if (isSelected) 32f else 12f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dotWidth"
            )
            val color = if (isSelected) NeonGreen else Color.White.copy(alpha = 0.3f)

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(width = width.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
