import { PrismaClient } from '../generated/prisma/client.js'
import { PrismaBetterSqlite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSqlite3({
  url: 'file:./dev.db',
})
const prisma = new PrismaClient({ adapter })

const products = [
  { name: 'Chocolate Cookies', price: 3.50, category: 'snacks', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/065/847/113/small/tasty-biscuits-and-cookies-isolated-on-a-transparent-background-png.png' },
  { name: 'ICEE', price: 6.00, category: 'snacks', imageUrl: 'https://www.icee.com/wp-content/uploads/2019/07/cherry-icee@2x.png' },
  { name: 'Classic Nachos', price: 4.75, category: 'snacks', imageUrl: 'https://png.pngtree.com/png-vector/20231019/ourmid/pngtree-mexican-food-nacho-png-image_10248870.png' },
  { name: 'Potato Chips', price: 2.99, category: 'snacks', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/027/990/538/small/fast-food-potato-chip-corn-on-the-cob-generative-ai-free-png.png' },
  { name: 'Caramel Popcorn', price: 5.50, category: 'snacks', imageUrl: 'https://www.pngall.com/wp-content/uploads/2018/06/Caramel-Popcorn-Free-PNG-Image.png' },
  { name: 'Candy Bar', price: 1.99, category: 'candy', imageUrl: 'https://www.pngarts.com/files/13/Sweet-Candy-Free-PNG-Image.png' },
  { name: 'Trail Mix', price: 4.25, category: 'healthy', imageUrl: 'https://png.pngtree.com/png-clipart/20241215/original/pngtree-trail-mix-png-image_17879138.png' },
  { name: 'Gummy Bears', price: 2.50, category: 'candy', imageUrl: 'https://png.pngtree.com/png-clipart/20250415/original/pngtree-delicious-translucent-red-gummy-bear-clipart-illustration-png-image_20794058.png' },
  { name: 'Pretzel Sticks', price: 3.25, category: 'snacks', imageUrl: 'https://png.pngtree.com/png-vector/20230922/ourmid/pngtree-pretzel-sticks-golden-png-image_10094625.png' },
]

async function seed() {
  await prisma.product.deleteMany()

  for (const product of products) {
    await prisma.product.create({ data: product })
  }

  console.log(`Seeded ${products.length} products.`)
}

seed()
  .catch(console.error)
  .finally(() => prisma.$disconnect())
  