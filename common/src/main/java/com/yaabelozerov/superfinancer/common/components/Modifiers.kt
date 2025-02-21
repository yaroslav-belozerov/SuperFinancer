package com.yaabelozerov.superfinancer.common.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// https://github.com/yaroslav-belozerov/kotlin-components/blob/main/composeApp/src/commonMain/kotlin/org/yaabelozerov/kmp_components/Modifiers.kt

fun Modifier.shimmerBackground(shape: Shape, color: Color): Modifier = composed {
    val transition = rememberInfiniteTransition()

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2000, easing = EaseInOut), RepeatMode.Restart
        ),
        label = "",
    )
    val shimmerColors = listOf(
        color.copy(alpha = 0.7f),
        color.copy(alpha = 0.0f),
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 200f, translateAnimation + 200f),
        tileMode = TileMode.Mirror,
    )
    return@composed this.background(brush, shape)
}

fun Modifier.horizontalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
) = composed {
    val color = edgeColor ?: MaterialTheme.colorScheme.surface
    val startFadingEdge by animateDpAsState(if (lazyListState.canScrollBackward) length else 0.dp)
    val endFadingEdge by animateDpAsState(if (lazyListState.canScrollForward) length else 0.dp)

    return@composed drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startX = 0f,
                endX = startFadingEdge.toPx(),
            ),
            size = Size(
                startFadingEdge.toPx(),
                this.size.height,
            ),
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startX = size.width - endFadingEdge.toPx(),
                endX = size.width,
            ),
            topLeft = Offset(x = size.width - endFadingEdge.toPx(), y = 0f),
        )
    }
}