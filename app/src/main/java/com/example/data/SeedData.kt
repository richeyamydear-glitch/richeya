package com.example.data

object SeedData {
    val fruits = listOf(
        FruitMenuEntity(
            name = "Semangka Segar",
            category = "Buah Segar",
            description = "Potongan semangka merah segar dan manis berair, cocok disantap dingin di sela kuliah.",
            weightInfo = "Cup Transparan + Garpu",
            priceRegular = 8000,
            priceJumbo = 12000,
            priceFamily = 22000,
            badgeText = "Terlaris",
            imageKey = "berries"
        ),
        FruitMenuEntity(
            name = "Melon Manis",
            category = "Buah Segar",
            description = "Potongan melon hijau harum manis lembut, segar untuk camilan sehat harga mahasiswa.",
            weightInfo = "Cup Transparan + Garpu",
            priceRegular = 8000,
            priceJumbo = 12000,
            priceFamily = 22000,
            badgeText = "Favorit",
            imageKey = "tropical"
        ),
        FruitMenuEntity(
            name = "Bengkoang Renyah",
            category = "Renyah & Rujak",
            description = "Bengkoang putih renyah dan segar alami, paling pas dicocol Saus Lotis atau Bumbu Bangkok.",
            weightInfo = "Cup Transparan + Garpu",
            priceRegular = 8000,
            priceJumbo = 12000,
            priceFamily = 22000,
            badgeText = "Crunchy",
            imageKey = "rujak"
        ),
        FruitMenuEntity(
            name = "Nanas Madu Segar",
            category = "Tropis Segar",
            description = "Potongan nanas kuning manis asam menyegarkan, pas untuk penambah energi harian.",
            weightInfo = "Cup Transparan + Garpu",
            priceRegular = 8000,
            priceJumbo = 12000,
            priceFamily = 22000,
            badgeText = "Segar",
            imageKey = "tropical"
        ),
        FruitMenuEntity(
            name = "Pepaya Manis",
            category = "Tropis Segar",
            description = "Pepaya matang manis alami, lembut, dan kaya serat untuk menjaga pencernaan.",
            weightInfo = "Cup Transparan + Garpu",
            priceRegular = 8000,
            priceJumbo = 12000,
            priceFamily = 22000,
            badgeText = "Hemat",
            imageKey = "tropical"
        ),
        FruitMenuEntity(
            name = "Mix 5 Buah Ah.Mantap",
            category = "Mix Cup",
            description = "Campuran lengkap Semangka, Bengkoang, Nanas, Melon, dan Pepaya dalam satu cup plus saus cocolan.",
            weightInfo = "Cup Mix + Mini Saus",
            priceRegular = 10000,
            priceJumbo = 15000,
            priceFamily = 28000,
            badgeText = "Best Seller",
            imageKey = "hero"
        )
    )

    val sauces = listOf(
        SauceOptionEntity(
            name = "Saus Lotis",
            flavorProfile = "Pedas Manis",
            description = "Lutis pedas manis dari bumbu rujak gula merah dan kacang sangrai.",
            extraPrice = 0
        ),
        SauceOptionEntity(
            name = "Bumbu Bangkok",
            flavorProfile = "Gurih Pedas",
            description = "Racikan khas cabai bubuk, gula, garam, dan terasi yang bikin nagih.",
            extraPrice = 1000
        ),
        SauceOptionEntity(
            name = "Saus Mayo",
            flavorProfile = "Creamy Manis",
            description = "Perpaduan creamy mayones dan susu kental manis (SKM) yang lembut.",
            extraPrice = 2000
        )
    )

    val promos = listOf(
        PromoBundleEntity(
            title = "Paket Hemat Mahasiswa (2 Cup)",
            subtitle = "2 Cup Reguler bebas pilih buah (Semangka/Melon/Bengkoang/Nanas/Pepaya) + 2 Mini Saus.",
            promoType = "PAKET_HEMAT",
            bundlePrice = 15000,
            discountPercent = 0,
            minQuantity = 2,
            isActive = true
        ),
        PromoBundleEntity(
            title = "Paket Rame-Rame Kost (4 Cup)",
            subtitle = "4 Cup Mix 5 Buah + lengkap 3 cocolan: Saus Lotis, Bumbu Bangkok, dan Saus Mayo.",
            promoType = "PAKET_HEMAT",
            bundlePrice = 34000,
            discountPercent = 0,
            minQuantity = 4,
            isActive = true
        ),
        PromoBundleEntity(
            title = "Promo Teman Kampus (Min. 3 Cup)",
            subtitle = "Diskon otomatis 10% di keranjang saat pesan 3 cup atau lebih bareng teman!",
            promoType = "PROMO_TEMAN",
            bundlePrice = 0,
            discountPercent = 10,
            minQuantity = 3,
            isActive = true
        )
    )

    val defaultProfile = CustomerProfileEntity(
        id = 1,
        customerName = "Pembeli Ah.Mantap",
        customerPhone = "081234567890",
        deliveryAddress = "Area Kampus Undip Tembalang",
        storeName = "Ah.Mantap",
        storeWhatsapp = "6281234567890"
    )
}
