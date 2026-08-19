package com.example.kiosco

const val SURVEY_COUPON = "SYSCOM-SUNMI"
const val SYSCOM_LOGO_ASSET = "brand/syscom-large-logo.png"
const val SUNMI_LOGO_ASSET = "brand/sunmi.webp"

class TicketPrintException(
    message: String,
    val retryable: Boolean,
    val submittedUnconfirmed: Boolean = false,
    cause: Throwable? = null
) : Exception(message, cause)

sealed interface TicketPrintState {
    data object Idle : TicketPrintState
    data object Printing : TicketPrintState
    data object Printed : TicketPrintState
    data class Submitted(val message: String) : TicketPrintState
    data class Failed(
        val message: String,
        val retryable: Boolean = true,
        val submittedUnconfirmed: Boolean = false
    ) : TicketPrintState
}
