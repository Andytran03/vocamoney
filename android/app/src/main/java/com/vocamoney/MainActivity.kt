package com.vocamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.vocamoney.core.designsystem.VocaMoneyTheme
import com.vocamoney.core.navigation.VocaMoneyNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = (application as VocaMoneyApp).database
        setContent {
            VocaMoneyTheme {
                val categoriesFlow = remember { database.categoryDao().observeAll() }
                val categories by categoriesFlow.collectAsState(initial = emptyList())
                VocaMoneyNavHost(categoryCount = categories.size)
            }
        }
    }
}
