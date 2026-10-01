package com.example.kiosco.hotel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiosco.TicketPrintState

private val code39 = mapOf(
    '0' to "nnnwwnwnn",
    '1' to "wnnwnnnnw",
    '2' to "nnwwnnnnw",
    '3' to "wnwwnnnnn",
    '4' to "nnnwwnnnw",
    '5' to "wnnwwnnnn",
    '6' to "nnwwwnnnn",
    '7' to "nnnwnnwnw",
    '8' to "wnnwnnwnn",
    '9' to "nnwwnnwnn",
    '*' to "nwnnwnwnn",
)

@Composable
fun HotelTicketScreen(
    reservation: HotelReservation,
    printState: TicketPrintState,
    checkoutState: TicketPrintState,
    onPrint: () -> Unit,
    onCheckOut: () -> Unit,
    onNewStay: () -> Unit,
    onBack: () -> Unit,
) {
    val reservationReady = printState == TicketPrintState.Printed ||
        printState is TicketPrintState.Submitted
    val checkoutDone = checkoutState == TicketPrintState.Printed ||
        checkoutState is TicketPrintState.Submitted
    val status = when {
        checkoutState == TicketPrintState.Printing -> "Imprimiendo check-out…"
        checkoutDone -> "Check-out completado"
        checkoutState is TicketPrintState.Failed -> checkoutState.message
        printState == TicketPrintState.Printing -> "Imprimiendo reserva…"
        else -> printStatus(printState)
    }

    LaunchedEffect(reservation.id) {
        onPrint()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HotelColors.Indigo)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = "Detalles de la reserva",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            TicketCard(reservation)
        }

        if (status != null) {
            Text(
                text = status,
                color = if (printState is TicketPrintState.Failed) Color(0xFFFFCDD2) else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (printState is TicketPrintState.Failed) {
                Button(
                    onClick = onPrint,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HotelColors.Yellow,
                        contentColor = HotelColors.Indigo
                    )
                ) {
                    Text("Reintentar impresión", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
            if (reservationReady) {
                Button(
                    onClick = if (checkoutDone) onNewStay else onCheckOut,
                    enabled = checkoutState != TicketPrintState.Printing,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HotelColors.Yellow,
                        contentColor = HotelColors.Indigo,
                        disabledContainerColor = HotelColors.Yellow.copy(alpha = 0.55f),
                        disabledContentColor = HotelColors.Indigo.copy(alpha = 0.7f)
                    )
                ) {
                    Text(
                        text = when {
                            checkoutState == TicketPrintState.Printing -> "Imprimiendo…"
                            checkoutDone -> "Nueva reserva"
                            else -> "Hacer check-out"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

private fun printStatus(printState: TicketPrintState): String? = when (printState) {
    TicketPrintState.Printed -> "Reserva impresa"
    is TicketPrintState.Submitted -> printState.message
    is TicketPrintState.Failed -> printState.message
    TicketPrintState.Idle,
    TicketPrintState.Printing -> null
}

@Composable
private fun TicketCard(reservation: HotelReservation) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pagado",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HotelColors.PaidGreen)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Código de reserva",
                        color = HotelColors.TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "#${reservation.bookingCode}",
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = reservation.hotelName,
                color = HotelColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = reservation.roomName,
                color = HotelColors.TextMuted,
                fontSize = 13.sp
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = HotelColors.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = reservation.hotelAddress,
                    color = HotelColors.TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            Text(
                text = "${formatShortDate(reservation.checkInDate)} – ${formatShortDate(reservation.checkOutDate)}",
                color = HotelColors.Indigo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HotelColors.CardBorder)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Inicio de estancia", color = HotelColors.TextMuted, fontSize = 11.sp)
                    Text(
                        text = formatStayDate(reservation.checkInDate),
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = HotelColors.Yellow,
                    modifier = Modifier
                        .padding(start = 6.dp, end = 6.dp, top = 12.dp)
                        .size(16.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Fin de estancia", color = HotelColors.TextMuted, fontSize = 11.sp)
                    Text(
                        text = formatStayDate(reservation.checkOutDate),
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = if (reservation.nights == 1) "1 noche" else "${reservation.nights} noches",
                        color = HotelColors.TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Costo ${formatHotelMoney(reservation.total)}",
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Datos del huésped",
                color = HotelColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            GuestLine(Icons.Filled.Phone, reservation.guestPhone)
            GuestLine(Icons.Filled.Person, reservation.guestName)
            GuestLine(Icons.Filled.Email, reservation.guestEmail)

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Datos de pago",
                color = HotelColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.cardMasked,
                    color = HotelColors.TextMuted,
                    fontSize = 13.sp
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = reservation.cardBrand,
                        color = HotelColors.Indigo,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = reservation.cardExpiry,
                        color = HotelColors.TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = HotelColors.DayMuted,
                    start = Offset(28f, size.height / 2f),
                    end = Offset(size.width - 28f, size.height / 2f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-12).dp)
                    .size(24.dp)
                    .background(HotelColors.Indigo, CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 12.dp)
                    .size(24.dp)
                    .background(HotelColors.Indigo, CircleShape)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookingBarcode(
                value = reservation.bookingCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            )
            Text(
                text = HotelDemo.footerNote,
                color = HotelColors.TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun GuestLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HotelColors.TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = HotelColors.TextPrimary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun BookingBarcode(value: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val patterns = buildList {
            add(code39.getValue('*'))
            value.filter { it in code39 }.forEach { add(code39.getValue(it)) }
            add(code39.getValue('*'))
        }
        var units = 0
        patterns.forEachIndexed { index, pattern ->
            pattern.forEach { units += if (it == 'w') 3 else 1 }
            if (index != patterns.lastIndex) units += 1
        }
        val quiet = 16
        val unit = size.width / (units + quiet * 2)
        var x = unit * quiet
        patterns.forEach { pattern ->
            var bar = true
            pattern.forEach { symbol ->
                val width = unit * (if (symbol == 'w') 3 else 1)
                if (bar) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height)
                    )
                }
                x += width
                bar = !bar
            }
            x += unit
        }
    }
}
