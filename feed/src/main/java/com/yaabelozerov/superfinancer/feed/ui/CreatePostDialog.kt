package com.yaabelozerov.superfinancer.feed.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButtonList
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import kotlinx.coroutines.launch

@Composable
fun CreatePostDialog(
    articleId: String?,
    onDismiss: () -> Unit,
    onCreate: (String, List<Pair<String, String>>) -> Unit,
) {
    var contents by remember { mutableStateOf("") }
    var images by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()

    CardDialog(title = "Post", onDismiss = {
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
        OutlinedTextField(contents, onValueChange = { contents = it }, shape = MaterialTheme.shapes.small)
        Button(onClick = {
            onCreate(contents, images.map { it to "" })
            onDismiss()
        }) { Text("Post") }
    }
}