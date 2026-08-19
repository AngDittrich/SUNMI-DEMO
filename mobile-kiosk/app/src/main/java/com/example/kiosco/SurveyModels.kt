package com.example.kiosco

enum class WelcomeService {
    POS,
    SURVEY
}

data class SurveyResponse(
    val overallRating: Int,
    val syscomRating: String,
    val sunmiRating: String,
    val recommendUs: Boolean,
    val highlights: List<String>,
    val comment: String
)

const val SURVEY_QR_ASSET = "file:///android_asset/brand/syscom-sunmi-qr.png"

val highlightOptions = listOf(
    "Atención al cliente",
    "Rapidez del servicio",
    "Calidad del producto",
    "Facilidad de uso",
    "Precio",
    "Instalación del equipo"
)
