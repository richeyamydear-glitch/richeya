package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SepotongBuahRepository(private val db: AppDatabase) {
    private val seedMutex = Mutex()

    val fruitMenu: Flow<List<FruitMenuEntity>> = db.fruitMenuDao().observeAll()
    val sauceOptions: Flow<List<SauceOptionEntity>> = db.sauceOptionDao().observeAll()
    val promoBundles: Flow<List<PromoBundleEntity>> = db.promoBundleDao().observeAll()
    val savedSauceMixes: Flow<List<SavedSauceMixEntity>> = db.savedSauceMixDao().observeAll()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().observeAll()
    val preOrders: Flow<List<PreOrderEntity>> = db.preOrderDao().observeAll()
    val customerProfile: Flow<CustomerProfileEntity?> = db.customerProfileDao().observeProfile()

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (db.fruitMenuDao().count() == 0) {
                db.fruitMenuDao().insertAll(SeedData.fruits)
            }
            if (db.sauceOptionDao().count() == 0) {
                db.sauceOptionDao().insertAll(SeedData.sauces)
            }
            if (db.promoBundleDao().count() == 0) {
                db.promoBundleDao().insertAll(SeedData.promos)
            }
            if (db.customerProfileDao().getProfileOnce() == null) {
                db.customerProfileDao().upsert(SeedData.defaultProfile)
            }
        }
    }

    suspend fun updateFruitMenu(fruit: FruitMenuEntity) {
        db.fruitMenuDao().update(fruit)
    }

    suspend fun addFruitMenu(fruit: FruitMenuEntity) {
        db.fruitMenuDao().insert(fruit)
    }

    suspend fun updateSauceOption(sauce: SauceOptionEntity) {
        db.sauceOptionDao().update(sauce)
    }

    suspend fun updatePromoBundle(promo: PromoBundleEntity) {
        db.promoBundleDao().update(promo)
    }

    suspend fun saveSauceMix(mix: SavedSauceMixEntity) {
        db.savedSauceMixDao().insert(mix)
    }

    suspend fun deleteSauceMix(id: Int) {
        db.savedSauceMixDao().deleteById(id)
    }

    suspend fun addToCart(item: CartItemEntity) {
        db.cartDao().insert(item)
    }

    suspend fun updateCartQuantity(id: Int, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().deleteById(id)
        } else {
            db.cartDao().updateQuantity(id, quantity)
        }
    }

    suspend fun removeCartItem(id: Int) {
        db.cartDao().deleteById(id)
    }

    suspend fun clearCart() {
        db.cartDao().clearAll()
    }

    suspend fun createPreOrder(order: PreOrderEntity): PreOrderEntity {
        val insertedId = db.preOrderDao().insert(order).toInt()
        db.cartDao().clearAll()
        return order.copy(id = insertedId)
    }

    suspend fun updatePreOrderStatus(id: Int, nextStatus: String) {
        db.preOrderDao().updateStatus(id, nextStatus)
    }

    suspend fun updateCustomerProfile(profile: CustomerProfileEntity) {
        db.customerProfileDao().upsert(profile)
    }
}
