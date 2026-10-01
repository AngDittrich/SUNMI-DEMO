package com.example.kiosco.hotel

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HotelRoomOffer(
    val id: String,
    val name: String,
    val pricePerNight: Double,
    val imageAsset: String,
    val amenities: List<String>,
)

data class HotelProperty(
    val id: String,
    val name: String,
    val address: String,
    val rating: String,
    val imageAsset: String,
    val bookingCode: String,
    val rooms: List<HotelRoomOffer>,
    val reviews: List<HotelReview>,
    val services: List<HotelService>,
) {
    val fromPrice: Double get() = rooms.minOf { it.pricePerNight }
}

data class HotelReview(
    val author: String,
    val rating: String,
    val comment: String,
)

data class HotelService(
    val title: String,
    val detail: String,
)

data class HotelReservation(
    val id: String,
    val bookingCode: String,
    val hotelName: String,
    val hotelAddress: String,
    val guestName: String,
    val guestPhone: String,
    val guestEmail: String,
    val roomName: String,
    val nights: Int,
    val pricePerNight: Double,
    val checkInDate: Long,
    val checkOutDate: Long,
    val cardBrand: String,
    val cardMasked: String,
    val cardExpiry: String,
) {
    val total: Double get() = nights * pricePerNight
}

object HotelDemo {
    const val guestName = "Jorge García"
    const val guestPhone = "55 1234 5678"
    const val guestEmail = "jorge.garcia@email.com"
    const val cardBrand = "VISA"
    const val cardMasked = "**** **** **** 1234"
    const val cardExpiry = "09/27"
    const val footerNote = "Presenta este código en la recepción del hotel."

    private const val standardImage = "file:///android_asset/room_standard.jpg"
    private const val suiteImage = "file:///android_asset/room_suite.jpg"

    val hotels: List<HotelProperty> = listOf(
        HotelProperty(
            id = "aria",
            name = "The Aria Hotel",
            address = "Paseo de la Reforma 222, Ciudad de México",
            rating = "4.5",
            imageAsset = "file:///android_asset/hotel_lobby.jpg",
            bookingCode = "123967",
            rooms = listOf(
                HotelRoomOffer(
                    id = "standard",
                    name = "Habitación estándar",
                    pricePerNight = 180.0,
                    imageAsset = standardImage,
                    amenities = listOf("Cama queen", "Wi-Fi gratis", "Escritorio", "Baño privado"),
                ),
                HotelRoomOffer(
                    id = "suite",
                    name = "Suite junior",
                    pricePerNight = 340.0,
                    imageAsset = suiteImage,
                    amenities = listOf("Cama king", "Sala", "Desayuno incluido", "Tina"),
                ),
            ),
            reviews = listOf(
                HotelReview(
                    author = "Ana López",
                    rating = "5.0",
                    comment = "Habitación impecable y muy silenciosa. Volvería sin dudarlo.",
                ),
                HotelReview(
                    author = "Luis Martín",
                    rating = "4.0",
                    comment = "Buena ubicación y el desayuno está muy bien.",
                ),
            ),
            services = listOf(
                HotelService("Wi-Fi de cortesía", "En todo el hotel"),
                HotelService("Desayuno incluido", "7:00 a 11:00"),
                HotelService("Estacionamiento", "Sujeto a disponibilidad"),
                HotelService("Recepción 24 horas", "Check-in desde las 15:00"),
            ),
        ),
        HotelProperty(
            id = "bruma",
            name = "Hotel Bruma",
            address = "Av. Ámsterdam 48, Condesa",
            rating = "4.8",
            imageAsset = "file:///android_asset/hotel_bruma.jpg",
            bookingCode = "184220",
            rooms = listOf(
                HotelRoomOffer(
                    id = "standard",
                    name = "Habitación estándar",
                    pricePerNight = 150.0,
                    imageAsset = standardImage,
                    amenities = listOf("Cama queen", "Patio interior", "Wi-Fi gratis", "Baño privado"),
                ),
                HotelRoomOffer(
                    id = "suite",
                    name = "Suite con sala",
                    pricePerNight = 280.0,
                    imageAsset = suiteImage,
                    amenities = listOf("Cama king", "Sala", "Desayuno incluido", "Jacuzzi"),
                ),
            ),
            reviews = listOf(
                HotelReview(
                    author = "Ana López",
                    rating = "5.0",
                    comment = "El patio se siente muy tranquilo y la suite está impecable.",
                ),
                HotelReview(
                    author = "Luis Martín",
                    rating = "4.5",
                    comment = "Ideal para caminar la Condesa. El desayuno está muy bien.",
                ),
            ),
            services = listOf(
                HotelService("Patio interior", "Abierto todo el día"),
                HotelService("Desayuno incluido", "8:00 a 11:30"),
                HotelService("Wi-Fi de cortesía", "En habitaciones y patio"),
                HotelService("Bicicletas", "Para recorrer la colonia"),
            ),
        ),
        HotelProperty(
            id = "cenit",
            name = "Hotel Cénit",
            address = "Av. Santa Fe 505, Santa Fe",
            rating = "4.6",
            imageAsset = "file:///android_asset/hotel_cenit.jpg",
            bookingCode = "209441",
            rooms = listOf(
                HotelRoomOffer(
                    id = "standard",
                    name = "Habitación ejecutiva",
                    pricePerNight = 170.0,
                    imageAsset = standardImage,
                    amenities = listOf("Cama queen", "Escritorio", "Wi-Fi gratis", "Baño privado"),
                ),
                HotelRoomOffer(
                    id = "suite",
                    name = "Suite ejecutiva",
                    pricePerNight = 310.0,
                    imageAsset = suiteImage,
                    amenities = listOf("Cama king", "Sala de juntas", "Desayuno", "Vista a la ciudad"),
                ),
            ),
            reviews = listOf(
                HotelReview(
                    author = "Ana López",
                    rating = "4.5",
                    comment = "Muy cómoda para un viaje de trabajo y el escritorio ayuda.",
                ),
                HotelReview(
                    author = "Luis Martín",
                    rating = "4.5",
                    comment = "Llegué tarde y la recepción estaba abierta. Todo en orden.",
                ),
            ),
            services = listOf(
                HotelService("Centro de negocios", "Salas por hora"),
                HotelService("Wi-Fi de cortesía", "Alta velocidad"),
                HotelService("Estacionamiento", "Techado, sujeto a cupo"),
                HotelService("Recepción 24 horas", "Check-in desde las 15:00"),
            ),
        ),
    )

