package com.example.kiosco.hotel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val weekdays = listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")

@Composable
fun HotelDatesScreen(
    hotel: HotelProperty,
    room: HotelRoomOffer,
    onReserve: (HotelReservation) -> Unit,
    onBack: () -> Unit,
) {
    var checkIn by rememberSaveable { mutableLongStateOf(hotelToday()) }
    var checkOut by rememberSaveable { mutableLongStateOf(addDays(hotelToday(), 4)) }
    val nights = nightsBetween(checkIn, checkOut)
    val total = nights * room.pricePerNight

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
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Definir periodo de estancia",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${hotel.name} · ${room.name}",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
        }

        StaySteps()

        StaySummary(
            checkIn = checkIn,
            checkOut = checkOut,
            nights = nights,
            total = total
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Definir periodo de estancia",
                    color = HotelColors.Indigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
                MonthCalendar(
                    monthMillis = monthStart(0),
                    checkIn = checkIn,
                    checkOut = checkOut,
                    onDayClick = { day ->
                        val next = nextRange(checkIn, checkOut, day)
                        checkIn = next.first
                        checkOut = next.second
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
                MonthCalendar(
                    monthMillis = monthStart(1),
                    checkIn = checkIn,
                    checkOut = checkOut,
                    onDayClick = { day ->
                        val next = nextRange(checkIn, checkOut, day)
                        checkIn = next.first
                        checkOut = next.second
                    }
                )
            }

            Button(
                onClick = {
                    onReserve(HotelRepository.createReservation(hotel, room, checkIn, checkOut))
                },
                enabled = nights >= 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HotelColors.Indigo,
                    contentColor = Color.White,
                    disabledContainerColor = HotelColors.Indigo.copy(alpha = 0.35f),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Reservar unidad",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

private fun nextRange(checkIn: Long, checkOut: Long, day: Long): Pair<Long, Long> {
    val selected = startOfDay(day)
    val rangeOpen = nightsBetween(checkIn, checkOut) == 0
    return when {
        !rangeOpen -> selected to selected
        selected < checkIn -> selected to checkIn
        else -> checkIn to selected
    }
}

@Composable
private fun StaySteps() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        StayStep(Icons.Filled.CreditCard, "Pago", active = false, modifier = Modifier.weight(1f))
        StepConnector(Modifier.padding(top = 16.dp))
        StayStep(Icons.Filled.Person, "Datos del huésped", active = false, modifier = Modifier.weight(1.3f))
        StepConnector(Modifier.padding(top = 16.dp))
        StayStep(Icons.Filled.CalendarMonth, "Duración", active = true, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StayStep(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (active) Color.White.copy(alpha = 0.18f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = if (active) 1f else 0.45f),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            color = Color.White.copy(alpha = if (active) 1f else 0.55f),
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun RowScope.StepConnector(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .weight(0.45f)
            .height(2.dp)
    ) {
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
        )
    }
}

@Composable
private fun StaySummary(
    checkIn: Long,
    checkOut: Long,
    nights: Int,
    total: Double,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SummaryColumn(
            label = "Inicio de estancia",
            value = formatStayDate(checkIn),
            modifier = Modifier.weight(1.2f)
        )
        Column(
            modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (nights == 1) "1 noche" else "$nights noches",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        SummaryColumn(
            label = "Fin de estancia",
            value = if (nights > 0) formatStayDate(checkOut) else "—",
            modifier = Modifier.weight(1.2f),
            alignEnd = true
        )
        SummaryColumn(
            label = "Costo",
            value = formatHotelMoney(total),
            modifier = Modifier.weight(0.7f),
            alignEnd = true,
            valueColor = HotelColors.Yellow
        )
    }
}

@Composable
private fun SummaryColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
    valueColor: Color = Color.White,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 11.sp,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun MonthCalendar(
    monthMillis: Long,
    checkIn: Long,
    checkOut: Long,
    onDayClick: (Long) -> Unit,
) {
    val cells = monthGrid(monthMillis)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = formatMonthTitle(monthMillis),
            color = HotelColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.End
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    color = HotelColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        cells.chunked(7).forEach { week ->
            WeekRow(
                week = week,
                checkIn = checkIn,
                checkOut = checkOut,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun WeekRow(
    week: List<CalendarDay>,
    checkIn: Long,
    checkOut: Long,
    onDayClick: (Long) -> Unit,
) {
    val rangeStart = minOf(checkIn, checkOut)
    val rangeEnd = maxOf(checkIn, checkOut)
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val first = week.indexOfFirst { it.millis in rangeStart..rangeEnd }
            val last = week.indexOfLast { it.millis in rangeStart..rangeEnd }
            if (first >= 0 && last >= first) {
                val cell = size.width / 7f
                val top = size.height * 0.12f
                val height = size.height * 0.76f
                drawRoundRect(
                    color = HotelColors.RangeFill,
                    topLeft = Offset(cell * first, top),
                    size = Size(cell * (last - first + 1), height),
                    cornerRadius = CornerRadius(height / 2f, height / 2f)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            week.forEach { day ->
                val endpoint = day.millis == checkIn || (nightsBetween(checkIn, checkOut) > 0 && day.millis == checkOut)
                val inRange = day.millis in rangeStart..rangeEnd
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable { onDayClick(day.millis) },
                    contentAlignment = Alignment.Center
                ) {
                    if (endpoint) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(HotelColors.Indigo)
                        )
                    }
                    Text(
                        text = dayOfMonth(day.millis).toString(),
                        color = when {
                            endpoint -> Color.White
                            !day.inMonth -> HotelColors.DayMuted
                            else -> HotelColors.TextPrimary
                        },
                        fontWeight = if (inRange) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
