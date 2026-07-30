import 'dotenv/config'
import express from 'express'
import { createServer } from 'http'
import { Server } from 'socket.io'
import cors from 'cors'
import { PrismaClient } from '../generated/prisma/client.js'
import { PrismaBetterSqlite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSqlite3({
  url: process.env.DATABASE_URL ?? 'file:./dev.db',
})

const prisma = new PrismaClient({ adapter })

const app = express()
const httpServer = createServer(app)

app.use(cors())
app.use(express.json())

const io = new Server(httpServer, {
  cors: {
    origin: '*',
  },
})

app.get('/', (_req, res) => {
  res.json({ status: 'ok', message: 'Servidor POS SUNMI activo y funcionando' })
})

app.get('/products', async (_req, res) => {
  const products = await prisma.product.findMany({
    orderBy: { id: 'asc' },
  })
  res.json(products)
})

io.on('connection', (socket) => {
  console.log(`[WebSocket] Dispositivo conectado con ID: ${socket.id}`)

  socket.on('disconnect', () => {
    console.log(`[WebSocket] Dispositivo desconectado: ${socket.id}`)
  })
})

const PORT = Number(process.env.PORT) || 3000
httpServer.listen(PORT, '0.0.0.0', () => {
  console.log(`[Servidor] Escuchando activamente en http://0.0.0.0:${PORT}`)
})
