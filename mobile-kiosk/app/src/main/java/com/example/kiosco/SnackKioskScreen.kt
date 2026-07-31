package com.example.kiosco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.DarkCharcoal
import com.example.kiosco.ui.theme.LightBg
import com.example.kiosco.ui.theme.NeonGreen
import com.example.kiosco.ui.theme.TextMuted
import java.util.Locale

private val SearchBackground = Color(0xFFFFFFFF)
private val ImageBackground = Color(0xFFF2F2F2)
private val FilterBackground = Color(0xFFE9E9EC)

@Composable
fun SnackKioskScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product, Offset, Float) -> Unit,
    onCartClick: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }

    val categories = remember(products) {
        products
            .map { it.category.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }
    val categoryCounts = remember(products) {
        products.groupingBy { it.category.trim() }.eachCount()
    }
    val filteredProducts = remember(products, selectedCategories, searchQuery) {
        products.filter { product ->
            val matchesCategory =
                selectedCategories.isEmpty() || product.category.trim() in selectedCategories
            val matchesSearch =
                searchQuery.isBlank() ||
                    product.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    product.description.orEmpty().contains(searchQuery.trim(), ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, LightBg),
                    endY = 900f
                )
            )
            .statusBarsPadding()
    ) {
        val largeDisplay = maxWidth >= 700.dp || maxHeight >= 1000.dp
        val horizontalPadding = if (largeDisplay) 40.dp else 16.dp
        val gridSpacing = if (largeDisplay) 22.dp else 12.dp
        val cardMinWidth = if (largeDisplay) 250.dp else 160.dp

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = if (largeDisplay) 22.dp else 10.dp
                )
            ) {
                KioskHeader(
                    productCount = products.size,
                    largeDisplay = largeDisplay,
                    onCartClick = onCartClick
                )

                Spacer(modifier = Modifier.height(if (largeDisplay) 26.dp else 18.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    largeDisplay = largeDisplay
                )

                Spacer(modifier = Modifier.height(if (largeDisplay) 18.dp else 12.dp))
            }

            CategoryFilterRow(
                categories = categories,
                categoryCounts = categoryCounts,
                totalCount = products.size,
                selectedCategories = selectedCategories,
                largeDisplay = largeDisplay,
                horizontalPadding = horizontalPadding,
                onAllClick = { selectedCategories = emptySet() },
                onCategoryClick = { category ->
                    selectedCategories = if (category in selectedCategories) {
                        selectedCategories - category
                    } else {
                        selectedCategories + category
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = if (largeDisplay) 18.dp else 12.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        searchQuery.isNotBlank() -> "Resultados para “${searchQuery.trim()}”"
                        selectedCategories.isNotEmpty() -> "Selección personalizada"
                        else -> "Todos los productos"
                    },
                    color = DarkCharcoal,
                    fontSize = if (largeDisplay) 22.sp else 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = DarkCharcoal
                ) {
                    Text(
                        text = "${filteredProducts.size} ${if (filteredProducts.size == 1) "producto" else "productos"}",
                        color = Color.White,
                        fontSize = if (largeDisplay) 14.sp else 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = if (largeDisplay) 15.dp else 10.dp,
                            vertical = if (largeDisplay) 8.dp else 6.dp
                        )
                    )
                }
            }

            val filteredIds = filteredProducts.map { it.id }

            AnimatedContent(
                targetState = filteredIds,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 240)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 240))
                },
                contentKey = { it },
                label = "filteredProducts",
                modifier = Modifier.weight(1f)
            ) { visibleIds ->
                val visibleProducts = products.filter { it.id in visibleIds }
                if (visibleProducts.isEmpty()) {
                    EmptyProductsState(
                        largeDisplay = largeDisplay,
                        onClearFilters = {
                            searchQuery = ""
                            selectedCategories = emptySet()
                        }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = cardMinWidth),
                        contentPadding = PaddingValues(
                            start = horizontalPadding,
                            end = horizontalPadding,
                            bottom = 118.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                        verticalArrangement = Arrangement.spacedBy(gridSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        gridItems(
                            items = visibleProducts,
                            key = { it.id },
                            contentType = { "product" }
                        ) { product ->
                            SnackCard(
                                product = product,
                                largeDisplay = largeDisplay,
                                onClick = { onProductClick(product) },
                                onAdd = { center, size -> onAddToCart(product, center, size) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KioskHeader(
    productCount: Int,
    largeDisplay: Boolean,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SNACK",
                    fontSize = if (largeDisplay) 40.sp else 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkCharcoal,
                    letterSpacing = (-0.8).sp
                )
                Box(
                    modifier = Modifier
                        .padding(start = 7.dp)
                        .size(if (largeDisplay) 15.dp else 11.dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                )
            }
            Text(
                text = "¿Qué se te antoja hoy?  •  $productCount opciones",
                fontSize = if (largeDisplay) 18.sp else 12.sp,
                color = TextMuted
            )
        }

        IconButton(
            onClick = onCartClick,
            modifier = Modifier
                .size(if (largeDisplay) 66.dp else 48.dp)
                .clip(CircleShape)
                .background(DarkCharcoal)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Abrir carrito",
                tint = NeonGreen,
                modifier = Modifier.size(if (largeDisplay) 30.dp else 22.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    largeDisplay: Boolean
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (largeDisplay) 68.dp else 56.dp),
        placeholder = {
            Text(
                text = "Buscar snacks, dulces o bebidas...",
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = if (largeDisplay) 17.sp else 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(if (largeDisplay) 26.dp else 21.dp)
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda",
                        tint = DarkCharcoal
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(if (largeDisplay) 24.dp else 18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBackground,
            unfocusedContainerColor = SearchBackground,
            focusedBorderColor = DarkCharcoal,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = DarkCharcoal,
            focusedTextColor = DarkCharcoal,
            unfocusedTextColor = DarkCharcoal
        )
    )
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    categoryCounts: Map<String, Int>,
    totalCount: Int,
    selectedCategories: Set<String>,
    largeDisplay: Boolean,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    onAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(if (largeDisplay) 12.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryChip(
            label = "Todos",
            count = totalCount,
            selected = selectedCategories.isEmpty(),
            largeDisplay = largeDisplay,
            onClick = onAllClick,
            modifier = Modifier.weight(1f)
        )

        categories.forEach { category ->
            CategoryChip(
                label = category.displayCategory(),
                count = categoryCounts[category] ?: 0,
                selected = category in selectedCategories,
                largeDisplay = largeDisplay,
                onClick = { onCategoryClick(category) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    count: Int,
    selected: Boolean,
    largeDisplay: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "filterPress"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .animateContentSize()
            .clip(RoundedCornerShape(50))
            .background(if (selected) DarkCharcoal else FilterBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = if (largeDisplay) 18.dp else 13.dp,
                vertical = if (largeDisplay) 12.dp else 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(if (largeDisplay) 19.dp else 15.dp)
            )
        }

        Text(
            text = label,
            color = if (selected) Color.White else DarkCharcoal,
            fontSize = if (largeDisplay) 15.sp else 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = count.toString(),
            color = if (selected) NeonGreen else TextMuted,
            fontSize = if (largeDisplay) 13.sp else 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun SnackCard(
    product: Product,
    onClick: () -> Unit,
    onAdd: (startCenter: Offset, startSize: Float) -> Unit,
    largeDisplay: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var imageCenter by remember { mutableStateOf(Offset.Zero) }
    var imageSizePx by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardPress"
    )

    Card(
        shape = RoundedCornerShape(if (largeDisplay) 30.dp else 22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 9.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(modifier = Modifier.padding(if (largeDisplay) 18.dp else 12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.16f)
                    .clip(RoundedCornerShape(if (largeDisplay) 23.dp else 17.dp))
                    .background(ImageBackground)
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        val size = coords.size
                        imageCenter = Offset(
                            pos.x + size.width / 2f,
                            pos.y + size.height / 2f
                        )
                        imageSizePx = minOf(size.width, size.height).toFloat() * 0.55f
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (largeDisplay) 18.dp else 10.dp),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(if (largeDisplay) 12.dp else 8.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Text(
                        text = product.category.displayCategory(),
                        color = DarkCharcoal,
                        fontSize = if (largeDisplay) 11.sp else 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(
                            horizontal = if (largeDisplay) 11.dp else 8.dp,
                            vertical = if (largeDisplay) 6.dp else 4.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (largeDisplay) 14.dp else 9.dp))

            Text(
                text = product.name,
                fontSize = if (largeDisplay) 20.sp else 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.description.orEmpty(),
                color = TextMuted,
                fontSize = if (largeDisplay) 12.sp else 9.sp,
                lineHeight = if (largeDisplay) 17.sp else 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(if (largeDisplay) 36.dp else 26.dp)
            )

            Spacer(modifier = Modifier.height(if (largeDisplay) 12.dp else 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "$%.2f", product.price),
                    color = DarkCharcoal,
                    fontWeight = FontWeight.Black,
                    fontSize = if (largeDisplay) 20.sp else 15.sp
                )

                Surface(
                    modifier = Modifier
                        .size(if (largeDisplay) 48.dp else 38.dp)
                        .clickable(
                            onClick = {
                                onAdd(imageCenter, imageSizePx.coerceAtLeast(48f))
                            }
                        ),
                    shape = CircleShape,
                    color = NeonGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar ${product.name}",
                            tint = DarkCharcoal,
                            modifier = Modifier.size(if (largeDisplay) 25.dp else 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProductsState(
    largeDisplay: Boolean,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (largeDisplay) 92.dp else 68.dp)
                .clip(CircleShape)
                .background(FilterBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(if (largeDisplay) 40.dp else 30.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "No encontramos productos",
            color = DarkCharcoal,
            fontSize = if (largeDisplay) 24.sp else 18.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Prueba otra búsqueda o limpia los filtros seleccionados.",
            color = TextMuted,
            fontSize = if (largeDisplay) 16.sp else 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Limpiar filtros",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun CartSummaryBar(
    totalItems: Int,
    totalPrice: Double,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    bagBounceTrigger: Int = 0,
    onBagPositioned: (Offset) -> Unit = {}
) {
    val bagScale = remember { Animatable(1f) }
    LaunchedEffect(bagBounceTrigger) {
        if (bagBounceTrigger == 0) return@LaunchedEffect
        bagScale.snapTo(1f)
        bagScale.animateTo(
            1.18f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        bagScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .widthIn(max = 900.dp)
            .fillMaxWidth()
    ) {
        val largeBar = maxWidth >= 700.dp

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (largeBar) 88.dp else 72.dp)
                .padding(end = if (largeBar) 24.dp else 16.dp),
            shape = RoundedCornerShape(50),
            color = DarkCharcoal,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = if (largeBar) 30.dp else 20.dp,
                        end = if (largeBar) 10.dp else 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(if (largeBar) 46.dp else 36.dp)
                            .graphicsLayer {
                                scaleX = bagScale.value
                                scaleY = bagScale.value
                            }
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                val size = coords.size
                                onBagPositioned(
                                    Offset(
                                        pos.x + size.width / 2f,
                                        pos.y + size.height / 2f
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(if (largeBar) 23.dp else 18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(if (largeBar) 15.dp else 10.dp))

                    Column {
                        Text(
                            text = "$totalItems ${if (totalItems == 1) "producto" else "productos"}",
                            color = Color.White.copy(alpha = 0.62f),
                            fontSize = if (largeBar) 13.sp else 10.sp
                        )
                        Text(
                            text = String.format(Locale.US, "$%.2f", totalPrice),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = if (largeBar) 22.sp else 17.sp
                        )
                    }
                }

                Button(
                    onClick = onCartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(
                        horizontal = if (largeBar) 28.dp else 18.dp,
                        vertical = if (largeBar) 15.dp else 11.dp
                    )
                ) {
                    Text(
                        text = "Ver carrito",
                        color = DarkCharcoal,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (largeBar) 17.sp else 13.sp
                    )
                }
            }
        }
    }
}

private fun String.displayCategory(): String =
    lowercase(Locale.getDefault()).replaceFirstChar { character ->
        if (character.isLowerCase()) {
            character.titlecase(Locale.getDefault())
        } else {
            character.toString()
        }
    }
