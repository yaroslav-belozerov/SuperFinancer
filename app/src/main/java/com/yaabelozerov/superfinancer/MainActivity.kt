package com.yaabelozerov.superfinancer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.ui.theme.SuperFinancerTheme
import com.yaabelozerov.superfinancer.ui.viewmodel.MainVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MainVM = viewModel()
            val uiState by vm.state.collectAsState()
            LaunchedEffect(Unit) { vm.fetchForex() }
            SuperFinancerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn(contentPadding = innerPadding) {
                        item {
                            LazyRow {
                                item {
                                    uiState.forex.error?.let {
                                        Text(it.localizedMessage ?: it.message ?: it.stackTrace.contentToString(), color = MaterialTheme.colorScheme.error)
                                    }
                                }
                                items(uiState.forex.list, key = { it.symbol }) {
                                    Text(it.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}