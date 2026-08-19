package com.example.kiosco

const val SURVEY_COUPON = "SYSCOM-SUNMI"

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
    data class Failed(
        val message: String,
        val retryable: Boolean = true,
        val submittedUnconfirmed: Boolean = false
    ) : TicketPrintState
}
