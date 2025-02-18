package com.yaabelozerov.superfinancer.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun Double.toString(precision: Int) = "%.${precision}f".format(this)

fun Double.smartRound(precision: Int) = when {
    (this > 0.0) && (this < 1.0) -> "<1"
    else -> toString(precision)
}
fun Float.smartRound(precision: Int) = toDouble().smartRound(precision)

fun LocalDateTime.format(): String {
    val time = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
    val date = dayOfMonth.toString().padStart(2, '0') + "/" + monthValue.toString()
        .padStart(2, '0') + "/" + year
    val daysBetween = ChronoUnit.DAYS.between(LocalDateTime.now(), this)
    val formattedDate = when (daysBetween) {
        -1L -> "Yesterday"
        0L -> "Today"
        1L -> "Tomorrow"
        else -> date
    }
    return "$formattedDate at $time"
}

@Composable
fun LoadingBox(width: Dp, height: Dp, shape: Shape = MaterialTheme.shapes.medium) = Box(
    modifier = Modifier
        .size(width, height)
        .shimmerBackground(shape)
)

fun Modifier.shimmerBackground(shape: Shape): Modifier = composed {
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
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.0f),
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 200f, translateAnimation + 200f),
        tileMode = TileMode.Mirror,
    )
    return@composed this.then(background(brush, shape))
}
