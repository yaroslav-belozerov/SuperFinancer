package com.yaabelozerov.superfinancer.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

@Composable
fun AsyncImageWithPlaceholder(model: Any?, modifier: Modifier = Modifier, contentDescription: String? = null) {
    var visible by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onState = {
                visible = when (it) {
                    AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Error -> true
                    is AsyncImagePainter.State.Success -> false
                }
            },
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop)
        AnimatedVisibility(visible, enter = fadeIn(), exit = fadeOut()) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground.copy(0.5f)))
        }
    }
}