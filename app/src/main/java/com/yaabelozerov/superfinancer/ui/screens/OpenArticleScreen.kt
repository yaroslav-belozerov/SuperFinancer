package com.yaabelozerov.superfinancer.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SocialScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yaabelozerov.superfinancer.ui.navigation.SlideInVertically
import com.yaabelozerov.superfinancer.ui.navigation.bottomNavigate
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled") // Articles are not loaded fully with JS disabled
@Destination<RootGraph>(style = SlideInVertically::class)
@Composable
fun OpenArticleScreen(url: String, navigator: DestinationsNavigator) {
    val uriHandler = LocalUriHandler.current
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
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        }, update = {
            it.loadUrl(url)
        })
        Card(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                Box(
                    modifier = Modifier.weight(1f, false).clip(MaterialTheme.shapes.small).clickable {
                        uriHandler.openUri(url)
                    }
                ) {
                    Text(url, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                ExtendedFloatingActionButton(
                    elevation = FloatingActionButtonDefaults.loweredElevation(),
                    onClick = {
                        navigator.navigateUp()
                        navigator.bottomNavigate(
                            SocialScreenDestination(
                                addToPostArticleUrl = url
                            ), restore = false
                        )
                    }
                ) {
                    Text("Add to post")
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}