package com.example.kiosco

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiosco.ui.theme.LocalBrandTheme

private val serviceOptions = listOf("Excelente", "Buena", "Regular", "Mala")

@Composable
fun SurveyScreen(
    onSubmit: (SurveyResponse) -> Unit,
    onBack: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    val focusManager = LocalFocusManager.current
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
                    colors = listOf(brandTheme.surface, brandTheme.background)
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
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = brandTheme.textPrimary
                    )
                }
                Text(
                    text = "¿Cómo fue tu experiencia?",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp),
                    color = brandTheme.textPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Califica tu experiencia general",
                color = brandTheme.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..5).forEach { rating ->
                    val filled = rating <= overallRating
                    Box(
                        modifier = Modifier
                            .size(60.dp)
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
                                brandTheme.textPrimary.copy(alpha = 0.35f)
                            },
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "¿Qué te pareció la atención?",
                modifier = Modifier.fillMaxWidth(),
                color = brandTheme.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                serviceOptions.forEach { option ->
                    val selected = serviceRating == option
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selected) {
                                    brandTheme.accent
                                } else {
                                    brandTheme.surface
                                }
                            )
                            .border(
                                width = 2.dp,
                                color = if (selected) {
                                    brandTheme.accent
                                } else {
                                    brandTheme.textPrimary.copy(alpha = 0.18f)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .selectable(
                                selected = selected,
                                role = Role.RadioButton,
                                onClick = { serviceRating = option }
                            )
                            .semantics(mergeDescendants = true) {
                                contentDescription = if (selected) {
                                    "$option, seleccionado"
                                } else {
                                    "$option, no seleccionado"
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                brandTheme.textPrimary
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 144.dp),
                label = { Text("Comentarios (opcional)") },
                placeholder = { Text("Cuéntanos más sobre tu experiencia") },
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = brandTheme.surface,
                    unfocusedContainerColor = brandTheme.surface,
                    focusedBorderColor = brandTheme.accent,
                    unfocusedBorderColor = brandTheme.textPrimary.copy(alpha = 0.18f),
                    cursorColor = brandTheme.accent,
                    focusedTextColor = brandTheme.textPrimary,
                    unfocusedTextColor = brandTheme.textPrimary,
                    focusedLabelColor = brandTheme.textPrimary,
                    unfocusedLabelColor = brandTheme.textPrimary.copy(alpha = 0.65f),
                    focusedPlaceholderColor = brandTheme.textPrimary.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = brandTheme.textPrimary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
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
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandTheme.base,
                    contentColor = brandTheme.onBase,
                    disabledContainerColor = brandTheme.base.copy(alpha = 0.3f),
                    disabledContentColor = brandTheme.onBase.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "Enviar opinión",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
