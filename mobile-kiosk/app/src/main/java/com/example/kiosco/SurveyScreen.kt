package com.example.kiosco

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiosco.ui.theme.LocalBrandTheme

private data class ServiceOption(
    val label: String,
    val emoji: String,
    val description: String
)

private val serviceOptions = listOf(
    ServiceOption("Excelente", "⭐", "Superó mis expectativas"),
    ServiceOption("Buena", "👍", "Cumplió con lo esperado"),
    ServiceOption("Regular", "😐", "Puede mejorar"),
    ServiceOption("Mala", "👎", "No cumplió expectativas")
)

private val starLabels = listOf("Muy mala", "Mala", "Regular", "Buena", "Muy buena")

@Composable
fun SurveyScreen(
    onSubmit: (SurveyResponse) -> Unit,
    onBack: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    var overallRating by rememberSaveable { mutableIntStateOf(0) }
    var serviceRating by rememberSaveable { mutableStateOf<String?>(null) }
    var comment by rememberSaveable { mutableStateOf("") }
    val canSubmit = overallRating in 1..5 && serviceRating != null

    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        brandTheme.surface,
                        brandTheme.background
                    )
                )
            )
            .statusBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Encuesta de Satisfacción",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp),
                    color = brandTheme.textPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = brandTheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = brandTheme.accent,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¿Cómo fue tu experiencia?",
                        color = brandTheme.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Selecciona una estrella para calificar",
                        color = brandTheme.textPrimary.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = brandTheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Califica tu experiencia",
                        color = brandTheme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..5).forEach { rating ->
                            val filled = rating <= overallRating
                            val scale by animateFloatAsState(
                                targetValue = if (filled) 1.15f else 1f,
                                animationSpec = spring(
                                    dampingRatio = 0.4f,
                                    stiffness = 300f
                                ),
                                label = "starScale"
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .scale(scale)
                                        .selectable(
                                            selected = rating == overallRating,
                                            role = Role.RadioButton,
                                            onClick = { overallRating = rating }
                                        )
                                        .semantics {
                                            contentDescription = "$rating de 5 estrellas"
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (filled) {
                                                    brandTheme.accent.copy(alpha = 0.15f)
                                                } else {
                                                    Color.Transparent
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (filled) {
                                                Icons.Filled.Star
                                            } else {
                                                Icons.Outlined.Star
                                            },
                                            contentDescription = null,
                                            tint = if (filled) {
                                                brandTheme.accent
                                            } else {
                                                brandTheme.textPrimary.copy(alpha = 0.3f)
                                            },
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = starLabels[rating - 1],
                                    color = if (filled) {
                                        brandTheme.accent
                                    } else {
                                        brandTheme.textPrimary.copy(alpha = 0.5f)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (filled) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = brandTheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "¿Qué te pareció la atención?",
                        color = brandTheme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    serviceOptions.forEach { option ->
                        val selected = serviceRating == option.label
                        val backgroundColor by animateColorAsState(
                            targetValue = if (selected) {
                                brandTheme.accent
                            } else {
                                brandTheme.background
                            },
                            label = "optionBg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = if (selected) {
                                brandTheme.accent
                            } else {
                                brandTheme.textPrimary.copy(alpha = 0.12f)
                            },
                            label = "optionBorder"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected,
                                    role = Role.RadioButton,
                                    onClick = { serviceRating = option.label }
                                )
                                .semantics(mergeDescendants = true) {
                                    contentDescription = if (selected) {
                                        "${option.label}, seleccionado"
                                    } else {
                                        "${option.label}, no seleccionado"
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = backgroundColor
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (selected) 4.dp else 0.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = option.emoji,
                                    fontSize = 28.sp
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = option.label,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.onSecondary
                                        } else {
                                            brandTheme.textPrimary
                                        },
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = option.description,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                                        } else {
                                            brandTheme.textPrimary.copy(alpha = 0.55f)
                                        },
                                        fontSize = 13.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = if (selected) {
                                                MaterialTheme.colorScheme.onSecondary
                                            } else {
                                                brandTheme.textPrimary.copy(alpha = 0.3f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.onSecondary)
                                        )
                                    }
                                }
                            }
                        }

                        if (option != serviceOptions.last()) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = brandTheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Cuéntanos más",
                        color = brandTheme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Opcional - Tu opinión nos ayuda a mejorar",
                        color = brandTheme.textPrimary.copy(alpha = 0.55f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = { Text("Escribe aquí tus comentarios...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = brandTheme.background,
                            unfocusedContainerColor = brandTheme.background,
                            focusedBorderColor = brandTheme.accent,
                            unfocusedBorderColor = brandTheme.textPrimary.copy(alpha = 0.12f),
                            cursorColor = brandTheme.accent,
                            focusedTextColor = brandTheme.textPrimary,
                            unfocusedTextColor = brandTheme.textPrimary,
                            focusedPlaceholderColor = brandTheme.textPrimary.copy(alpha = 0.4f),
                            unfocusedPlaceholderColor = brandTheme.textPrimary.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onSubmit(
                        SurveyResponse(
                            overallRating = overallRating,
                            serviceRating = checkNotNull(serviceRating),
                            comment = comment
                        )
                    )
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandTheme.base,
                    contentColor = brandTheme.onBase,
                    disabledContainerColor = brandTheme.base.copy(alpha = 0.25f),
                    disabledContentColor = brandTheme.onBase.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enviar opinión",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
