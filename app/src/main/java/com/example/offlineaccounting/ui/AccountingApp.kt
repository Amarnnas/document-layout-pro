package com.example.offlineaccounting.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlineaccounting.data.Product

@Composable
fun AccountingApp(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("لوحة", "الأصناف", "العملاء", "الفواتير", "الإعدادات")

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            when (selectedTab) {
                0 -> DashboardTab(state.status, state.invoices.size, state.products.size, state.customers.size)
                1 -> ProductsTab(viewModel)
                2 -> CustomersTab(viewModel)
                3 -> InvoicesTab(viewModel)
                else -> SettingsTab(viewModel)
            }
        }
    }
}

@Composable
private fun DashboardTab(status: String, invoices: Int, products: Int, customers: Int) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("نظام محاسبة أوفلاين", style = MaterialTheme.typography.headlineSmall)
        Text("الحالة: $status")
        Text("عدد الفواتير: $invoices")
        Text("عدد الأصناف: $products")
        Text("عدد العملاء: $customers")
    }
}

@Composable
private fun ProductsTab(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("إضافة صنف")
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") })
        OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("السعر") })
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("المخزون") })
        Button(onClick = {
            viewModel.addProduct(name, sku, price.toDoubleOrNull() ?: 0.0, stock.toDoubleOrNull() ?: 0.0)
            name = ""; sku = ""; price = ""; stock = ""
        }) { Text("حفظ الصنف") }

        LazyColumn {
            items(state.products) { product ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(product.name)
                        Text("${product.unitPrice} | ${product.stockQty}")
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomersTab(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("إضافة عميل")
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم العميل") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الهاتف") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("العنوان") })
        Button(onClick = {
            viewModel.addCustomer(name, phone, address)
            name = ""; phone = ""; address = ""
        }) { Text("حفظ العميل") }

        LazyColumn {
            items(state.customers) { customer ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(customer.name)
                        Text(customer.phone)
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoicesTab(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lines = remember { mutableStateListOf<Pair<Product, String>>() }
    var discount by remember { mutableStateOf("0") }
    var tax by remember { mutableStateOf("15") }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("إنشاء فاتورة")
        state.products.take(5).forEach { p ->
            var qty by remember(p.id) { mutableStateOf("1") }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(p.name, modifier = Modifier.weight(1f))
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("كمية") }, modifier = Modifier.weight(1f))
                Button(onClick = {
                    lines.add(p to qty)
                }) { Text("إضافة") }
            }
        }

        OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("خصم") })
        OutlinedTextField(value = tax, onValueChange = { tax = it }, label = { Text("ضريبة %") })
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("ملاحظات") })

        Button(onClick = {
            val draft = lines.map { it.first to (it.second.toDoubleOrNull() ?: 1.0) }
            viewModel.createInvoice(
                customerId = state.customers.firstOrNull()?.id,
                itemsDraft = draft,
                discount = discount.toDoubleOrNull() ?: 0.0,
                tax = tax.toDoubleOrNull() ?: 0.0,
                notes = notes
            )
            lines.clear()
        }) { Text("حفظ الفاتورة + PDF + صورة") }

        Text("آخر الفواتير")
        LazyColumn {
            items(state.invoices) { invoice ->
                Text("#${invoice.id} - ${invoice.finalTotal}")
            }
        }
    }
}

@Composable
private fun SettingsTab(viewModel: AccountingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var storeName by remember { mutableStateOf(state.profile.storeName) }
    var phone by remember { mutableStateOf(state.profile.phone) }
    var address by remember { mutableStateOf(state.profile.address) }
    var invoiceTitle by remember { mutableStateOf(state.profile.invoiceTitle) }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تخصيص المتجر والفاتورة")
        OutlinedTextField(value = storeName, onValueChange = { storeName = it }, label = { Text("اسم المتجر") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الهاتف") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("العنوان") })
        OutlinedTextField(value = invoiceTitle, onValueChange = { invoiceTitle = it }, label = { Text("عنوان الفاتورة") })
        Button(onClick = {
            viewModel.saveProfile(
                state.profile.copy(
                    storeName = storeName,
                    phone = phone,
                    address = address,
                    invoiceTitle = invoiceTitle
                )
            )
        }) { Text("حفظ التخصيص") }

        Text("ملاحظة: يمكن لاحقاً ربط زر لاختيار شعار المتجر من معرض الصور.")
    }
}
