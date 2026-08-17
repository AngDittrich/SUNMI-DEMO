package com.example.kiosco

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kiosco.ui.theme.LocalBrandTheme
import com.example.kiosco.ui.theme.TextMuted

private const val PinLength = 4

@Composable
fun EmployeePinDialog(
    onUnlocked: () -> Unit,
    onDismiss: () -> Unit
) {
    val brandTheme = LocalBrandTheme.current
    var pin by remember { mutableStateOf("") }

    fun appendDigit(digit: String) {
        if (pin.length >= PinLength) return
        val next = pin + digit
        pin = next
        if (next.length == PinLength) {
            onUnlocked()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = brandTheme.surface,
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Modo empleado",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = brandTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ingresa cualquier PIN de 4 dígitos",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(PinLength) { index ->
                        val filled = index < pin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (filled) brandTheme.accent else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "⌫")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "" -> Box(modifier = Modifier.size(72.dp))
                                "⌫" -> PinKey(
                                    onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Borrar",
                                        tint = brandTheme.textPrimary
                                    )
                                }
                                else -> PinKey(onClick = { appendDigit(key) }) {
                                    Text(
                                        text = key,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = brandTheme.textPrimary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "Cancelar",
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(onClick = onDismiss)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun PinKey(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFFF2F2F2))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
