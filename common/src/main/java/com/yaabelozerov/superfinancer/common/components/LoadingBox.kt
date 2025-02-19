package com.yaabelozerov.superfinancer.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Composable
fun LoadingBox(width: Dp, height: Dp, shape: Shape = MaterialTheme.shapes.medium) = Box(
    modifier = Modifier
        .size(width, height)
        .shimmerBackground(shape)
)

