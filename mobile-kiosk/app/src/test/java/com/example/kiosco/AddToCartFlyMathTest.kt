package com.example.kiosco

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class AddToCartFlyMathTest {
    @Test
    fun quadraticBezier_atEndpoints_matchesP0AndP2() {
        val p0 = Offset(0f, 0f)
        val p1 = Offset(50f, -100f)
        val p2 = Offset(100f, 200f)
        assertEquals(0f, quadraticBezier(0f, p0, p1, p2).x, 0.01f)
        assertEquals(0f, quadraticBezier(0f, p0, p1, p2).y, 0.01f)
        assertEquals(100f, quadraticBezier(1f, p0, p1, p2).x, 0.01f)
        assertEquals(200f, quadraticBezier(1f, p0, p1, p2).y, 0.01f)
    }

    @Test
    fun quadraticBezier_atMidpoint_pullsTowardControl() {
        val p0 = Offset(0f, 0f)
        val p1 = Offset(0f, -100f)
        val p2 = Offset(0f, 0f)
        val mid = quadraticBezier(0.5f, p0, p1, p2)
        assertEquals(0f, mid.x, 0.01f)
        assertEquals(-50f, mid.y, 0.01f)
    }

    @Test
    fun defaultFlyControlPoint_isAboveMidpoint() {
        val start = Offset(100f, 400f)
        val end = Offset(200f, 800f)
        val control = defaultFlyControlPoint(start, end)
        assertEquals(150f, control.x, 0.01f)
        // Control y must be less than midpoint y (higher on screen)
        assertEquals(true, control.y < (start.y + end.y) / 2f)
    }
}
