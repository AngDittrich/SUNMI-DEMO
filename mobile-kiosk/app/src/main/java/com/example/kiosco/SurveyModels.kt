package com.example.kiosco

enum class WelcomeService {
    POS,
    SURVEY
}

data class SurveyResponse(
    val overallRating: Int,
    val serviceRating: String,
    val comment: String
)

const val SURVEY_COUPON = "SYSCOM-SUNMI"
const val SURVEY_QR_ASSET = "file:///android_asset/brand/syscom-sunmi-qr.png"
