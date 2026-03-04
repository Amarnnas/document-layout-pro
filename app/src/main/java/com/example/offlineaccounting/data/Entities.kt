package com.example.offlineaccounting.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class StoreProfile(
    @PrimaryKey val id: Int = 1,
    val storeName: String = "متجري",
    val phone: String = "",
    val address: String = "",
    val logoPath: String? = null,
    val invoiceTitle: String = "فاتورة",
    val primaryColor: Long = 0xFF1565C0,
    val accentColor: Long = 0xFF42A5F5,
    val columnsLayout: String = "name,qty,price,total"
)

@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String
)

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val unitPrice: Double,
    val stockQty: Double
)

@Entity
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long?,
    val createdAt: Long,
    val discount: Double,
    val taxPercent: Double,
    val notes: String,
    val finalTotal: Double,
    val pdfPath: String? = null,
    val imagePath: String? = null
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val lineTotal: Double
)
