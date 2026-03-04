package com.example.offlineaccounting.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StoreProfile::class, Customer::class, Product::class, Invoice::class, InvoiceItem::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeProfileDao(): StoreProfileDao
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offline-accounting.db"
                ).build().also { instance = it }
            }
    }
}
