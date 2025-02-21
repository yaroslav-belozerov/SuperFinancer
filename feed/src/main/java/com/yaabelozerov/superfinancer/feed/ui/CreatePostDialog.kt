package com.yaabelozerov.superfinancer.feed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.AsyncImageWithPlaceholder
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButtonList
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import com.yaabelozerov.superfinancer.feed.domain.PostStory
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
internal fun CreatePostDialog(
    article: PostStory?,
    onDismiss: () -> Unit,
    onCreate: (String, List<Pair<String, String>>) -> Unit,
) {
    var contents by remember { mutableStateOf("") }
    var images by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()
    val maxSymbols = 300
    val enablePost by remember(contents) { mutableStateOf(contents.isNotEmpty() && contents.length <= maxSymbols) }

    CardDialog(title = "Create a post", onDismiss = {
        scope.launch {
            images.forEach { CommonModule.mediaManager.removeMedia(it) }
            onDismiss()
        }
    }) {
        LazyRow(verticalAlignment = Alignment.CenterVertically) {
            items(images) {
                PhotoPickerImage(it) {
                    images = images.minus(it)
                }
                Spacer(Modifier.width(12.dp))
            }
            item {
                PhotoPickerButtonList { images = images.plus(it) }
            }
        }
        OutlinedTextField(contents,
            onValueChange = { contents = it },
            shape = MaterialTheme.shapes.small,
            minLines = 3,
            placeholder = {
                Text("Write something...")
            },
            supportingText = {
                Text(
                    "${contents.length}/$maxSymbols",
                    color = if (contents.length > maxSymbols) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                )
            })
        article?.let {
            EmbeddedArticleCard(it)
        }
        Button(onClick = {
            onCreate(contents, images.map { it to "" })
            onDismiss()
        }, enabled = enablePost, modifier = Modifier.fillMaxWidth()) { Text("Post") }
    }
}