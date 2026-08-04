package com.example.kiosco.data

object ProductImages {
    const val PLACEHOLDER_FILE = "cookie.png"

    fun assetUri(fileName: String): String =
        "file:///android_asset/products/$fileName"

    val PLACEHOLDER: String = assetUri(PLACEHOLDER_FILE)
}
