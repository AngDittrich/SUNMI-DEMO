package com.example.kiosco

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.SdkException
import com.sunmi.printerx.api.LineApi
import com.sunmi.printerx.api.PrintResult
import com.sunmi.printerx.enums.Align
import com.sunmi.printerx.enums.DividingLine
import com.sunmi.printerx.enums.Status
import com.sunmi.printerx.style.BaseStyle
import com.sunmi.printerx.style.QrStyle
import com.sunmi.printerx.style.TextStyle
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class SunmiPrinterManager(context: Context) {
    private val appContext = context.applicationContext
    private val printerSdk = PrinterSdk.getInstance()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val printExecutor = Executors.newSingleThreadExecutor()
    private val lock = Any()
    private val pendingPrinterRequests =
        mutableListOf<(Result<PrinterSdk.Printer>) -> Unit>()
    private val pendingRequestCompletions =
        mutableSetOf<(Result<Unit>) -> Unit>()
    private val activePrintTimeouts = mutableSetOf<Runnable>()
    private val activePrintCancellations = mutableSetOf<() -> Unit>()
    private val activePrintCallbacks = mutableSetOf<PrintResult>()

    private var printer: PrinterSdk.Printer? = null
    private var connecting = false
    private var released = false
    private var connectionAttempt = 0L
    private var connectionTimeout: Runnable? = null

    init {
        connect()
    }

    fun printPosReceipt(
        items: List<CartItem>,
        onResult: (Result<Unit>) -> Unit
    ) {
        enqueuePrint(onResult) {
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 1)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printText(
                "KIOSCO DE SNACKS",
                TextStyle.getStyle()
                    .setTextSize(20)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.DOTTED, 1)
            printDividingLine(DividingLine.EMPTY, 1)

            printText(
                "TICKET DE COMPRA",
                TextStyle.getStyle()
                    .setTextSize(28)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)

            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            printText(
                dateStr,
                TextStyle.getStyle()
                    .setTextSize(16)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 1)

            initLine(BaseStyle.getStyle().setAlign(Align.LEFT))
            items.forEach { item ->
                printTexts(
                    arrayOf(
                        item.product.name,
                        "${item.quantity}x ${money(item.product.price)}",
                        money(item.subtotal)
                    ),
                    intArrayOf(6, 3, 3),
                    arrayOf(
                        TextStyle.getStyle()
                            .setAlign(Align.LEFT)
                            .enableBold(true),
                        TextStyle.getStyle().setAlign(Align.CENTER),
                        TextStyle.getStyle().setAlign(Align.RIGHT)
                    )
                )
            }

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 1)

            initLine(BaseStyle.getStyle().setAlign(Align.RIGHT))
            printText(
                "TOTAL:",
                TextStyle.getStyle()
                    .setTextSize(26)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)

            printText(
                money(items.sumOf { it.subtotal }),
                TextStyle.getStyle()
                    .setTextSize(28)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 1)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printText(
                "¡Gracias por su compra!",
                TextStyle.getStyle()
                    .setTextSize(18)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)

            printText(
                "Vuelva pronto",
                TextStyle.getStyle()
                    .setTextSize(14)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 6)
        }
    }

    fun printSurveyCoupon(onResult: (Result<Unit>) -> Unit) {
        enqueuePrint(onResult) {
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 1)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printText(
                "CUPÓN DE ENCUESTA",
                TextStyle.getStyle()
                    .setTextSize(22)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.DOTTED, 1)
            printDividingLine(DividingLine.EMPTY, 1)

            printText(
                SURVEY_COUPON,
                TextStyle.getStyle()
                    .setTextSize(30)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 1)

            printQrCode(
                SURVEY_COUPON,
                QrStyle.getStyle()
                    .setDot(8)
                    .setAlign(Align.CENTER)
            )

            printDividingLine(DividingLine.EMPTY, 1)
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 6)
        }
    }

    fun release() {
        val printerCallbacks: List<(Result<PrinterSdk.Printer>) -> Unit>
        val requestCompletions: List<(Result<Unit>) -> Unit>
        val printTimeouts: List<Runnable>
        val printCancellations: List<() -> Unit>
        val acquisitionTimeout: Runnable?
        synchronized(lock) {
            if (released) return
            released = true
            connecting = false
            connectionAttempt += 1
            printer = null
            acquisitionTimeout = connectionTimeout
            connectionTimeout = null
            printerCallbacks = pendingPrinterRequests.toList()
            pendingPrinterRequests.clear()
            requestCompletions = pendingRequestCompletions.toList()
            pendingRequestCompletions.clear()
            printTimeouts = activePrintTimeouts.toList()
            activePrintTimeouts.clear()
            printCancellations = activePrintCancellations.toList()
            activePrintCancellations.clear()
            activePrintCallbacks.clear()
        }

        acquisitionTimeout?.let(mainHandler::removeCallbacks)
        printTimeouts.forEach(mainHandler::removeCallbacks)
        printCancellations.forEach { it() }
        printExecutor.shutdownNow()

        val printerFailure = Result.failure<PrinterSdk.Printer>(releasedError())
        printerCallbacks.forEach { it(printerFailure) }
        val requestFailure = Result.failure<Unit>(releasedError())
        requestCompletions.forEach { it(requestFailure) }
        // PrinterSdk is process-global; destroying it here can break a recreated Activity.
    }

    private fun enqueuePrint(
        onResult: (Result<Unit>) -> Unit,
        addContent: LineApi.() -> Unit
    ) {
        val complete = onceOnMain(onResult)
        val accepted = synchronized(lock) {
            if (released) {
                false
            } else {
                pendingRequestCompletions += complete
                true
            }
        }
        if (!accepted) {
            complete(Result.failure(releasedError()))
            return
        }

        withPrinter { printerResult ->
            val currentPrinter = printerResult.getOrElse {
                completeRequest(complete, Result.failure(it))
                return@withPrinter
            }
            try {
                printExecutor.execute {
                    val result = runTransaction(currentPrinter, addContent)
                    completeRequest(complete, result)
                }
            } catch (_: RejectedExecutionException) {
                completeRequest(complete, Result.failure(releasedError()))
            }
        }
    }

    private fun completeRequest(
        complete: (Result<Unit>) -> Unit,
        result: Result<Unit>
    ) {
        synchronized(lock) {
            pendingRequestCompletions.remove(complete)
        }
        complete(
            result.fold(
                onSuccess = { Result.success(Unit) },
                onFailure = { Result.failure(userFacingFailure(it)) }
            )
        )
    }

    private fun connect() {
        val attempt: Long
        synchronized(lock) {
            if (released || connecting || printer != null) return
            connecting = true
            connectionAttempt += 1
            attempt = connectionAttempt
        }

        scheduleConnectionTimeout(attempt)
        val managerReference = WeakReference(this)
        try {
            printerSdk.getPrinter(
                appContext,
                object : PrinterSdk.PrinterListen {
                    override fun onDefPrinter(defaultPrinter: PrinterSdk.Printer?) {
                        managerReference.get()?.finishConnection(
                            attempt,
                            defaultPrinter?.let { Result.success(it) }
                                ?: Result.failure(
                                    IllegalStateException(
                                        "No hay una impresora SUNMI predeterminada."
                                    )
                                )
                        )
                    }

                    override fun onPrinters(
                        printers: MutableList<PrinterSdk.Printer>?
                    ) = Unit
                }
            )
        } catch (error: Throwable) {
            finishConnection(attempt, Result.failure(error))
        }
    }

    private fun scheduleConnectionTimeout(attempt: Long) {
        val timeout = Runnable {
            timeoutConnectionWait(attempt)
        }
        synchronized(lock) {
            if (
                released ||
                !connecting ||
                attempt != connectionAttempt ||
                connectionTimeout != null
            ) {
                return
            }
            connectionTimeout = timeout
            mainHandler.postDelayed(timeout, PRINTER_ACQUISITION_TIMEOUT_MS)
        }
    }

    private fun timeoutConnectionWait(attempt: Long) {
        val callbacks: List<(Result<PrinterSdk.Printer>) -> Unit>
        synchronized(lock) {
            if (released || !connecting || attempt != connectionAttempt) return
            connecting = false
            connectionAttempt += 1
            connectionTimeout = null
            callbacks = pendingPrinterRequests.toList()
            pendingPrinterRequests.clear()
        }

        val failure = Result.failure<PrinterSdk.Printer>(
            IllegalStateException("No se pudo obtener la impresora SUNMI.")
        )
        callbacks.forEach { it(failure) }
    }

    private fun finishConnection(
        attempt: Long,
        result: Result<PrinterSdk.Printer>
    ) {
        val callbacks: List<(Result<PrinterSdk.Printer>) -> Unit>
        val timeout: Runnable?
        val callbackResult: Result<PrinterSdk.Printer>
        synchronized(lock) {
            if (released) return
            val validPrinter = result.getOrNull()
            if (validPrinter != null) {
                printer = validPrinter
                callbackResult = Result.success(validPrinter)
                if (connecting) {
                    connecting = false
                    connectionAttempt += 1
                    timeout = connectionTimeout
                    connectionTimeout = null
                } else {
                    timeout = null
                }
            } else {
                if (!connecting || attempt != connectionAttempt) return
                callbackResult = result
                connecting = false
                timeout = connectionTimeout
                connectionTimeout = null
            }
            callbacks = pendingPrinterRequests.toList()
            pendingPrinterRequests.clear()
        }

        timeout?.let(mainHandler::removeCallbacks)
        callbacks.forEach { it(callbackResult) }
    }

    private fun withPrinter(
        callback: (Result<PrinterSdk.Printer>) -> Unit
    ) {
        val currentPrinter: PrinterSdk.Printer?
        val failure: Throwable?
        var shouldConnect = false
        var timeoutAttempt: Long? = null
        val cachedCallbacks: List<(Result<PrinterSdk.Printer>) -> Unit>
        synchronized(lock) {
            currentPrinter = printer
            failure = if (released) {
                IllegalStateException("El administrador de impresión fue liberado.")
            } else {
                null
            }
            if (currentPrinter == null && failure == null) {
                pendingPrinterRequests += callback
                shouldConnect = !connecting
                if (connecting && connectionTimeout == null) {
                    timeoutAttempt = connectionAttempt
                }
                cachedCallbacks = emptyList()
            } else if (currentPrinter != null) {
                cachedCallbacks = pendingPrinterRequests.toList() + callback
                pendingPrinterRequests.clear()
            } else {
                cachedCallbacks = emptyList()
            }
        }

        val attemptToTimeout = timeoutAttempt
        when {
            failure != null -> callback(Result.failure(failure))
            currentPrinter != null -> {
                val success = Result.success(currentPrinter)
                cachedCallbacks.forEach { it(success) }
            }
            shouldConnect -> connect()
            attemptToTimeout != null -> scheduleConnectionTimeout(attemptToTimeout)
        }
    }

    private fun runTransaction(
        currentPrinter: PrinterSdk.Printer,
        addContent: LineApi.() -> Unit
    ): Result<Unit> {
        synchronized(lock) {
            if (released) return Result.failure(releasedError())
        }

        printerStatusFailure(currentPrinter)?.let {
            invalidatePrinter(currentPrinter)
            return Result.failure(it)
        }

        val terminalResult = AtomicReference<Result<Unit>?>(null)
        val terminalSignal = CountDownLatch(1)
        val transactionGate = Any()
        val transactionPhase = AtomicReference(TransactionPhase.PRE_SUBMISSION)
        val timeout = Runnable {
            synchronized(transactionGate) {
                val outputMayHaveStarted =
                    transactionPhase.get() != TransactionPhase.PRE_SUBMISSION
                val timeoutFailure = TicketPrintException(
                    message = if (outputMayHaveStarted) {
                        "No se confirmó el resultado de impresión. Continúe sin reimprimir."
                    } else {
                        "La preparación de la impresión agotó el tiempo de espera."
                    },
                    retryable = !outputMayHaveStarted
                )
                if (
                    terminalResult.compareAndSet(
                        null,
                        Result.failure(timeoutFailure)
                    )
                ) {
                    terminalSignal.countDown()
                }
            }
        }
        val cancellation: () -> Unit = {
            if (
                terminalResult.compareAndSet(
                    null,
                    Result.failure(releasedError())
                )
            ) {
                terminalSignal.countDown()
            }
        }
        val printCallback = object : PrintResult() {
            override fun onResult(resultCode: Int, message: String?) {
                val result = if (resultCode == 0) {
                    Result.success(Unit)
                } else {
                    Result.failure(
                        TicketPrintException(
                            message = message
                                ?.takeIf { it.isNotBlank() }
                                ?.let { "La impresora SUNMI reportó: $it" }
                                ?: "La impresora SUNMI devolvió el error $resultCode.",
                            retryable = true
                        )
                    )
                }
                if (terminalResult.compareAndSet(null, result)) {
                    terminalSignal.countDown()
                }
            }
        }

        synchronized(lock) {
            if (released) return Result.failure(releasedError())
            activePrintTimeouts += timeout
            activePrintCancellations += cancellation
            activePrintCallbacks += printCallback
            mainHandler.postDelayed(timeout, PRINT_RESULT_TIMEOUT_MS)
        }

        var lineApi: LineApi? = null
        var submissionAttempted = false
        var cleanupFailure: Throwable? = null
        try {
            val currentLineApi = currentPrinter.lineApi()
            lineApi = currentLineApi
            currentLineApi.enableTransMode(true)
            val shouldBufferContent = synchronized(transactionGate) {
                if (terminalResult.get() != null) {
                    false
                } else {
                    transactionPhase.set(TransactionPhase.CONTENT_BUFFERING_STARTED)
                    true
                }
            }
            if (shouldBufferContent) {
                currentLineApi.addContent()
                currentLineApi.autoOut()
            }
            val shouldSubmit = synchronized(transactionGate) {
                if (terminalResult.get() != null) {
                    false
                } else {
                    transactionPhase.set(TransactionPhase.SUBMISSION_STARTED)
                    submissionAttempted = true
                    true
                }
            }
            if (shouldSubmit) {
                currentLineApi.printTrans(printCallback)
            }
        } catch (error: Throwable) {
            val failure = userFacingFailure(
                error,
                retryable =
                    transactionPhase.get() == TransactionPhase.PRE_SUBMISSION
            )
            if (
                terminalResult.compareAndSet(
                    null,
                    Result.failure(failure)
                )
            ) {
                terminalSignal.countDown()
            }
        }

        try {
            terminalSignal.await(
                PRINT_RESULT_TIMEOUT_MS + TIMEOUT_SIGNAL_GRACE_MS,
                TimeUnit.MILLISECONDS
            )
        } catch (_: InterruptedException) {
            terminalResult.compareAndSet(
                null,
                Result.failure(releasedError())
            )
            Thread.currentThread().interrupt()
        } finally {
            // External PrinterX paths can flush again when transaction mode is
            // disabled, so cleanup is safe only before content starts buffering.
            if (
                !submissionAttempted &&
                transactionPhase.get() == TransactionPhase.PRE_SUBMISSION
            ) {
                cleanupFailure = runCatching {
                    lineApi?.enableTransMode(false)
                }.exceptionOrNull()
            }
            mainHandler.removeCallbacks(timeout)
            synchronized(lock) {
                activePrintTimeouts.remove(timeout)
                activePrintCancellations.remove(cancellation)
                activePrintCallbacks.remove(printCallback)
            }
        }

        val outputMayHaveStarted =
            transactionPhase.get() != TransactionPhase.PRE_SUBMISSION
        val result = terminalResult.get()
            ?: Result.failure(
                TicketPrintException(
                    message = if (outputMayHaveStarted) {
                        "No se confirmó el resultado de impresión. Continúe sin reimprimir."
                    } else {
                        "La preparación de la impresión agotó el tiempo de espera."
                    },
                    retryable = !outputMayHaveStarted
                )
            )
        if (cleanupFailure != null) {
            Log.w(
                TAG,
                "PrinterX no pudo limpiar el modo transacción antes del contenido.",
                cleanupFailure
            )
            invalidatePrinter(currentPrinter)
        }
        if (result.isFailure) {
            invalidatePrinter(currentPrinter)
        }
        return result
    }

    private fun invalidatePrinter(failedPrinter: PrinterSdk.Printer) {
        synchronized(lock) {
            if (printer === failedPrinter) {
                printer = null
            }
        }
    }

    private fun printerStatusFailure(
        currentPrinter: PrinterSdk.Printer
    ): Throwable? = try {
        when (val status = currentPrinter.queryApi().getStatus()) {
            Status.READY -> null
            Status.OFFLINE, Status.COMM ->
                TicketPrintException(
                    "La impresora SUNMI está desconectada.",
                    retryable = true
                )
            Status.ERR_PAPER_OUT ->
                TicketPrintException(
                    "La impresora SUNMI no tiene papel.",
                    retryable = true
                )
            Status.ERR_COVER, Status.ERR_COVER_INCOMPLETE ->
                TicketPrintException(
                    "La tapa de la impresora SUNMI está abierta.",
                    retryable = true
                )
            else -> {
                if (status.name.startsWith("ERR_")) {
                    TicketPrintException(
                        "La impresora SUNMI reportó el estado ${status.name}.",
                        retryable = true
                    )
                } else {
                    null
                }
            }
        }
    } catch (_: Throwable) {
        // Unsupported or unavailable QueryApi is indeterminate, not affirmative failure.
        null
    }

    private fun userFacingFailure(
        error: Throwable,
        retryable: Boolean? = null
    ): TicketPrintException {
        if (error is TicketPrintException && retryable == null) return error

        val canRetry = retryable ?: true
        val detail = error.message?.takeIf { it.isNotBlank() }
        val message = when (error) {
            is SdkException -> detail
                ?.let { "Error de PrinterX: $it" }
                ?: "PrinterX no pudo completar la impresión."
            else -> detail ?: "No se pudo completar la impresión SUNMI."
        }
        return TicketPrintException(message, canRetry, error)
    }

    private fun onceOnMain(
        callback: (Result<Unit>) -> Unit
    ): (Result<Unit>) -> Unit {
        val completed = AtomicBoolean(false)
        return { result ->
            if (completed.compareAndSet(false, true)) {
                mainHandler.post { callback(result) }
            }
        }
    }

    private fun releasedError(): TicketPrintException =
        TicketPrintException(
            "El administrador de impresión fue liberado.",
            retryable = false
        )

    private fun money(value: Double): String =
        String.format(Locale.US, "$%.2f", value)

    private companion object {
        const val TAG = "SunmiPrinterManager"
        const val PRINTER_ACQUISITION_TIMEOUT_MS = 5_000L
        const val PRINT_RESULT_TIMEOUT_MS = 15_000L
        const val TIMEOUT_SIGNAL_GRACE_MS = 1_000L
    }

    private enum class TransactionPhase {
        PRE_SUBMISSION,
        CONTENT_BUFFERING_STARTED,
        SUBMISSION_STARTED
    }
}
