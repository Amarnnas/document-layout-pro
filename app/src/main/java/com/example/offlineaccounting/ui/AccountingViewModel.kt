package com.example.offlineaccounting.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.offlineaccounting.data.AppDatabase
import com.example.offlineaccounting.data.Customer
import com.example.offlineaccounting.data.Invoice
import com.example.offlineaccounting.data.InvoiceItem
import com.example.offlineaccounting.data.Product
import com.example.offlineaccounting.data.StoreProfile
import com.example.offlineaccounting.domain.AccountingRepository
import com.example.offlineaccounting.utils.InvoiceExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccountingUiState(
    val profile: StoreProfile = StoreProfile(),
    val customers: List<Customer> = emptyList(),
    val products: List<Product> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val status: String = "جاهز"
)

class AccountingViewModel(
    private val appContext: Context,
    private val repo: AccountingRepository
) : ViewModel() {

    private val status = MutableStateFlow("جاهز")

    val uiState: StateFlow<AccountingUiState> = combine(
        repo.profile,
        repo.customers,
        repo.products,
        repo.invoices,
        status
    ) { profile, customers, products, invoices, statusMessage ->
        AccountingUiState(
            profile = profile ?: StoreProfile(),
            customers = customers,
            products = products,
            invoices = invoices,
            status = statusMessage
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountingUiState())

    init {
        viewModelScope.launch {
            repo.saveProfile(StoreProfile())
        }
    }

    fun saveProfile(profile: StoreProfile) {
        viewModelScope.launch {
            repo.saveProfile(profile)
            status.value = "تم حفظ إعدادات المتجر والفاتورة"
        }
    }

    fun addCustomer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            repo.addCustomer(Customer(name = name, phone = phone, address = address))
            status.value = "تمت إضافة العميل"
        }
    }

    fun addProduct(name: String, sku: String, unitPrice: Double, stock: Double) {
        viewModelScope.launch {
            repo.addProduct(Product(name = name, sku = sku, unitPrice = unitPrice, stockQty = stock))
            status.value = "تمت إضافة الصنف"
        }
    }

    fun createInvoice(customerId: Long?, itemsDraft: List<Pair<Product, Double>>, discount: Double, tax: Double, notes: String) {
        viewModelScope.launch {
            val subtotal = itemsDraft.sumOf { it.first.unitPrice * it.second }
            val taxAmount = subtotal * (tax / 100)
            val total = subtotal + taxAmount - discount
            val invoice = Invoice(
                customerId = customerId,
                createdAt = System.currentTimeMillis(),
                discount = discount,
                taxPercent = tax,
                notes = notes,
                finalTotal = total
            )

            val items = itemsDraft.map {
                InvoiceItem(
                    invoiceId = 0,
                    productName = it.first.name,
                    quantity = it.second,
                    unitPrice = it.first.unitPrice,
                    lineTotal = it.first.unitPrice * it.second
                )
            }

            val invoiceId = repo.addInvoice(invoice, items)
            val saved = invoice.copy(id = invoiceId)
            val savedItems = repo.getInvoiceItems(invoiceId)
            val profile = uiState.value.profile

            val pdfPath = InvoiceExporter.exportPdf(appContext, profile, saved, savedItems)
            val imagePath = InvoiceExporter.exportImage(appContext, profile, saved, savedItems)
            status.value = "تم إنشاء الفاتورة. PDF: $pdfPath | PNG: $imagePath"
        }
    }
}

class AccountingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.get(context)
        val repo = AccountingRepository(db)
        @Suppress("UNCHECKED_CAST")
        return AccountingViewModel(context, repo) as T
    }
}
