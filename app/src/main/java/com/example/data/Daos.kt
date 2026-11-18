package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FruitMenuDao {
    @Query("SELECT * FROM fruit_menu ORDER BY id ASC")
    fun observeAll(): Flow<List<FruitMenuEntity>>

    @Query("SELECT COUNT(*) FROM fruit_menu")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FruitMenuEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FruitMenuEntity)

    @Update
    suspend fun update(item: FruitMenuEntity)
}

@Dao
interface SauceOptionDao {
    @Query("SELECT * FROM sauce_options ORDER BY id ASC")
    fun observeAll(): Flow<List<SauceOptionEntity>>

    @Query("SELECT COUNT(*) FROM sauce_options")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SauceOptionEntity>)

    @Update
    suspend fun update(item: SauceOptionEntity)
}

@Dao
interface PromoBundleDao {
    @Query("SELECT * FROM promo_bundles ORDER BY id ASC")
    fun observeAll(): Flow<List<PromoBundleEntity>>

    @Query("SELECT COUNT(*) FROM promo_bundles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PromoBundleEntity>)

    @Update
    suspend fun update(item: PromoBundleEntity)
}

@Dao
interface SavedSauceMixDao {
    @Query("SELECT * FROM saved_sauce_mixes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SavedSauceMixEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mix: SavedSauceMixEntity)

    @Query("DELETE FROM saved_sauce_mixes WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()
}

@Dao
interface PreOrderDao {
    @Query("SELECT * FROM pre_orders ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PreOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: PreOrderEntity): Long

    @Query("UPDATE pre_orders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}

@Dao
interface CustomerProfileDao {
    @Query("SELECT * FROM customer_profile WHERE id = 1")
    fun observeProfile(): Flow<CustomerProfileEntity?>

    @Query("SELECT * FROM customer_profile WHERE id = 1")
    suspend fun getProfileOnce(): CustomerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: CustomerProfileEntity)
}
