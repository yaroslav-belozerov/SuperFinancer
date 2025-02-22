package com.yaabelozerov.superfinancer.tickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.components.LoadingBox
import com.yaabelozerov.superfinancer.tickers.ui.TickerCard
import com.yaabelozerov.superfinancer.tickers.ui.TickerRowVM

@Composable
fun TickerRow(onClick: (String) -> Unit, setRefresh: (Boolean, String) -> Unit, setOnRefreshCallback: (() -> Unit) -> Unit, modifier: Modifier = Modifier) {
    val viewModel = viewModel<TickerRowVM>()
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(uiState.isLoading, uiState.lastUpdated) {
        setRefresh(uiState.isLoading, uiState.lastUpdated)
    }
    LaunchedEffect(Unit) {
        setOnRefreshCallback {
            viewModel.refresh()
        }
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = !uiState.isLoading
    ) {
        if (!uiState.isLoading) items(uiState.map.entries.toList(), key = { it.key }) {
            TickerCard(it.key, it.value, Modifier.animateItem(), onClick)
        } else {
            items(3) {
                LoadingTickerCard(modifier.animateItem())
            }
        }
    }
}

@Composable
private fun LoadingTickerCard(modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            LoadingBox(48.dp, 48.dp, shape = CircleShape)
            Spacer(Modifier.height(8.dp))
            LoadingBox(96.dp, 32.dp)
            Spacer(Modifier.height(8.dp))
            LoadingBox(104.dp, 40.dp)
            Spacer(Modifier.height(4.dp))
        }
    }
}