package com.yaabelozerov.superfinancer.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SocialScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yaabelozerov.superfinancer.ui.navigation.SlideInVertically
import com.yaabelozerov.superfinancer.ui.navigation.bottomNavigate
import kotlinx.coroutines.delay

@Destination<RootGraph>(style = SlideInVertically::class)
@Composable
fun OpenArticleScreen(url: String, navigator: DestinationsNavigator) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            TextButton(onClick = {
                navigator.navigateUp()
                navigator.bottomNavigate(
                    SocialScreenDestination(
                        addToPostArticleUrl = url
                    )
                )
            }) {
                Text("Add to post")
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
        var webviewLoaded by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(200)
            webviewLoaded = true
        }
        AnimatedVisibility(webviewLoaded, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AndroidView(factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }
                }, update = {
                    it.loadUrl(url)
                })
            }
        }
    }
}