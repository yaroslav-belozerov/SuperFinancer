package com.yaabelozerov.superfinancer.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.yaabelozerov.superfinancer.common.R
import kotlin.math.min

@Composable
fun RefreshIndicator(isLoading: Boolean, text: String, distanceFraction: Float, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .zIndex(1f)
            .height(distanceFraction.times(48.dp))
    ) {
        val textColor by animateColorAsState(
            MaterialTheme.colorScheme.onBackground.copy(
                min(distanceFraction, 0.85f)
            )
        )
        Text(
            text.let {
                if (it.isBlank() || isLoading) stringResource(R.string.refreshing)
                else "${stringResource(R.string.last_updated)} $it"
            }, color = textColor
        )
    }
}