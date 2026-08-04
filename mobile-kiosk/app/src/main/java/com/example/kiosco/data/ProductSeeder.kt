package com.example.kiosco.data

object ProductSeeder {
    fun seedProducts(): List<ProductEntity> = listOf(
        product(
            name = "Galletas de Chocolate",
            price = 3.50,
            category = "snacks",
            barcode = "5901234123457",
            image = "cookie.png",
            description = "Crujientes galletas de mantequilla repletas de chips de chocolate derretido. Horneadas doradas por fuera y suaves por dentro para ese clásico sabor de panadería."
        ),
        product(
            name = "ICEE",
            price = 6.00,
            category = "snacks",
            barcode = "5901234123464",
            image = "ICEE.png",
            description = "Un granizado de cereza helado servido extra frío. Dulce, ácido y refrescante, es el compañero perfecto para un día cálido de cine."
        ),
        product(
            name = "Nachos Clásicos",
            price = 4.75,
            category = "snacks",
            barcode = "5901234123471",
            image = "nachos.webp",
            description = "Crujientes totopos de maíz bañados en queso derretido caliente y jalapeños suaves. Un favorito salado para compartir con un crujido satisfactorio."
        ),
        product(
            name = "Papas Fritas",
            price = 2.99,
            category = "snacks",
            barcode = "5901234123488",
            image = "chips.png",
            description = "Delgadas rodajas de papa fritas hasta quedar ligeramente crujientes y doradas, terminadas con un toque de sal marina. Simples, crujientes e imposibles de dejar."
        ),
        product(
            name = "Palomitas de Caramelo",
            price = 5.50,
            category = "snacks",
            barcode = "5901234123495",
            image = "caramelpopcorn.png",
            description = "Palomitas de maíz esponjosas cubiertas con un brillante caramelo de mantequilla. Dulces con un toque de toffee, ofrecen un sabor rico que consiente al paladar."
        ),
        product(
            name = "Barra de Dulces",
            price = 1.99,
            category = "dulces",
            barcode = "5901234123501",
            image = "barracaramelo.png",
            description = "Un colorido surtido de caramelos masticables de frutas envueltos individualmente. Sabores frutales y brillantes que te darán un impulso rápido y alegre."
        ),
        product(
            name = "Mezcla de Frutos Secos",
            price = 4.25,
            category = "saludable",
            barcode = "5901234123518",
            image = "frutossecos.png",
            description = "Una mezcla saludable de nueces tostadas, semillas y frutas deshidratadas. Naturalmente dulce y llena de energía, ideal para un refrigerio sin culpa."
        ),
        product(
            name = "Gomitas de Osito",
            price = 2.50,
            category = "dulces",
            barcode = "5901234123525",
            image = "gomitasdeoso.webp",
            description = "Gomitas de osito suaves y translúcidas repletas de un jugoso sabor frutal. Textura masticable y un final dulce que encanta a niños y adultos."
        ),
        product(
            name = "Palitos de Pretzel",
            price = 3.25,
            category = "snacks",
            barcode = "5901234123532",
            image = "palospretzel.png",
            description = "Palitos de pretzel horneados dorados con un crujido crujiente y una pizca de sal. Sabor ligeramente tostado que combina bien con cualquier bebida."
        ),
        product(
            name = "Doritos Ranch",
            price = 2.99,
            category = "snacks",
            barcode = "5901234123533",
            image = "doritosranch.png",
            description = "Totopos de tortilla con sabor a ranch picante y un condimento atrevido y sabroso. Crujientes, cremosos e imposibles de dejar."
        ),
        product(
            name = "Copas de Mantequilla de Maní",
            price = 2.25,
            category = "dulces",
            barcode = "5901234123534",
            image = "mantequillademani.png",
            description = "Ricas copas de chocolate con un centro cremoso de mantequilla de maní. El equilibrio perfecto entre indulgencia dulce y salada."
        ),
        product(
            name = "Gusanos de Gomita Ácidos",
            price = 2.50,
            category = "dulces",
            barcode = "5901234123535",
            image = "gusanosdegoma.webp",
            description = "Gomitas de gusano retorcidas cubiertas con una azúcar ácida que hace la boca agua. Masticables, ácidas y sumamente adictivas."
        ),
        product(
            name = "Barra de Frutos Secos",
            price = 1.99,
            category = "saludable",
            barcode = "5901234123536",
            image = "barrasfrutossecos.png",
            description = "Una barra masticable repleta de avena, nueces y bayas deshidratadas. Un refrigerio saludable para llevar con dulzura natural."
        ),
        product(
            name = "Ricitos de Queso",
            price = 2.75,
            category = "snacks",
            barcode = "5901234123537",
            image = "ricitosdequeso.webp",
            description = "Ricitos de maíz inflado espolvoreados con un intenso queso cheddar en polvo. Ligeros, esponjosos y repletos de bondad quesosa."
        ),
        product(
            name = "Rollito de Fruta",
            price = 1.50,
            category = "dulces",
            barcode = "5901234123538",
            image = "rollodefruta.png",
            description = "Un rollo de bocadillo frutal brillante y masticable con un acabado lustroso. Dulce sabor a fresa que disfrutan niños y adultos."
        ),
        product(
            name = "Clústeres de Almendra",
            price = 3.75,
            category = "saludable",
            barcode = "5901234123539",
            image = "almonds.png",
            description = "Almendras tostadas cubiertas con una fina capa de chocolate oscuro. Un bocadillo crujiente y rico en antioxidantes."
        ),
        product(
            name = "M&Ms con Cacahuate",
            price = 3.00,
            category = "dulces",
            barcode = "5901234123540",
            image = "mandmscacahuate.webp",
            description = "Crujientes coberturas de azúcar alrededor de cacahuates tostados en todos los colores del arcoíris. Un clásico colorido para cualquier momento."
        ),
        product(
            name = "Carne Seca",
            price = 5.25,
            category = "saludable",
            barcode = "5901234123541",
            image = "carneseca.png",
            description = "Tiras magras de carne de res cocinadas a fuego lento con un glaseado ahumado de teriyaki. Altas en proteínas y llenas de sabor salado."
        ),
        product(
            name = "Obleas de Chocolate",
            price = 2.25,
            category = "snacks",
            barcode = "5901234123542",
            image = "obleaschocolate.png",
            description = "Barritas de oblea en capas cubiertas de suave chocolate de leche. Crujientes, ligeras y deliciosas que se derriten en tu boca."
        ),
        product(
            name = "Paletas",
            price = 1.25,
            category = "dulces",
            barcode = "5901234123543",
            image = "paletas.webp",
            description = "Un colorido surtido de paletas clásicas con remolinos. Dulces, coloridas y hechas para compartir."
        ),
        product(
            name = "Nueces Mixtas",
            price = 4.50,
            category = "saludable",
            barcode = "5901234123544",
            image = "nuecesmixtas.png",
            description = "Una mezcla tostada de almendras, marañones, pecán y cacahuates. Una mezcla crujiente y satisfactoria con un toque de sal marina."
        ),
        product(
            name = "Palomitas de Maíz",
            price = 2.75,
            category = "snacks",
            barcode = "5901234123545",
            image = "palomitas.webp",
            description = "Palomitas clásicas con mantequilla reventadas hasta quedar crujientes y doradas. El favorito de siempre para noches de películas."
        ),
        product(
            name = "Malvaviscos",
            price = 2.00,
            category = "dulces",
            barcode = "5901234123546",
            image = "malvaviscos.png",
            description = "Malvaviscos esponjosos y suaves con un dulce sabor a vainilla. Ideales para s'mores, chocolate caliente o directamente de la bolsa."
        )
    )

    private fun product(
        name: String,
        price: Double,
        category: String,
        barcode: String,
        image: String,
        description: String
    ) = ProductEntity(
        name = name,
        price = price,
        category = category,
        barcode = barcode,
        imageUrl = ProductImages.assetUri(image),
        description = description
    )
}
