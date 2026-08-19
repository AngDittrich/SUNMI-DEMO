package com.example.kiosco

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.data.ProductImages
import com.example.kiosco.data.ProductRepository
import com.example.kiosco.ui.theme.LocalBrandTheme
import com.example.kiosco.ui.theme.TextMuted
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AdminProductListScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onCreateClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleted: suspend () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    var searchQuery by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<Product?>(null) }
    var deleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = remember(context) { ProductRepository(context) }

    val filtered = remember(products, searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) products
        else products.filter {
            it.name.contains(q, ignoreCase = true) ||
                it.barcode.contains(q, ignoreCase = true) ||
                it.nfcId.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brandTheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Admin productos",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = brandTheme.textPrimary
                )
                Text(
                    text = "${products.size} en catálogo · escanea para editar o crear",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brandTheme.base)
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "Salir modo empleado",
                    tint = brandTheme.onBase
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                if (!(newQuery.all { it.isDigit() } && newQuery.length >= 8)) {
                    searchQuery = newQuery
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar nombre, categoría o código…") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandTheme.accent,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = brandTheme.surface,
                unfocusedContainerColor = brandTheme.surface
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = Color(0xFFE53935), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filtered, key = { it.id }) { product ->
                AdminProductRow(
                    product = product,
                    onClick = { onProductClick(product) },
                    onDelete = { pendingDelete = product }
                )
            }
        }

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = brandTheme.accent,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nuevo producto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }

    pendingDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { if (!deleting) pendingDelete = null },
            title = { Text("¿Eliminar producto?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se eliminará \"${product.name}\" del catálogo. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            deleting = true
                            errorMessage = null
                            try {
                                repository.deleteProduct(product.id)
                                pendingDelete = null
                                onDeleted()
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Error al eliminar"
                            } finally {
                                deleting = false
                            }
                        }
                    },
                    enabled = !deleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (deleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDelete = null },
                    enabled = !deleting
                ) {
                    Text("Cancelar", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = brandTheme.surface
        )
    }
}

@Composable
private fun AdminProductRow(
    product: Product,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = brandTheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = brandTheme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.barcode.ifBlank { "Sin código" },
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = String.format(Locale.US, "$%.2f · %s", product.price, product.category),
                    color = brandTheme.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun AdminProductFormScreen(
    products: List<Product>,
    editingProductId: Int?,
    initialBarcode: String,
    onBack: () -> Unit,
    onSaved: suspend () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    val existing = editingProductId?.let { id -> products.find { it.id == id } }
    var name by remember(existing?.id, initialBarcode) {
        mutableStateOf(existing?.name.orEmpty())
    }
    var priceText by remember(existing?.id) {
        mutableStateOf(existing?.price?.let { String.format(Locale.US, "%.2f", it) }.orEmpty())
    }
    var category by remember(existing?.id) {
        mutableStateOf(existing?.category.orEmpty())
    }
    var barcode by remember(existing?.id, initialBarcode) {
        mutableStateOf(existing?.barcode?.takeIf { it.isNotBlank() } ?: initialBarcode)
    }
    var nfcId by remember(existing?.id) {
        mutableStateOf(existing?.nfcId.orEmpty())
    }
    var imageUrl by remember(existing?.id) {
        mutableStateOf(
            existing?.imageUrl?.takeIf { it.isNotBlank() } ?: ProductImages.PLACEHOLDER
        )
    }
    var description by remember(existing?.id) {
        mutableStateOf(existing?.description.orEmpty())
    }
    var saving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = remember(context) { ProductRepository(context) }
    val isEdit = existing != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brandTheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = brandTheme.textPrimary
                )
            }
            Text(
                text = if (isEdit) "Editar producto" else "Nuevo producto",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = brandTheme.textPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            AdminField("Nombre", name) { name = it }
            AdminField(
                label = "Precio",
                value = priceText,
                keyboardType = KeyboardType.Decimal
            ) { priceText = it }
            AdminField("Categoría", category) { category = it }
            AdminField("Código de barras", barcode) { barcode = it }
            AdminField("ID NFC (tag)", nfcId) { nfcId = it }
            AdminField(
                label = "Imagen (asset local)",
                value = imageUrl,
                onValueChange = { imageUrl = it }
            )
            AdminField("Descripción", description, singleLine = false) { description = it }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = Color(0xFFE53935), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = {
                val price = priceText.toDoubleOrNull()
                if (name.isBlank() || category.isBlank() || barcode.isBlank() ||
                    price == null || price < 0
                ) {
                    errorMessage = "Completa nombre, precio, categoría y código"
                    return@Button
                }
                val resolvedImage = imageUrl.trim().ifBlank { ProductImages.PLACEHOLDER }
                scope.launch {
                    saving = true
                    errorMessage = null
                    try {
                        if (isEdit) {
                            repository.updateProduct(
                                existing!!.copy(
                                    name = name.trim(),
                                    price = price,
                                    category = category.trim(),
                                    barcode = barcode.trim(),
                                    nfcId = nfcId.trim(),
                                    imageUrl = resolvedImage,
                                    description = description.trim()
                                )
                            )
                        } else {
                            repository.createProduct(
                                name = name.trim(),
                                price = price,
                                category = category.trim(),
                                barcode = barcode.trim(),
                                nfcId = nfcId.trim(),
                                imageUrl = resolvedImage,
                                description = description.trim()
                            )
                        }
                        onSaved()
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "No se pudo guardar"
                    } finally {
                        saving = false
                    }
                }
            },
            enabled = !saving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = brandTheme.accent,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            if (saving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isEdit) "Guardar cambios" else "Crear producto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AdminField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = brandTheme.accent,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = brandTheme.surface,
            unfocusedContainerColor = brandTheme.surface
        )
    )
}
