package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fruit_menu")
data class FruitMenuEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val description: String,
    val weightInfo: String,
    val priceRegular: Int,
    val priceJumbo: Int,
    val priceFamily: Int,
    val badgeText: String = "",
    val imageKey: String,
    val isAvailable: Boolean = true
)

@Entity(tableName = "sauce_options")
data class SauceOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val flavorProfile: String,
    val description: String,
    val extraPrice: Int,
    val isAvailable: Boolean = true
)

@Entity(tableName = "promo_bundles")
data class PromoBundleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String,
    val promoType: String, // "PAKET_HEMAT" or "PROMO_TEMAN"
    val bundlePrice: Int,
    val discountPercent: Int,
    val minQuantity: Int,
    val isActive: Boolean = true
)

@Entity(tableName = "saved_sauce_mixes")
data class SavedSauceMixEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mixName: String,
    val sauceNames: String,
    val portionLevel: String,
    val spiceLevel: String,
    val extraCost: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fruitId: Int,
    val fruitName: String,
    val cupSize: String,
    val unitPrice: Int,
    val sauceSummary: String,
    val portionLevel: String,
    val spiceLevel: String,
    val sauceExtraCost: Int,
    val quantity: Int,
    val notes: String = ""
)

@Entity(tableName = "pre_orders")
data class PreOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderCode: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryType: String,
    val deliveryAddress: String,
    val preOrderDateLabel: String,
    val preOrderTimeSlot: String,
    val paymentMethod: String,
    val itemsSummary: String,
    val subtotal: Int,
    val discountAmount: Int,
    val totalAmount: Int,
    val status: String, // WAITING_CONFIRM, PREPARING, READY, COMPLETED
    val estimatedReadyTime: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val storeName: String,
    val storeWhatsapp: String
)
