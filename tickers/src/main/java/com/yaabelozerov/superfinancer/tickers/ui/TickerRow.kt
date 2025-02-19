package com.yaabelozerov.superfinancer.tickers.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.common.components.LoadingBox
import com.yaabelozerov.superfinancer.tickers.domain.Ticker
import java.math.RoundingMode
import kotlin.math.absoluteValue

@Composable
fun TickerRow(ticker: TickerUiState, modifier: Modifier = Modifier) = LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    userScrollEnabled = !ticker.isLoading
) {
    if (!ticker.isLoading) items(ticker.map.entries.toList(), key = { it.key }) {
        TickerCard(it.key, it.value, Modifier.animateItem())
    } else {
        items(3) {
            LoadingTickerCard(modifier.animateItem())
        }
    }
}

@Composable
private fun TickerCard(symbol: String, ticker: Ticker, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(Modifier.align(Alignment.TopStart)) {
                AsyncImage(
                    model = ticker.logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(48.dp)
                        .clip(
                            CircleShape
                        )
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = ticker.value, style = MaterialTheme.typography.headlineLarge
                    )
                }
                Text(
                    ticker.name, color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                )
                Text("$$symbol", color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(start = 56.dp)
            ) {
                val percent = ticker.changePercent
                val icon = when {
                    percent == null -> Icons.Default.HorizontalRule to MaterialTheme.colorScheme.onBackground.copy(
                        0.5f
                    )

                    percent > 0 -> Icons.AutoMirrored.Default.TrendingUp to MaterialTheme.colorScheme.primary
                    percent < 0 -> Icons.AutoMirrored.Default.TrendingDown to MaterialTheme.colorScheme.error
                    else -> Icons.Default.HorizontalRule to MaterialTheme.colorScheme.onBackground.copy(
                        0.5f
                    )
                }
                Icon(icon.first, contentDescription = null, tint = icon.second)
                ticker.changePercent?.takeIf { it != 0.0 }?.let {
                    Text(
                        it.absoluteValue.toBigDecimal().setScale(1, RoundingMode.UP)
                            .stripTrailingZeros().toPlainString() + "%", color = icon.second
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingTickerCard(modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            LoadingBox(48.dp, 48.dp, CircleShape)
            Spacer(Modifier.height(8.dp))
            LoadingBox(96.dp, 32.dp)
            Spacer(Modifier.height(8.dp))
            LoadingBox(104.dp, 40.dp)
            Spacer(Modifier.height(4.dp))
        }
    }
}