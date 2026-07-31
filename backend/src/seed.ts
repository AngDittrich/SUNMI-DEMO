import { PrismaClient } from '../generated/prisma/client.js'
import { PrismaBetterSqlite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSqlite3({
  url: 'file:./dev.db',
})
const prisma = new PrismaClient({ adapter })

const products = [
  { name: 'Chocolate Cookies', price: 3.50, category: 'snacks', description: 'Crunchy butter cookies loaded with melting chocolate chips. Baked golden on the outside and soft in the middle for that classic bakery bite.', barcode: '5901234123457', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/065/847/113/small/tasty-biscuits-and-cookies-isolated-on-a-transparent-background-png.png' },
  { name: 'ICEE', price: 6.00, category: 'snacks', description: 'An ice-cold cherry slush served extra frosty. Sweet, tangy and refreshing, it is the perfect companion for a warm day at the cinema.', barcode: '5901234123464', imageUrl: 'https://www.icee.com/wp-content/uploads/2019/07/cherry-icee@2x.png' },
  { name: 'Classic Nachos', price: 4.75, category: 'snacks', description: 'Crispy corn tortilla chips smothered in warm melted cheese and mild jalapenos. A savory, shareable favorite with a satisfying crunch.', barcode: '5901234123471', imageUrl: 'https://png.pngtree.com/png-vector/20231019/ourmid/pngtree-mexican-food-nacho-png-image_10248870.png' },
  { name: 'Potato Chips', price: 2.99, category: 'snacks', description: 'Thin slices of potato fried to a light golden crisp and finished with a touch of sea salt. Simple, crunchy and impossible to put down.', barcode: '5901234123488', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/027/990/538/small/fast-food-potato-chip-corn-on-the-cob-generative-ai-free-png.png' },
  { name: 'Caramel Popcorn', price: 5.50, category: 'snacks', description: 'Fluffy popped corn coated in a glossy buttery caramel. Sweet with a hint of toffee, it delivers a rich taste that pampers the tongue.', barcode: '5901234123495', imageUrl: 'https://www.pngall.com/wp-content/uploads/2018/06/Caramel-Popcorn-Free-PNG-Image.png' },
  { name: 'Candy Bar', price: 1.99, category: 'candy', description: 'A colorful assortment of chewy fruit candies wrapped one by one. Bright, fruity flavors that make a quick and cheerful pick-me-up.', barcode: '5901234123501', imageUrl: 'https://www.pngarts.com/files/13/Sweet-Candy-Free-PNG-Image.png' },
  { name: 'Trail Mix', price: 4.25, category: 'healthy', description: 'A wholesome blend of roasted nuts, seeds and dried fruit. Naturally sweet and full of energy, ideal for a guilt-free snack break.', barcode: '5901234123518', imageUrl: 'https://png.pngtree.com/png-clipart/20241215/original/pngtree-trail-mix-png-image_17879138.png' },
  { name: 'Gummy Bears', price: 2.50, category: 'candy', description: 'Soft, translucent gummy bears bursting with juicy fruit flavor. Bouncy texture and a sweet finish that kids and adults both love.', barcode: '5901234123525', imageUrl: 'https://png.pngtree.com/png-clipart/20250415/original/pngtree-delicious-translucent-red-gummy-bear-clipart-illustration-png-image_20794058.png' },
  { name: 'Pretzel Sticks', price: 3.25, category: 'snacks', description: 'Golden baked pretzel sticks with a crisp snap and a sprinkle of salt. Lightly toasted flavor that pairs well with any drink.', barcode: '5901234123532', imageUrl: 'https://png.pngtree.com/png-vector/20230922/ourmid/pngtree-pretzel-sticks-golden-png-image_10094625.png' },
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
  