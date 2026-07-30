package com.example.kiosco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiosco.ui.theme.KioscoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KioscoTheme {
                // Estado mutable para rastrear si el WebSocket está conectado
                var isConnected by remember { mutableStateOf(false) }

                // Inicializar conexión WebSocket al cargar la interfaz
                LaunchedEffect(Unit) {
                    SocketManager.init { connected ->
                        isConnected = connected
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KioskStatusScreen(
                        isConnected = isConnected,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.disconnect()
    }
}

@Composable
fun KioskStatusScreen(isConnected: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SUNMI Kiosko POS Demo",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Indicador de estado de conexión
        Box(
            modifier = Modifier
                .background(
                    color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (isConnected) "Servidor Conectado" else "Servidor Desconectado",
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}