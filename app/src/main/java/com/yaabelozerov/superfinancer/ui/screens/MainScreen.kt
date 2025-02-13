package com.yaabelozerov.superfinancer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.ui.viewmodel.MainVM

@Composable
fun MainScreen(contentPadding: PaddingValues, viewModel: MainVM = viewModel()) {
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchTickers() }
    LazyColumn(contentPadding = contentPadding) {
        item {
            uiState.ticker.error?.let {
                var showDetails by remember { mutableStateOf(false) }
                Card(
                    onClick = { showDetails = !showDetails },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).animateItem()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 16.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Text("Some tickers failed to load")
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { showDetails = !showDetails }) {
                            Icon(if (showDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                    AnimatedVisibility(showDetails) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                            Text(it.localizedMessage ?: it.message ?: it.stackTrace.contentToString())
                        }
                    }
                }
            }
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.ticker.map.entries.toList(), key = { it.key }) {
                    Card(modifier = Modifier.animateItem()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(it.value.name, fontWeight = FontWeight.Bold)
                            Text(it.value.run { "$value $currency" })
                        }
                    }
                }
            }
        }
    }
}