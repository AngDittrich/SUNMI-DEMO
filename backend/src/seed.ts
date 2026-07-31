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
  { name: 'Ranch Doritos', price: 2.99, category: 'snacks', description: 'Tangy ranch-flavored tortilla chips with a bold, zesty dusting. Crunchy, creamy and impossible to put down.', barcode: '5901234123533', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/036/128/727/small/ai-generated-doritos-style-tortilla-chips-png.png' },
  { name: 'Peanut Butter Cups', price: 2.25, category: 'candy', description: 'Rich chocolate cups with a creamy peanut butter center. The perfect balance of sweet and salty indulgence.', barcode: '5901234123534', imageUrl: 'https://png.pngtree.com/png-vector/20240305/ourmid/pngtree-peanut-butter-cup-png-image_14156982.png' },
  { name: 'Sour Gummy Worms', price: 2.50, category: 'candy', description: 'Twisted gummy worms coated in a tart, mouth-puckering sour sugar. Chewy, tangy and wildly addictive.', barcode: '5901234123535', imageUrl: 'https://png.pngtree.com/png-vector/20230816/ourmid/pngtree-sour-gummy-worms-png-image_10778336.png' },
  { name: 'Trail Mix Bar', price: 1.99, category: 'healthy', description: 'A chewy bar packed with oats, nuts and dried berries. A wholesome on-the-go snack with natural sweetness.', barcode: '5901234123536', imageUrl: 'https://png.pngtree.com/png-vector/20240125/ourmid/pngtree-granola-bar-png-image_14110953.png' },
  { name: 'Cheese Curls', price: 2.75, category: 'snacks', description: 'Puffy corn curls dusted with a sharp cheddar cheese powder. Light, airy and bursting with cheesy goodness.', barcode: '5901234123537', imageUrl: 'https://static.vecteezy.com/system/resources/thumbnails/033/480/123/small/cheese-puffs-corn-snacks-png.png' },
  { name: 'Fruit Roll-Up', price: 1.50, category: 'candy', description: 'A bright, chewy fruit snack roll with a glossy finish. Sweet strawberry flavor kids and grown-ups both enjoy.', barcode: '5901234123538', imageUrl: 'https://png.pngtree.com/png-vector/20230907/ourmid/pngtree-fruit-roll-up-png-image_12023631.png' },
  { name: 'Almond Clusters', price: 3.75, category: 'healthy', description: 'Toasted almonds coated in a thin layer of dark chocolate. A crunchy, antioxidant-rich pick-me-up.', barcode: '5901234123539', imageUrl: 'https://png.pngtree.com/png-vector/20240315/ourmid/pngtree-chocolate-covered-almonds-png-image_14277953.png' },
  { name: 'M&M Peanuts', price: 3.00, category: 'candy', description: 'Crisp sugar shells around roasted peanuts in every color of the rainbow. A colorful classic for any moment.', barcode: '5901234123540', imageUrl: 'https://png.pngtree.com/png-vector/20230715/ourmid/pngtree-mms-candies-png-image_10002348.png' },
  { name: 'Beef Jerky', price: 5.25, category: 'healthy', description: 'Lean strips of beef slow-cooked with a smoky teriyaki glaze. High in protein and full of savory flavor.', barcode: '5901234123541', imageUrl: 'https://png.pngtree.com/png-vector/20240125/ourmid/pngtree-beef-jerky-png-image_14111290.png' },
  { name: 'Chocolate Wafers', price: 2.25, category: 'snacks', description: 'Layered wafer sticks enrobed in smooth milk chocolate. Crisp, light and melt-in-your-mouth delicious.', barcode: '5901234123542', imageUrl: 'https://png.pngtree.com/png-vector/20230815/ourmid/pngtree-chocolate-wafer-sticks-png-image_10778112.png' },
  { name: 'Lollipops', price: 1.25, category: 'candy', description: 'A bright assortment of classic swirl lollipops. Sweet, colorful and made for sharing with a little one.', barcode: '5901234123543', imageUrl: 'https://png.pngtree.com/png-vector/20231019/ourmid/pngtree-lollipop-candy-png-image_10304485.png' },
  { name: 'Mixed Nuts', price: 4.50, category: 'healthy', description: 'A roasted medley of almonds, cashews, pecans and peanuts. A satisfying crunchy blend with a hint of sea salt.', barcode: '5901234123544', imageUrl: 'https://png.pngtree.com/png-vector/20230816/ourmid/pngtree-mixed-nuts-png-image_10778459.png' },
  { name: 'Popcorn', price: 2.75, category: 'snacks', description: 'Classic buttered popcorn popped to a fluffy golden crisp. The timeless movie-night favorite.', barcode: '5901234123545', imageUrl: 'https://www.pngall.com/wp-content/uploads/2018/06/Caramel-Popcorn-Free-PNG-Image.png' },
  { name: 'Marshmallows', price: 2.00, category: 'candy', description: 'Pillowy soft marshmallows with a sweet vanilla flavor. Great for s\u2019mores, hot chocolate or straight out of the bag.', barcode: '5901234123546', imageUrl: 'https://png.pngtree.com/png-vector/20240110/ourmid/pngtree-marshmallows-png-image_14022685.png' },
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
  