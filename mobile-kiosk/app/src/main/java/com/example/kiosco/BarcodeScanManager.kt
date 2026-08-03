package com.example.kiosco

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

/**
 * Listens for SUNMI scanner broadcasts and forwards decoded barcodes.
 *
 * Device setup: set scanner output to Broadcast mode (not keyboard/HID).
 * Action: com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED, extra key: "data"
 */
class BarcodeScanManager(
    private val context: Context,
    private val onBarcode: (String) -> Unit
) {
    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != ACTION_DATA_CODE_RECEIVED) return
            val code = intent.getStringExtra(DATA_EXTRA)?.trim().orEmpty()
            if (code.isNotEmpty()) {
                onBarcode(code)
            }
        }
    }

    fun register() {
        if (registered) return
        val filter = IntentFilter(ACTION_DATA_CODE_RECEIVED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
        registered = true
    }

    fun unregister() {
        if (!registered) return
        try {
            context.unregisterReceiver(receiver)
        } catch (_: IllegalArgumentException) {
            // Already unregistered
        }
        registered = false
    }

    companion object {
        const val ACTION_DATA_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
        private const val DATA_EXTRA = "data"
    }
}
