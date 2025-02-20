package com.yaabelozerov.superfinancer.common.components

import coil3.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.common.CommonModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhotoPickerButtonList(onPick: (List<String>) -> Unit) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uriList ->
            scope.launch {
                val lst = mutableListOf<String>()
                uriList.forEach {
                    CommonModule.mediaManager.importMedia(it) { lst.add(it) }
                }
                onPick(lst)
            }
        }
    IconButton(onClick = {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }) {
        Icon(Icons.Default.ImageSearch, contentDescription = null)
    }
}

@Composable
fun PhotoPickerButton(onPick: (String) -> Unit) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { uriNotNull ->
                scope.launch {
                    CommonModule.mediaManager.importMedia(uriNotNull) {
                        onPick(it)
                    }
                }
            }
        }
    IconButton(onClick = {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }) {
        Icon(Icons.Default.ImageSearch, contentDescription = null)
    }
}

@Composable
fun PhotoPickerImage(path: String, onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    AsyncImage(model = path,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(192.dp)
            .aspectRatio(1.5f)
            .clip(
                MaterialTheme.shapes.medium
            )
            .clickable {
                scope.launch {
                    CommonModule.mediaManager.removeMedia(path)
                    onDelete()
                }
            })
}