package com.example.kiosco

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

/**
 * Administrador singleton para gestionar la conexión WebSocket con el backend Node.js
 */
object SocketManager {
    private var socket: Socket? = null

    // IP de la pc corriendo backend con Node
    private const val SERVER_URL = "http://192.168.10.4:3000"

    fun init(onStatusChange: (Boolean) -> Unit) {
        try {
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
            }

            socket = IO.socket(SERVER_URL, options)

            // Evento: Conexión exitosa
            socket?.on(Socket.EVENT_CONNECT) {
                onStatusChange(true)
            }

            // Evento: Desconexión
            socket?.on(Socket.EVENT_DISCONNECT) {
                onStatusChange(false)
            }

            socket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
    }
}