import { PrismaClient } from '../generated/prisma/client.js'
import { PrismaBetterSqlite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSqlite3({
  url: 'file:./dev.db',
})
const prisma = new PrismaClient({ adapter })

const products = [
  { name: 'Chocolate Cookies', price: 3.50, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=400&h=400&fit=crop' },
  { name: 'Green Popcorn', price: 6.00, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=400&h=400&fit=crop' },
  { name: 'Classic Nachos', price: 4.75, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=400&h=400&fit=crop' },
  { name: 'Potato Chips', price: 2.99, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400&h=400&fit=crop' },
  { name: 'Caramel Popcorn', price: 5.50, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-149963844271-f8829c5a1697?w=400&h=400&fit=crop' },
  { name: 'Candy Bar', price: 1.99, category: 'candy', imageUrl: 'https://images.unsplash.com/photo-1575377427642-087cf684f29d?w=400&h=400&fit=crop' },
  { name: 'Trail Mix', price: 4.25, category: 'healthy', imageUrl: 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&h=400&fit=crop' },
  { name: 'Gummy Bears', price: 2.50, category: 'candy', imageUrl: 'https://images.unsplash.com/photo-1587132137056-bfbf0166836e?w=400&h=400&fit=crop' },
  { name: 'Pretzel Sticks', price: 3.25, category: 'snacks', imageUrl: 'https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&h=400&fit=crop' },
]

async function seed() {
  const count = await prisma.product.count()
  if (count > 0) {
    console.log(`Database already has ${count} products. Skipping seed.`)
    return
  }

  for (const product of products) {
    await prisma.product.create({ data: product })
  }

  console.log(`Seeded ${products.length} products.`)
}

seed()
  .catch(console.error)
  .finally(() => prisma.$disconnect())
