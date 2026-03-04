package com.example.offlineaccounting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.offlineaccounting.ui.AccountingApp
import com.example.offlineaccounting.ui.AccountingViewModel
import com.example.offlineaccounting.ui.AccountingViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: AccountingViewModel by viewModels {
        AccountingViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountingApp(viewModel = viewModel)
        }
    }
}
