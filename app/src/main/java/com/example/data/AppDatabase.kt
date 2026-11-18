package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FruitMenuEntity::class,
        SauceOptionEntity::class,
        PromoBundleEntity::class,
        SavedSauceMixEntity::class,
        CartItemEntity::class,
        PreOrderEntity::class,
        CustomerProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fruitMenuDao(): FruitMenuDao
    abstract fun sauceOptionDao(): SauceOptionDao
    abstract fun promoBundleDao(): PromoBundleDao
    abstract fun savedSauceMixDao(): SavedSauceMixDao
    abstract fun cartDao(): CartDao
    abstract fun preOrderDao(): PreOrderDao
    abstract fun customerProfileDao(): CustomerProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sepotong_buah_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
