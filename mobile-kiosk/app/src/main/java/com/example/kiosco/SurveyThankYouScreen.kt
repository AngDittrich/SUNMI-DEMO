package com.example.kiosco

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kiosco.ui.theme.LocalBrandTheme

@Composable
fun SurveyThankYouScreen(
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onReturnHome: () -> Unit,
    onBack: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    var automaticPrintStarted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!automaticPrintStarted) {
            automaticPrintStarted = true
            onPrint()
        } else if (printState == TicketPrintState.Idle) {
            onPrint()
        }
    }

    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brandTheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = brandTheme.surface,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Gracias por tu opinión!",
                        color = brandTheme.textPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Presenta este cupón en tu próxima visita",
                        color = brandTheme.textPrimary.copy(alpha = 0.72f),
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        AsyncImage(
                            model = SURVEY_QR_ASSET,
                            contentDescription = "Código QR del cupón $SURVEY_COUPON",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(240.dp)
                                .padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Cupón",
                        color = brandTheme.textPrimary.copy(alpha = 0.72f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = SURVEY_COUPON,
                        color = brandTheme.textPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val printStatus = when (printState) {
                TicketPrintState.Idle,
                TicketPrintState.Printing -> "Imprimiendo tu cupón…"

                TicketPrintState.Printed ->
                    "Tu cupón se imprimió correctamente. Por favor recógelo."

                is TicketPrintState.Failed ->
                    "No se pudo imprimir el cupón: ${printState.message}"
            }

            Text(
                text = printStatus,
                color = brandTheme.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (printState) {
                TicketPrintState.Printed -> {
                    SurveyPrintButton(
                        text = "Volver al inicio",
                        onClick = onReturnHome
                    )
                }

                TicketPrintState.Idle,
                TicketPrintState.Printing -> {
                    SurveyPrintButton(
                        text = "Continuar sin imprimir",
                        onClick = onReturnHome
                    )
                }

                is TicketPrintState.Failed -> {
                    if (printState.retryable) {
                        SurveyPrintButton(
                            text = "Imprimir de nuevo",
                            onClick = onPrint
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    SurveyPrintButton(
                        text = "Continuar sin imprimir",
                        onClick = onReturnHome
                    )
                }
            }
        }
    }
}

@Composable
private fun SurveyPrintButton(
    text: String,
    onClick: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = brandTheme.base,
            contentColor = brandTheme.onBase
        )
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
