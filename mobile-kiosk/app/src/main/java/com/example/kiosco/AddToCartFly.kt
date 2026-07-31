package com.example.kiosco

import androidx.compose.ui.geometry.Offset

data class AddToCartFlyEvent(
    val id: Long,
    val imageUrl: String,
    val startCenter: Offset,
    val startSize: Float,
    val endCenter: Offset,
    val endSize: Float
)

fun quadraticBezier(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset {
    val u = 1f - t
    val x = u * u * p0.x + 2f * u * t * p1.x + t * t * p2.x
    val y = u * u * p0.y + 2f * u * t * p1.y + t * t * p2.y
    return Offset(x, y)
}

fun defaultFlyControlPoint(start: Offset, end: Offset): Offset {
    val midX = (start.x + end.x) / 2f
    val midY = (start.y + end.y) / 2f
    val lift = (end.y - start.y).coerceAtLeast(120f) * 0.35f
    return Offset(midX, midY - lift)
}