    fun findHotel(id: String): HotelProperty? = hotels.firstOrNull { it.id == id }

    fun findRoom(hotelId: String, roomId: String): HotelRoomOffer? =
        findHotel(hotelId)?.rooms?.firstOrNull { it.id == roomId }
}

object HotelRepository {
    val activeReservations = mutableStateListOf<HotelReservation>()

    fun createReservation(
        hotel: HotelProperty,
        room: HotelRoomOffer,
        checkIn: Long,
        checkOut: Long,
    ): HotelReservation {
        val checkInDate = startOfDay(checkIn)
        val checkOutDate = startOfDay(checkOut)
        val reservation = HotelReservation(
            id = "${hotel.id}-${room.id}-$checkInDate-$checkOutDate",
            bookingCode = hotel.bookingCode,
            hotelName = hotel.name,
            hotelAddress = hotel.address,
            guestName = HotelDemo.guestName,
            guestPhone = HotelDemo.guestPhone,
            guestEmail = HotelDemo.guestEmail,
            roomName = room.name,
            nights = nightsBetween(checkInDate, checkOutDate),
            pricePerNight = room.pricePerNight,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            cardBrand = HotelDemo.cardBrand,
            cardMasked = HotelDemo.cardMasked,
            cardExpiry = HotelDemo.cardExpiry,
        )
        val existing = activeReservations.indexOfFirst { it.id == reservation.id }
        if (existing >= 0) {
            activeReservations[existing] = reservation
        } else {
            activeReservations.add(reservation)
        }
        return reservation
    }

    fun findById(id: String): HotelReservation? =
        activeReservations.firstOrNull { it.id == id }
}

private val esMx: Locale = Locale.forLanguageTag("es-MX")

fun hotelToday(): Long = startOfDay(System.currentTimeMillis())

fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun addDays(millis: Long, days: Int): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = startOfDay(millis)
    cal.add(Calendar.DAY_OF_MONTH, days)
    return cal.timeInMillis
}

fun nightsBetween(checkIn: Long, checkOut: Long): Int {
    val cursor = Calendar.getInstance()
    cursor.timeInMillis = startOfDay(checkIn)
    val end = startOfDay(checkOut)
    var nights = 0
    while (cursor.timeInMillis < end) {
        cursor.add(Calendar.DAY_OF_MONTH, 1)
        nights++
    }
    return nights
}

fun monthStart(offsetFromThisMonth: Int): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = hotelToday()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.add(Calendar.MONTH, offsetFromThisMonth)
    return startOfDay(cal.timeInMillis)
}

fun formatStayDate(millis: Long): String {
    val raw = SimpleDateFormat("EEEE, d 'de' MMMM yyyy", esMx).format(Date(millis))
    return raw.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(esMx) else char.toString()
    }
}

fun formatMonthTitle(millis: Long): String {
    val raw = SimpleDateFormat("MMMM yyyy", esMx).format(Date(millis))
    return raw.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(esMx) else char.toString()
    }
}

fun formatShortDate(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", esMx).format(Date(millis))

fun formatHotelMoney(value: Double): String =
    if (value % 1.0 == 0.0) "$${value.toInt()}" else String.format(Locale.US, "$%.2f", value)

fun dayOfMonth(millis: Long): Int {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    return cal.get(Calendar.DAY_OF_MONTH)
}

data class CalendarDay(
    val millis: Long,
    val inMonth: Boolean,
)

fun monthGrid(monthMillis: Long): List<CalendarDay> {
    val month = Calendar.getInstance()
    month.timeInMillis = startOfDay(monthMillis)
    val monthIndex = month.get(Calendar.MONTH)
    val cursor = month.clone() as Calendar
    cursor.set(Calendar.DAY_OF_MONTH, 1)
    val mondayOffset = (cursor.get(Calendar.DAY_OF_WEEK) + 5) % 7
    cursor.add(Calendar.DAY_OF_MONTH, -mondayOffset)
    val cells = mutableListOf<CalendarDay>()
    repeat(42) {
        cells += CalendarDay(startOfDay(cursor.timeInMillis), cursor.get(Calendar.MONTH) == monthIndex)
        cursor.add(Calendar.DAY_OF_MONTH, 1)
    }
    while (cells.size > 28 && cells.takeLast(7).none { it.inMonth }) {
        repeat(7) { cells.removeAt(cells.lastIndex) }
    }
    return cells
}
