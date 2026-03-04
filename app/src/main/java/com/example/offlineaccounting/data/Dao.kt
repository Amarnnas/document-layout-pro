package com.example.offlineaccounting.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreProfileDao {
    @Query("SELECT * FROM StoreProfile WHERE id = 1")
    fun observe(): Flow<StoreProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: StoreProfile)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM Customer ORDER BY id DESC")
    fun observeAll(): Flow<List<Customer>>

    @Insert
    suspend fun insert(customer: Customer)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product ORDER BY id DESC")
    fun observeAll(): Flow<List<Product>>

    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM Invoice ORDER BY id DESC")
    fun observeAll(): Flow<List<Invoice>>

    @Insert
    suspend fun insert(invoice: Invoice): Long

    @Insert
    suspend fun insertItems(items: List<InvoiceItem>)

    @Query("SELECT * FROM InvoiceItem WHERE invoiceId = :invoiceId")
    suspend fun getItems(invoiceId: Long): List<InvoiceItem>

    @Transaction
    suspend fun createInvoice(invoice: Invoice, items: List<InvoiceItem>): Long {
        val id = insert(invoice)
        insertItems(items.map { it.copy(invoiceId = id) })
        return id
    }
}
