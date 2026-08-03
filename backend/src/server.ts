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

type ProductBody = {
  name?: unknown
  price?: unknown
  description?: unknown
  category?: unknown
  barcode?: unknown
  imageUrl?: unknown
}

function parseProductBody(body: ProductBody) {
  const name = typeof body.name === 'string' ? body.name.trim() : ''
  const category = typeof body.category === 'string' ? body.category.trim() : ''
  const barcode = typeof body.barcode === 'string' ? body.barcode.trim() : ''
  const imageUrl = typeof body.imageUrl === 'string' ? body.imageUrl.trim() : ''
  const description =
    typeof body.description === 'string' ? body.description.trim() : ''
  const price =
    typeof body.price === 'number'
      ? body.price
      : typeof body.price === 'string'
        ? Number(body.price)
        : NaN

  if (!name || !category || !barcode || !imageUrl || Number.isNaN(price) || price < 0) {
    return null
  }

  return { name, price, description, category, barcode, imageUrl }
}

function isUniqueConstraintError(error: unknown): boolean {
  return (
    typeof error === 'object' &&
    error !== null &&
    'code' in error &&
    (error as { code?: string }).code === 'P2002'
  )
}

app.get('/products', async (_req, res) => {
  const products = await prisma.product.findMany({
    orderBy: { id: 'asc' },
  })
  res.json(products)
})

app.post('/products', async (req, res) => {
  const data = parseProductBody(req.body as ProductBody)
  if (!data) {
    res.status(400).json({ error: 'Invalid product payload' })
    return
  }

  try {
    const product = await prisma.product.create({ data })
    res.status(201).json(product)
  } catch (error) {
    if (isUniqueConstraintError(error)) {
      res.status(409).json({ error: 'Barcode already exists' })
      return
    }
    console.error('[POST /products]', error)
    res.status(500).json({ error: 'Failed to create product' })
  }
})

app.put('/products/:id', async (req, res) => {
  const id = Number(req.params.id)
  if (!Number.isInteger(id) || id <= 0) {
    res.status(400).json({ error: 'Invalid product id' })
    return
  }

  const data = parseProductBody(req.body as ProductBody)
  if (!data) {
    res.status(400).json({ error: 'Invalid product payload' })
    return
  }

  try {
    const product = await prisma.product.update({
      where: { id },
      data,
    })
    res.json(product)
  } catch (error) {
    if (isUniqueConstraintError(error)) {
      res.status(409).json({ error: 'Barcode already exists' })
      return
    }
    if (
      typeof error === 'object' &&
      error !== null &&
      'code' in error &&
      (error as { code?: string }).code === 'P2025'
    ) {
      res.status(404).json({ error: 'Product not found' })
      return
    }
    console.error('[PUT /products/:id]', error)
    res.status(500).json({ error: 'Failed to update product' })
  }
})

app.delete('/products/:id', async (req, res) => {
  const id = Number(req.params.id)
  if (!Number.isInteger(id) || id <= 0) {
    res.status(400).json({ error: 'Invalid product id' })
    return
  }

  try {
    await prisma.product.delete({ where: { id } })
    res.status(204).send()
  } catch (error) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'code' in error &&
      (error as { code?: string }).code === 'P2025'
    ) {
      res.status(404).json({ error: 'Product not found' })
      return
    }
    console.error('[DELETE /products/:id]', error)
    res.status(500).json({ error: 'Failed to delete product' })
  }
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
