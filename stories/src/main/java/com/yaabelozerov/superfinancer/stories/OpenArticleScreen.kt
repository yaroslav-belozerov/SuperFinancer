package com.yaabelozerov.superfinancer.stories

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled") // Articles are not loaded fully with JS disabled
@Composable
fun OpenArticleScreen(url: String, onBack: () -> Unit, onAddToPost: (String) -> Unit) {
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
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
                val clipboardManager = LocalClipboardManager.current
                IconButton(onClick = {
                    clipboardManager.setText(buildAnnotatedString {
                        append(url)
                        addLink(LinkAnnotation.Url(url), 0, url.length)
                    })
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.copy_url))
                }
                val uriHandler = LocalUriHandler.current
                IconButton(onClick = { uriHandler.openUri(url) }) {
                    Icon(
                        Icons.AutoMirrored.Default.OpenInNew, contentDescription = stringResource(R.string.open_in_browser)
                    )
                }
                Spacer(Modifier.weight(1f))
                ExtendedFloatingActionButton(elevation = FloatingActionButtonDefaults.loweredElevation(),
                    onClick = {
                        onAddToPost(url)
                    }) {
                    Text(stringResource(R.string.add_to_post))
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}