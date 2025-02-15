package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.ui.viewmodel.TickerState
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue

@Composable
fun TickerRow(ticker: TickerState, modifier: Modifier = Modifier) = LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(ticker.map.entries.toList(), key = { it.key }) {
        TickerCard(it.key, it.value, Modifier.animateItem())
    }
}

@Composable
fun TickerCard(symbol: String, ticker: Ticker, modifier: Modifier = Modifier) {
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
                        text = ticker.value,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(ticker.currency)
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