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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.ui.graphics.vector.ImageVector
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
    var syscomRating by rememberSaveable { mutableStateOf<String?>(null) }
    var sunmiRating by rememberSaveable { mutableStateOf<String?>(null) }
    var recommendUs by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var highlights by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var comment by rememberSaveable { mutableStateOf("") }
    val canSubmit = overallRating in 1..5 && syscomRating != null && sunmiRating != null && recommendUs != null

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
                .widthIn(max = 680.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            RatingSection(
                icon = Icons.Default.Star,
                title = "Califica tu experiencia",
                subtitle = "¿Qué tan satisfecho estás en general?",
                rating = overallRating,
                onRatingChange = { overallRating = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChoiceSection(
                icon = Icons.Default.Handshake,
                title = "Servicio SYSCOM",
                subtitle = "¿Cómo calificas la atención de nuestro equipo?",
                options = serviceOptions,
                selected = syscomRating,
                onSelect = { syscomRating = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChoiceSection(
                icon = Icons.Default.PhoneAndroid,
                title = "Dispositivo SUNMI",
                subtitle = "¿Cómo calificas el dispositivo SUNMI?",
                options = serviceOptions,
                selected = sunmiRating,
                onSelect = { sunmiRating = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecommendSection(
                selected = recommendUs,
                onSelect = { recommendUs = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HighlightsSection(
                selected = highlights,
                onToggle = { option ->
                    highlights = if (option in highlights) {
                        highlights - option
                    } else {
                        highlights + option
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommentSection(
                comment = comment,
                onCommentChange = { comment = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onSubmit(
                        SurveyResponse(
                            overallRating = overallRating,
                            syscomRating = checkNotNull(syscomRating),
                            sunmiRating = checkNotNull(sunmiRating),
                            recommendUs = checkNotNull(recommendUs),
                            highlights = highlights,
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

@Composable
private fun RatingSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    val brandTheme = LocalBrandTheme.current

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionHeader(icon = icon, title = title, subtitle = subtitle)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..5).forEach { value ->
                    val filled = value <= rating
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
                                    selected = value == rating,
                                    role = Role.RadioButton,
                                    onClick = { onRatingChange(value) }
                                )
                                .semantics {
                                    contentDescription = "$value de 5 estrellas"
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
                            text = starLabels[value - 1],
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
}

@Composable
private fun ChoiceSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<ServiceOption>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    val brandTheme = LocalBrandTheme.current

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
                .padding(20.dp)
        ) {
            SectionHeader(icon = icon, title = title, subtitle = subtitle)

            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { option ->
                val isSelected = selected == option.label
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        brandTheme.accent
                    } else {
                        brandTheme.background
                    },
                    label = "optionBg"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) {
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
                            selected = isSelected,
                            role = Role.RadioButton,
                            onClick = { onSelect(option.label) }
                        )
                        .semantics(mergeDescendants = true) {
                            contentDescription = if (isSelected) {
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
                        defaultElevation = if (isSelected) 4.dp else 0.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = option.emoji,
                            fontSize = 26.sp
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = option.label,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondary
                                } else {
                                    brandTheme.textPrimary
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = option.description,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                                } else {
                                    brandTheme.textPrimary.copy(alpha = 0.55f)
                                },
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSecondary
                                    } else {
                                        brandTheme.textPrimary.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSecondary)
                                )
                            }
                        }
                    }
                }

                if (option != options.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RecommendSection(
    selected: Boolean?,
    onSelect: (Boolean) -> Unit
) {
    val brandTheme = LocalBrandTheme.current

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
                .padding(20.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.ThumbUp,
                title = "¿Nos recomendarías?",
                subtitle = "Tu opinión nos ayuda a crecer"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendOption(
                    text = "Sí, claro",
                    selected = selected == true,
                    onClick = { onSelect(true) },
                    modifier = Modifier.weight(1f)
                )
                RecommendOption(
                    text = "No, por ahora",
                    selected = selected == false,
                    onClick = { onSelect(false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RecommendOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandTheme = LocalBrandTheme.current
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) brandTheme.accent else brandTheme.background,
        label = "recommendBg"
    )

    Card(
        modifier = modifier
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            ),
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
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (selected) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Outlined.CheckCircle
                },
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    brandTheme.textPrimary.copy(alpha = 0.4f)
                },
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    brandTheme.textPrimary
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HighlightsSection(
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    val brandTheme = LocalBrandTheme.current

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
                .padding(20.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.Build,
                title = "¿Qué destacas más?",
                subtitle = "Selecciona todo lo que aplique"
            )

            Spacer(modifier = Modifier.height(16.dp))

            highlightOptions.forEach { option ->
                val isSelected = option in selected
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        brandTheme.accent
                    } else {
                        brandTheme.background
                    },
                    label = "highlightBg"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            role = Role.Checkbox,
                            onClick = { onToggle(option) }
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.weight(1f),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                brandTheme.textPrimary
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = if (isSelected) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = null,
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                brandTheme.textPrimary.copy(alpha = 0.35f)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (option != highlightOptions.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CommentSection(
    comment: String,
    onCommentChange: (String) -> Unit
) {
    val brandTheme = LocalBrandTheme.current

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
                .padding(20.dp)
        ) {
            SectionHeader(
                icon = Icons.Default.Build,
                title = "Cuéntanos más",
                subtitle = "Opcional - Tu opinión nos ayuda a mejorar"
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
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
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val brandTheme = LocalBrandTheme.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(brandTheme.accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = brandTheme.accent,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                color = brandTheme.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = brandTheme.textPrimary.copy(alpha = 0.55f),
                fontSize = 13.sp
            )
        }
    }
}