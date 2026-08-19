package com.example.kiosco

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.util.Log

/**
 * Reads NFC tags on SUNMI FLEX 3 using the standard Android NFC stack.
 *
 * Tag identification order:
 *  1. NDEF payload (first non-blank UTF-8 record) — e.g. "NFC-001"
 *  2. Tag UID as uppercase hex — e.g. "04A2B3C4D5E6F7"
 *
 * Products are matched by this identifier against the `nfcId` column
 * (see docs/superpowers/plans/2026-08-18-sunmi-nfc-reader.md, Task 4).
 *
 * SUNMI's proprietary NfcControlManager (switching between the under-screen
 * and external NFC antennas, plus the watermark) is delivered as a local AAR
 * through the SUNMI partner platform and is NOT published on Maven Central.
 * The [init] hook below is where those calls belong once the AAR is available:
 *
 * ```kotlin
 * NfcManager.init(activity) { success ->
 *     if (success) NfcControlManager.registerNfcListener(object : INfcListener.Stub() {
 *         override fun onNfcListChanged(nfcList: MutableList<Nfc>?) {
 *             nfcList?.firstOrNull()?.let {
 *                 NfcControlManager.switchNfc(it.sn)
 *                 NfcControlManager.setNfcWaterMarkAlpha(50)
 *             }
 *         }
 *     })
 * }
 * ```
 */
class NfcScanManager(
    private val context: Context,
    private val onTagRead: (String) -> Unit
) {
    private var nfcAdapter: NfcAdapter? = null
    private var lastHandledIntent: Intent? = null

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, context.javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }
        PendingIntent.getActivity(context, 0, intent, flags)
    }

    /** Called once from the Activity composition. Safe to call more than once. */
    fun init() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val adapter = nfcAdapter
        when {
            adapter == null -> Log.w(TAG, "No se detectó hardware NFC en este dispositivo")
            !adapter.isEnabled -> Log.w(TAG, "NFC está desactivado en ajustes del sistema")
            else -> Log.i(TAG, "NFC disponible y activo")
        }
        // Hook: SUNMI NfcManager.init + NfcControlManager.switchNfc/watermark
        // go here once the proprietary AAR is added to the build.
    }

    /**
     * Extracts a tag identifier from a foreground-dispatch / launch intent.
     * Returns the identifier (NDEF payload or UID hex) or null when the
     * intent is not a tag intent or was already handled.
     */
    fun onNewIntent(intent: Intent): String? {
        val action = intent.action
        val isTagIntent = action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_TAG_DISCOVERED
        if (!isTagIntent) return null
        if (intent === lastHandledIntent) return null
        lastHandledIntent = intent

        val tag = extractTag(intent) ?: return null
        val ndefPayload = readNdefPayload(tag)
        val tagId = ndefPayload ?: tag.id.joinToString("") { "%02X".format(it) }
        if (tagId.isNotEmpty()) {
            onTagRead(tagId)
        }
        return tagId
    }

    /** Enables foreground dispatch so tag taps reach this Activity while open. */
    fun enableForegroundDispatch(activity: Activity) {
        val adapter = nfcAdapter ?: NfcAdapter.getDefaultAdapter(activity) ?: return
        if (!adapter.isEnabled) return
        val filters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
        try {
            adapter.enableForegroundDispatch(activity, pendingIntent, filters, null)
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo habilitar foreground dispatch: ${e.message}")
        }
    }

    /** Disables foreground dispatch; call from the Activity onPause. */
    fun disableForegroundDispatch(activity: Activity) {
        val adapter = nfcAdapter ?: NfcAdapter.getDefaultAdapter(activity) ?: return
        try {
            adapter.disableForegroundDispatch(activity)
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo deshabilitar foreground dispatch: ${e.message}")
        }
    }

    /** Releases resources; makes repeated calls safe. */
    fun destroy() {
        nfcAdapter = null
        lastHandledIntent = null
    }

    @Suppress("DEPRECATION")
    private fun extractTag(intent: Intent): Tag? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }

    private fun readNdefPayload(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        return try {
            ndef.connect()
            val message = ndef.ndefMessage ?: return null
            decodeMessage(message)
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo leer el mensaje NDEF: ${e.message}")
            null
        } finally {
            try {
                ndef.close()
            } catch (_: Exception) {
                // Already closed or connection lost
            }
        }
    }

    private fun decodeMessage(message: NdefMessage): String? {
        for (record in message.records) {
            val text = decodeRecord(record) ?: continue
            if (text.isNotBlank()) return text
        }
        return null
    }

    private fun decodeRecord(record: NdefRecord): String? {
        return try {
            val isWellKnownText = record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                record.type.contentEquals(NdefRecord.RTD_TEXT)
            when {
                isWellKnownText -> {
                    val payload = record.payload
                    if (payload.isEmpty()) return null
                    val status = payload[0].toInt()
                    val languageCodeLength = status and 0x3F
                    val start = 1 + languageCodeLength
                    String(payload, start, payload.size - start, Charsets.UTF_8)
                }
                record.tnf != NdefRecord.TNF_EMPTY && record.payload.isNotEmpty() ->
                    String(record.payload, Charsets.UTF_8)
                else -> null
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo decodificar el registro NDEF: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "NfcScanManager"
    }
}