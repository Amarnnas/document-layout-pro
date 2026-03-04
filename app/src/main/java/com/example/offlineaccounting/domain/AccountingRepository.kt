package com.example.offlineaccounting.domain

import com.example.offlineaccounting.data.AppDatabase
import com.example.offlineaccounting.data.Customer
import com.example.offlineaccounting.data.Invoice
import com.example.offlineaccounting.data.InvoiceItem
import com.example.offlineaccounting.data.Product
import com.example.offlineaccounting.data.StoreProfile

class AccountingRepository(private val db: AppDatabase) {
    val profile = db.storeProfileDao().observe()
    val customers = db.customerDao().observeAll()
    val products = db.productDao().observeAll()
    val invoices = db.invoiceDao().observeAll()

    suspend fun saveProfile(profile: StoreProfile) = db.storeProfileDao().upsert(profile)
    suspend fun addCustomer(customer: Customer) = db.customerDao().insert(customer)
    suspend fun addProduct(product: Product) = db.productDao().insert(product)
    suspend fun updateProduct(product: Product) = db.productDao().update(product)
    suspend fun addInvoice(invoice: Invoice, items: List<InvoiceItem>) = db.invoiceDao().createInvoice(invoice, items)
    suspend fun getInvoiceItems(invoiceId: Long) = db.invoiceDao().getItems(invoiceId)
}
