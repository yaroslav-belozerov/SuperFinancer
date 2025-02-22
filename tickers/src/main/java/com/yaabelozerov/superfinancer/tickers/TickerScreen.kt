package com.yaabelozerov.superfinancer.tickers

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.superfinancer.common.components.Header
import com.yaabelozerov.superfinancer.common.util.smartRound
import com.yaabelozerov.superfinancer.common.util.toString
import com.yaabelozerov.superfinancer.tickers.ui.TickerScreenVM

@Composable
fun TickerScreen(symbol: String) {
    val viewModel = viewModel<TickerScreenVM>()
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchInfoForSymbol(symbol)
    }
    val pager = rememberPagerState {
        uiState.recommendations.size
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) { Text(symbol, style = MaterialTheme.typography.headlineLarge) }
        }
        item {
            Header("Recommendations", null)
            HorizontalPager(
                pager,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) { page ->
                uiState.recommendations[page].let {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val max =
                            maxOf(it.strongSell, it.sell, it.hold, it.buy, it.strongBuy).toFloat()
                        Text(it.date, fontStyle = FontStyle.Italic)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Strong Sell (${it.strongSell})", modifier = Modifier.weight(0.5f))
                            Row(
                                modifier = Modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (it.strongSell != 0) Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth(it.strongSell / max)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Sell (${it.sell})", modifier = Modifier.weight(0.5f))
                            Row(
                                modifier = Modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (it.sell != 0) Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth(it.sell / max)
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Hold (${it.hold})", modifier = Modifier.weight(0.5f))
                            Row(
                                modifier = Modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (it.hold != 0) Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth(it.hold / max)
                                        .background(MaterialTheme.colorScheme.surfaceDim)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Buy (${it.buy})", modifier = Modifier.weight(0.5f))
                            Row(
                                modifier = Modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (it.buy != 0) Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth(it.buy / max)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Strong Buy (${it.strongBuy})", modifier = Modifier.weight(0.5f))
                            Row(
                                modifier = Modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (it.strongBuy != 0) Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth(it.strongBuy / max)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Header("Surprise Earnings", null)
            Spacer(Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(uiState.earnings) { earn ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Column(
                                Modifier
                                    .height(128.dp)
                                    .width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(earn.actual.dp.times(50)))
                                Box(
                                    Modifier
                                        .size(32.dp, 8.dp)
                                        .background(Color.Red.copy(0.5f))
                                )
                            }
                            Column(
                                Modifier
                                    .height(128.dp)
                                    .width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(earn.estimate.dp.times(50)))
                                Box(
                                    Modifier
                                        .size(32.dp, 8.dp)
                                        .background(Color.Blue.copy(0.5f))
                                )
                            }
                        }
                        Text(
                            earn.surprisePercent.toString(2) + "%",
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}