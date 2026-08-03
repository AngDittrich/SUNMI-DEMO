import { PrismaClient } from '../generated/prisma/client.js'
import { PrismaBetterSqlite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSqlite3({
  url: 'file:./dev.db',
})
const prisma = new PrismaClient({ adapter })

const img = (id: string) => `https://drive.google.com/uc?export=download&id=${id}`

const products = [
  { name: 'Galletas de Chocolate', price: 3.50, category: 'snacks', description: 'Crujientes galletas de mantequilla repletas de chips de chocolate derretido. Horneadas doradas por fuera y suaves por dentro para ese clásico sabor de panadería.', barcode: '5901234123457', imageUrl: img('1u7SgPztkpLJljw9OPl0L7igOppK2Ejag') },
  { name: 'ICEE', price: 6.00, category: 'snacks', description: 'Un granizado de cereza helado servido extra frío. Dulce, ácido y refrescante, es el compañero perfecto para un día cálido de cine.', barcode: '5901234123464', imageUrl: img('19FxZ9NVd3qhr_UM8XWeOCxp4Mp5Lipdr') },
  { name: 'Nachos Clásicos', price: 4.75, category: 'snacks', description: 'Crujientes totopos de maíz bañados en queso derretido caliente y jalapeños suaves. Un favorito salado para compartir con un crujido satisfactorio.', barcode: '5901234123471', imageUrl: img('1KQRKMp331fE-YSwqbeJ-i8O-go90S7kQ') },
  { name: 'Papas Fritas', price: 2.99, category: 'snacks', description: 'Delgadas rodajas de papa fritas hasta quedar ligeramente crujientes y doradas, terminadas con un toque de sal marina. Simples, crujientes e imposibles de dejar.', barcode: '5901234123488', imageUrl: img('18P7VgH0nmN02XJDQ0QpvAhF3hxl1rNV-') },
  { name: 'Palomitas de Caramelo', price: 5.50, category: 'snacks', description: 'Palomitas de maíz esponjosas cubiertas con un brillante caramelo de mantequilla. Dulces con un toque de toffee, ofrecen un sabor rico que consiente al paladar.', barcode: '5901234123495', imageUrl: img('1rSIurfpRfYFiz9t9s8XroKoBpLdQaiw_') },
  { name: 'Barra de Dulces', price: 1.99, category: 'dulces', description: 'Un colorido surtido de caramelos masticables de frutas envueltos individualmente. Sabores frutales y brillantes que te darán un impulso rápido y alegre.', barcode: '5901234123501', imageUrl: img('1g51olB8RfNI8rBJNjMggG4RYE0NXK28z') },
  { name: 'Mezcla de Frutos Secos', price: 4.25, category: 'saludable', description: 'Una mezcla saludable de nueces tostadas, semillas y frutas deshidratadas. Naturalmente dulce y llena de energía, ideal para un refrigerio sin culpa.', barcode: '5901234123518', imageUrl: img('1pJvblniea-EGKK_N8gaRoIAnvgpBxj5M') },
  { name: 'Gomitas de Osito', price: 2.50, category: 'dulces', description: 'Gomitas de osito suaves y translúcidas repletas de un jugoso sabor frutal. Textura masticable y un final dulce que encanta a niños y adultos.', barcode: '5901234123525', imageUrl: img('10BbhujppUKrD4tC45ZeAqz4FotY-703a') },
  { name: 'Palitos de Pretzel', price: 3.25, category: 'snacks', description: 'Palitos de pretzel horneados dorados con un crujido crujiente y una pizca de sal. Sabor ligeramente tostado que combina bien con cualquier bebida.', barcode: '5901234123532', imageUrl: img('1DVgdzOvQKQn9xwOtOka7dutRmLjK4kYX') },
  { name: 'Doritos Ranch', price: 2.99, category: 'snacks', description: 'Totopos de tortilla con sabor a ranch picante y un condimento atrevido y sabroso. Crujientes, cremosos e imposibles de dejar.', barcode: '5901234123533', imageUrl: img('1-5HgJTh02DC6__bG7fk--cFrWNCp_9JE') },
  { name: 'Copas de Mantequilla de Maní', price: 2.25, category: 'dulces', description: 'Ricas copas de chocolate con un centro cremoso de mantequilla de maní. El equilibrio perfecto entre indulgencia dulce y salada.', barcode: '5901234123534', imageUrl: img('17-Hjy6xatpEe755DkeL8S8eaVnz9UNyE') },
  { name: 'Gusanos de Gomita Ácidos', price: 2.50, category: 'dulces', description: 'Gomitas de gusano retorcidas cubiertas con una azúcar ácida que hace la boca agua. Masticables, ácidas y sumamente adictivas.', barcode: '5901234123535', imageUrl: img('1AjFNHFufMURIzVYyi5a-AfhXLxmGZObS') },
  { name: 'Barra de Frutos Secos', price: 1.99, category: 'saludable', description: 'Una barra masticable repleta de avena, nueces y bayas deshidratadas. Un refrigerio saludable para llevar con dulzura natural.', barcode: '5901234123536', imageUrl: img('14Ws17Wkdi9PsaEnc0QlZTRmTcMC5Vfip') },
  { name: 'Ricitos de Queso', price: 2.75, category: 'snacks', description: 'Ricitos de maíz inflado espolvoreados con un intenso queso cheddar en polvo. Ligeros, esponjosos y repletos de bondad quesosa.', barcode: '5901234123537', imageUrl: img('11PuKeSo7AGhOyD6oq6luqb9mXpDf5ZFV') },
  { name: 'Rollito de Fruta', price: 1.50, category: 'dulces', description: 'Un rollo de bocadillo frutal brillante y masticable con un acabado lustroso. Dulce sabor a fresa que disfrutan niños y adultos.', barcode: '5901234123538', imageUrl: img('1H98qWTHA8QoulO-htgr0SWKnb_BkXqgI') },
  { name: 'Clústeres de Almendra', price: 3.75, category: 'saludable', description: 'Almendras tostadas cubiertas con una fina capa de chocolate oscuro. Un bocadillo crujiente y rico en antioxidantes.', barcode: '5901234123539', imageUrl: img('1oHg9AewXEZhkhcXClOgHxunJX8cuBM9R') },
  { name: 'M&Ms con Cacahuate', price: 3.00, category: 'dulces', description: 'Crujientes coberturas de azúcar alrededor de cacahuates tostados en todos los colores del arcoíris. Un clásico colorido para cualquier momento.', barcode: '5901234123540', imageUrl: img('1BPHjJGU-lgVxxJQL1Pj35IjtAfyWIlZy') },
  { name: 'Carne Seca', price: 5.25, category: 'saludable', description: 'Tiras magras de carne de res cocinadas a fuego lento con un glaseado ahumado de teriyaki. Altas en proteínas y llenas de sabor salado.', barcode: '5901234123541', imageUrl: img('1HMbsQnriL1fwin618ax0GX9iBEmrJU89') },
  { name: 'Obleas de Chocolate', price: 2.25, category: 'snacks', description: 'Barritas de oblea en capas cubiertas de suave chocolate de leche. Crujientes, ligeras y deliciosas que se derriten en tu boca.', barcode: '5901234123542', imageUrl: img('1HMbsQnriL1fwin618ax0GX9iBEmrJU89') },
  { name: 'Paletas', price: 1.25, category: 'dulces', description: 'Un colorido surtido de paletas clásicas con remolinos. Dulces, coloridas y hechas para compartir.', barcode: '5901234123543', imageUrl: img('1jI2ftdPLHntcjWgFYk9C7reTXVk_Hg_Y') },
  { name: 'Nueces Mixtas', price: 4.50, category: 'saludable', description: 'Una mezcla tostada de almendras, marañones, pecán y cacahuates. Una mezcla crujiente y satisfactoria con un toque de sal marina.', barcode: '5901234123544', imageUrl: img('1asBbB-JGPoGTYqj_gBrm5QIDMf0wJgm8') },
  { name: 'Palomitas de Maíz', price: 2.75, category: 'snacks', description: 'Palomitas clásicas con mantequilla reventadas hasta quedar crujientes y doradas. El favorito de siempre para noches de películas.', barcode: '5901234123545', imageUrl: img('16km1fo1Dz8Ir1wlKlOHL-gwTodpCALjp') },
  { name: 'Malvaviscos', price: 2.00, category: 'dulces', description: 'Malvaviscos esponjosos y suaves con un dulce sabor a vainilla. Ideales para s\'mores, chocolate caliente o directamente de la bolsa.', barcode: '5901234123546', imageUrl: img('1V6L2ZvVioGo2xBIyNIMlm-452z8Fj_zo') },
]

async function seed() {
  await prisma.product.deleteMany()

  for (const product of products) {
    await prisma.product.create({ data: product })
  }

  console.log(`Se sembraron ${products.length} productos.`)
}

seed()
  .catch(console.error)
  .finally(() => prisma.$disconnect())
