package com.example.kiosco

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
import com.sunmi.printerx.style.BitmapStyle
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
        enqueuePrint(onResult) { logoStrip ->
            printDividingLine(DividingLine.EMPTY, 12)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printTicketLogos(logoStrip)

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.DOTTED, 2)
            printDividingLine(DividingLine.EMPTY, 10)

            printText(
                "TICKET DE COMPRA",
                TextStyle.getStyle()
                    .setTextSize(26)
                    .enableBold(true)
            )

            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            printText(
                dateStr,
                TextStyle.getStyle()
                    .setTextSize(16)
            )

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 10)

            initLine(BaseStyle.getStyle().setAlign(Align.LEFT))
            printTexts(
                arrayOf("PRODUCTO", "CANT.", "TOTAL"),
                intArrayOf(6, 3, 3),
                arrayOf(
                    TextStyle.getStyle()
                        .setAlign(Align.LEFT)
                        .enableBold(true)
                        .setTextSize(14),
                    TextStyle.getStyle()
                        .setAlign(Align.CENTER)
                        .enableBold(true)
                        .setTextSize(14),
                    TextStyle.getStyle()
                        .setAlign(Align.RIGHT)
                        .enableBold(true)
                        .setTextSize(14)
                )
            )

            printDividingLine(DividingLine.EMPTY, 6)

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

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 10)

            initLine(BaseStyle.getStyle().setAlign(Align.RIGHT))
            printText(
                "TOTAL:",
                TextStyle.getStyle()
                    .setTextSize(24)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 8)

            printText(
                money(items.sumOf { it.subtotal }),
                TextStyle.getStyle()
                    .setTextSize(30)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.SOLID, 2)
            printDividingLine(DividingLine.EMPTY, 10)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printText(
                "¡Gracias por su compra!",
                TextStyle.getStyle()
                    .setTextSize(18)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 10)

            printText(
                "Vuelva pronto",
                TextStyle.getStyle()
                    .setTextSize(14)
            )

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 30)
        }
    }

    fun printSurveyCoupon(onResult: (Result<Unit>) -> Unit) {
        enqueuePrint(onResult) { logoStrip ->
            printDividingLine(DividingLine.EMPTY, 12)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printTicketLogos(logoStrip)

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.DOTTED, 2)
            printDividingLine(DividingLine.EMPTY, 10)

            printText(
                "CUPÓN DE ENCUESTA",
                TextStyle.getStyle()
                    .setTextSize(22)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 10)

            printText(
                "Escanea y califica tu experiencia",
                TextStyle.getStyle()
                    .setTextSize(14)
            )

            printDividingLine(DividingLine.EMPTY, 14)

            printQrCode(
                SURVEY_COUPON,
                QrStyle.getStyle()
                    .setDot(8)
                    .setAlign(Align.CENTER)
            )

            printDividingLine(DividingLine.EMPTY, 10)

            initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
            printText(
                SURVEY_COUPON,
                TextStyle.getStyle()
                    .setTextSize(18)
                    .enableBold(true)
            )

            printDividingLine(DividingLine.EMPTY, 10)
            printDividingLine(DividingLine.SOLID, 2)

            printDividingLine(DividingLine.EMPTY, 30)
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
        addContent: LineApi.(Bitmap) -> Unit
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
                    val logoStrip = runCatching { loadTicketLogoStrip() }.getOrElse {
                        completeRequest(
                            complete,
                            Result.failure(
                                userFacingFailure(it, retryable = true)
                            )
                        )
                        return@execute
                    }
                    val result = try {
                        runTransaction(currentPrinter) {
                            addContent(logoStrip)
                        }
                    } finally {
                        logoStrip.recycle()
                    }
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
        val failureReconciliation = AtomicReference<Runnable?>(null)
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
                    retryable = !outputMayHaveStarted,
                    submittedUnconfirmed = outputMayHaveStarted
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
                if (resultCode == PRINT_RESULT_SUCCESS) {
                    if (
                        terminalResult.compareAndSet(
                            null,
                            Result.success(Unit)
                        )
                    ) {
                        failureReconciliation.get()?.let(mainHandler::removeCallbacks)
                        terminalSignal.countDown()
                    }
                    return
                }

                val sdkFailure = TicketPrintException(
                    message = message
                        ?.takeIf { it.isNotBlank() }
                        ?.let { "La impresora SUNMI reportó: $it" }
                        ?: "La impresora SUNMI devolvió el error $resultCode.",
                    retryable = true
                )
                val reconciliation = Runnable {
                    if (terminalResult.get() != null) return@Runnable

                    // PrinterX can report its documented failure after the
                    // transaction has already produced paper. A current device
                    // error remains definitive; READY/unknown cannot prove
                    // whether output occurred, so it must not invite a duplicate.
                    val currentStatusFailure = printerStatusFailure(currentPrinter)
                    val reconciledFailure = currentStatusFailure?.let {
                        userFacingFailure(it, retryable = false)
                    } ?: submittedUnconfirmedFailure(sdkFailure)
                    if (
                        terminalResult.compareAndSet(
                            null,
                            Result.failure(reconciledFailure)
                        )
                    ) {
                        terminalSignal.countDown()
                    }
                }
                if (
                    failureReconciliation.compareAndSet(
                        null,
                        reconciliation
                    )
                ) {
                    mainHandler.postDelayed(
                        reconciliation,
                        FAILURE_RECONCILIATION_DELAY_MS
                    )
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
            val failure = if (
                transactionPhase.get() == TransactionPhase.PRE_SUBMISSION
            ) {
                userFacingFailure(error, retryable = true)
            } else {
                submittedUnconfirmedFailure(error)
            }
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
            failureReconciliation.get()?.let(mainHandler::removeCallbacks)
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
                    retryable = !outputMayHaveStarted,
                    submittedUnconfirmed = outputMayHaveStarted
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

    private fun loadTicketLogoStrip(): Bitmap {
        var syscom: Bitmap? = null
        var sunmi: Bitmap? = null
        return try {
            val loadedSyscom = loadAssetBitmap(SYSCOM_LOGO_ASSET)
            syscom = loadedSyscom
            val loadedSunmi = loadAssetBitmap(SUNMI_LOGO_ASSET)
            sunmi = loadedSunmi
            combineTicketLogos(
                syscom = loadedSyscom,
                sunmi = loadedSunmi
            )
        } catch (error: Throwable) {
            throw TicketPrintException(
                message = "No se pudieron cargar los logotipos del ticket.",
                retryable = true,
                cause = error
            )
        } finally {
            syscom?.recycle()
            sunmi?.recycle()
        }
    }

    private fun loadAssetBitmap(assetPath: String): Bitmap {
        val source = appContext.assets.open(assetPath).use(BitmapFactory::decodeStream)
            ?: throw IllegalStateException(
                "No se pudo cargar el logotipo de impresión $assetPath."
            )
        return try {
            trimTransparentPaddingAndFlatten(source)
        } finally {
            source.recycle()
        }
    }

    private fun trimTransparentPaddingAndFlatten(source: Bitmap): Bitmap {
        val pixels = IntArray(source.width * source.height)
        source.getPixels(
            pixels,
            0,
            source.width,
            0,
            0,
            source.width,
            source.height
        )

        var left = source.width
        var top = source.height
        var right = -1
        var bottom = -1
        pixels.forEachIndexed { index, pixel ->
            if (Color.alpha(pixel) != 0) {
                val x = index % source.width
                val y = index / source.width
                left = minOf(left, x)
                top = minOf(top, y)
                right = maxOf(right, x)
                bottom = maxOf(bottom, y)
            }
        }

        if (right < left || bottom < top) {
            return flattenOnWhite(source)
        }

        val cropped = Bitmap.createBitmap(
            source,
            left,
            top,
            right - left + 1,
            bottom - top + 1
        )
        return try {
            flattenOnWhite(cropped)
        } finally {
            if (cropped !== source) {
                cropped.recycle()
            }
        }
    }

    private fun flattenOnWhite(source: Bitmap): Bitmap {
        val flattened = Bitmap.createBitmap(
            source.width,
            source.height,
            Bitmap.Config.ARGB_8888
        )
        return try {
            val canvas = Canvas(flattened)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(
                source,
                0f,
                0f,
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
            flattened.setHasAlpha(false)
            flattened
        } catch (error: Throwable) {
            flattened.recycle()
            throw error
        }
    }

    private fun combineTicketLogos(
        syscom: Bitmap,
        sunmi: Bitmap
    ): Bitmap {
        val availableLogoWidth =
            TICKET_LOGO_STRIP_WIDTH_DOTS -
                (TICKET_LOGO_HORIZONTAL_PADDING_DOTS * 2) -
                TICKET_LOGO_GAP_DOTS
        val combinedAspectRatio =
            syscom.width.toFloat() / syscom.height +
                sunmi.width.toFloat() / sunmi.height
        val commonLogoHeight = minOf(
            TICKET_LOGO_MAX_HEIGHT_DOTS,
            (availableLogoWidth / combinedAspectRatio).toInt()
        ).coerceAtLeast(1)
        val syscomWidth = syscom.width * commonLogoHeight / syscom.height
        val sunmiWidth = sunmi.width * commonLogoHeight / sunmi.height
        val combinedLogoWidth = syscomWidth + TICKET_LOGO_GAP_DOTS + sunmiWidth
        val groupLeft = (TICKET_LOGO_STRIP_WIDTH_DOTS - combinedLogoWidth) / 2f
        val stripHeight =
            commonLogoHeight + (TICKET_LOGO_VERTICAL_PADDING_DOTS * 2)
        val strip = Bitmap.createBitmap(
            TICKET_LOGO_STRIP_WIDTH_DOTS,
            stripHeight,
            Bitmap.Config.ARGB_8888
        )

        return try {
            val canvas = Canvas(strip)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawColor(Color.WHITE)

            fun drawLogo(source: Bitmap, left: Float, scaledWidth: Int) {
                canvas.drawBitmap(
                    source,
                    null,
                    RectF(
                        left,
                        TICKET_LOGO_VERTICAL_PADDING_DOTS.toFloat(),
                        left + scaledWidth,
                        (TICKET_LOGO_VERTICAL_PADDING_DOTS + commonLogoHeight)
                            .toFloat()
                    ),
                    paint
                )
            }

            drawLogo(syscom, left = groupLeft, scaledWidth = syscomWidth)
            drawLogo(
                sunmi,
                left = groupLeft + syscomWidth + TICKET_LOGO_GAP_DOTS,
                scaledWidth = sunmiWidth
            )
            strip.setHasAlpha(false)
            strip
        } catch (error: Throwable) {
            strip.recycle()
            throw error
        }
    }

    private fun LineApi.printTicketLogos(logoStrip: Bitmap) {
        initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
        printBitmap(
            logoStrip,
            BitmapStyle.getStyle()
                .setAlign(Align.CENTER)
                .setWidth(TICKET_LOGO_STRIP_WIDTH_DOTS)
        )
        printDividingLine(DividingLine.EMPTY, 12)
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
        return TicketPrintException(
            message = message,
            retryable = canRetry,
            cause = error
        )
    }

    private fun submittedUnconfirmedFailure(
        error: Throwable
    ): TicketPrintException {
        val detail = error.message?.takeIf { it.isNotBlank() }
        return TicketPrintException(
            message = buildString {
                append("El ticket fue enviado a la impresora, pero PrinterX no confirmó el resultado")
                if (detail != null) append(": $detail")
                append(". Evite imprimirlo de nuevo.")
            },
            retryable = false,
            submittedUnconfirmed = true,
            cause = error
        )
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
        const val FAILURE_RECONCILIATION_DELAY_MS = 1_000L
        const val PRINT_RESULT_SUCCESS = 0
        const val TICKET_LOGO_STRIP_WIDTH_DOTS = 576
        const val TICKET_LOGO_MAX_HEIGHT_DOTS = 192
        const val TICKET_LOGO_HORIZONTAL_PADDING_DOTS = 4
        const val TICKET_LOGO_GAP_DOTS = 8
        const val TICKET_LOGO_VERTICAL_PADDING_DOTS = 12
    }

    private enum class TransactionPhase {
        PRE_SUBMISSION,
        CONTENT_BUFFERING_STARTED,
        SUBMISSION_STARTED
    }
}
