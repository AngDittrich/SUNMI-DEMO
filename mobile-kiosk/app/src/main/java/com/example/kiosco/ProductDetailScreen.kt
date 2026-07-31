package com.example.kiosco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.NeonGreen
import java.util.Locale

private val DetailBackground = Color(0xFFF8F8F8)
private val DetailSheet = Color(0xFFFFFFFF)
private val ControlBackground = Color(0xFFF2F2F2)

@Composable
fun ProductDetailScreen(
    products: List<Product>,
    initialProductId: Int,
    getQuantity: (Int) -> Int,
    onQuantityChange: (Int, Int) -> Unit,
    totalItems: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    val product = products.firstOrNull { it.id == initialProductId } ?: return
    val quantity = getQuantity(product.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .background(DetailBackground)
                .statusBarsPadding()
        ) {
            DetailTopBar(onBack = onBack, onCartClick = onCartClick)

            ProductHero(
                product = product,
                quantity = quantity,
                onIncrease = { onQuantityChange(product.id, quantity + 1) },
                onDecrease = {
                    if (quantity > 0) onQuantityChange(product.id, quantity - 1)
                },
                modifier = Modifier.weight(1f)
            )

            ProductInformationSheet(
                product = product,
                totalItems = totalItems,
                onCartClick = onCartClick
            )
        }
    }
}

@Composable
private fun DetailTopBar(
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 14.dp)
    ) {
        DetailTopButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = "Snack Information",
            color = DarkCharcoal,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center)
        )

        DetailTopButton(
            icon = Icons.Default.MoreVert,
            contentDescription = "Más opciones",
            onClick = onCartClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun DetailTopButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(ControlBackground)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = DarkCharcoal,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun ProductHero(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White,
                radius = size.width * 0.57f,
                center = Offset(
                    x = size.width / 2,
                    y = size.height * 0.18f
                )
            )
            drawCircle(
                color = Color.White,
                radius = size.width * 0.32f,
                center = Offset(
                    x = -size.width * 0.12f,
                    y = size.height * 0.75f
                )
            )
            drawCircle(
                color = Color.White,
                radius = size.width * 0.32f,
                center = Offset(
                    x = size.width * 1.12f,
                    y = size.height * 0.75f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Agregar",
                    onClick = onIncrease,
                    enabled = true
                )

                AnimatedContent(
                    targetState = quantity,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "quantity"
                ) { currentQuantity ->
                    Text(
                        text = currentQuantity.toString(),
                        color = DarkCharcoal,
                        fontSize = 42.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                QuantityButton(
                    icon = Icons.Default.Remove,
                    contentDescription = "Quitar",
                    onClick = onDecrease,
                    enabled = quantity > 0
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = DarkCharcoal,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "$%05.2f", product.price),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = DarkCharcoal.copy(alpha = if (enabled) 1f else 0.25f),
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun ProductInformationSheet(
    product: Product,
    totalItems: Int,
    onCartClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        color = DetailSheet,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(52.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(DarkCharcoal)
            )

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    color = DarkCharcoal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ControlBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Guardar",
                        tint = DarkCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("This popcorn may have a sweet taste with a touch of caramel or fruit syrup which gives it a rich taste and pampers the tongue. ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                        append("Read More")
                    }
                },
                color = Color(0xFF454545),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            DetailCartBar(
                totalItems = totalItems,
                onClick = onCartClick
            )
        }
    }
}

@Composable
private fun DetailCartBar(
    totalItems: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp),
        shape = RoundedCornerShape(34.dp),
        color = DarkCharcoal
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    lineHeight = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalItems donuts",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "›››",
                        color = NeonGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(25.dp),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = DarkCharcoal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cart",
                    color = DarkCharcoal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
