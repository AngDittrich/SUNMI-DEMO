package com.example.kiosco.hotel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private enum class HotelTab(val label: String) {
    Reserva("Reserva"),
    Resenas("Reseñas"),
    Servicios("Servicios"),
}

@Composable
fun HotelDetailScreen(
    hotel: HotelProperty,
    onSelectRoom: (String) -> Unit,
    onBack: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val heroHeight = (LocalConfiguration.current.screenHeightDp * 0.34f).dp
    val tabs = HotelTab.entries

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HotelColors.Page)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                AsyncImage(
                    model = hotel.imageAsset,
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .pullUp(28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = hotel.name,
                            color = HotelColors.TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HotelColors.Yellow)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = hotel.rating,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "Desde ${formatHotelMoney(hotel.fromPrice)}",
                        color = HotelColors.Yellow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = HotelColors.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = hotel.address,
                        color = HotelColors.TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(HotelColors.Page)
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) HotelColors.Indigo else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.label,
                                color = if (selected) Color.White else HotelColors.TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (tabs[selectedTab]) {
                    HotelTab.Reserva -> {
                        hotel.rooms.forEachIndexed { index, room ->
                            if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                            RoomOfferCard(room = room, onSelectStay = { onSelectRoom(room.id) })
                        }
                    }
                    HotelTab.Resenas -> ReviewsPane(hotel.reviews)
                    HotelTab.Servicios -> ServicesPane(hotel.services)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }
    }
}

private fun Modifier.pullUp(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    layout(placeable.width, (placeable.height - overlapPx).coerceAtLeast(0)) {
        placeable.placeRelative(0, -overlapPx)
    }
}

@Composable
private fun RoomOfferCard(room: HotelRoomOffer, onSelectStay: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, HotelColors.CardBorder, RoundedCornerShape(18.dp))
            .background(Color.White)
    ) {
        AsyncImage(
            model = room.imageAsset,
            contentDescription = room.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = room.name,
                    color = HotelColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${formatHotelMoney(room.pricePerNight)} / noche",
                    color = HotelColors.Yellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            room.amenities.chunked(2).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { amenity ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = HotelColors.PaidGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = amenity,
                                color = HotelColors.TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
            Button(
                onClick = onSelectStay,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HotelColors.Indigo,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Definir periodo de estancia",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ReviewsPane(reviews: List<HotelReview>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        reviews.forEach { review ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HotelColors.Page)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = review.author,
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = HotelColors.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = review.rating,
                            color = HotelColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                Text(
                    text = review.comment,
                    color = HotelColors.TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ServicesPane(services: List<HotelService>) {
    val icons = listOf(
        Icons.Filled.Wifi,
        Icons.Filled.RoomService,
        Icons.Filled.LocalParking,
        Icons.Filled.Wifi,
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        services.forEachIndexed { index, service ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HotelColors.Page)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HotelColors.Indigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icons[index % icons.size],
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = service.title,
                        color = HotelColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = service.detail,
                        color = HotelColors.TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
