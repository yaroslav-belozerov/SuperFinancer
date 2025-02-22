package com.yaabelozerov.superfinancer.feed.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.components.AsyncImageWithPlaceholder
import com.yaabelozerov.superfinancer.common.components.CardDialog
import com.yaabelozerov.superfinancer.common.components.PhotoPickerButtonList
import com.yaabelozerov.superfinancer.common.components.PhotoPickerImage
import com.yaabelozerov.superfinancer.feed.R
import com.yaabelozerov.superfinancer.feed.domain.PostStory
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CreatePostDialog(
    article: PostStory?,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>, List<String>) -> Unit,
) {
    var contents by remember { mutableStateOf("") }
    var images by remember { mutableStateOf(listOf<String>()) }
    var tags by remember { mutableStateOf(CommonModule.config.defaultPostTags.associateWith { false }) }
    var chooseTagsOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val maxSymbols = 300
    val enablePost by remember(contents, tags.values) { mutableStateOf(contents.isNotEmpty() && contents.length <= maxSymbols && tags.values.contains(true)) }

    CardDialog(title = stringResource(R.string.create_a_post), onDismiss = {
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
        OutlinedButton(onClick = {
            chooseTagsOpen = true
        }) {
            val cnt = tags.filter { it.value }.size
            Text("${stringResource(R.string.add_tags)}${if (cnt != 0) "  ($cnt)" else ""}")
        }
        OutlinedTextField(contents,
            onValueChange = { contents = it },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            minLines = 3,
            placeholder = {
                Text(stringResource(R.string.write_something))
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
            onCreate(contents, images, tags.filter { it.value }.keys.toList())
            onDismiss()
        }, enabled = enablePost, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.post)) }
        if (chooseTagsOpen) Dialog(onDismissRequest = { chooseTagsOpen = false }) {
            OutlinedCard {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var currentQuery by remember { mutableStateOf("") }
                    OutlinedTextField(currentQuery,
                        onValueChange = { currentQuery = it },
                        shape = MaterialTheme.shapes.small,
                        placeholder = { Text(stringResource(R.string.search_tags)) })
                    val foundTags = remember(currentQuery, tags.values) {
                        tags.filter { (it, _) ->
                            it.lowercase().filter { it.isLetter() }
                                .contains(currentQuery.lowercase().filter { it.isLetter() })
                        }.ifEmpty { tags }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        foundTags.forEach { (tag, isSelected) ->
                            FilterChip(isSelected,
                                onClick = { tags = tags.plus(tag to !isSelected) },
                                label = {
                                    Text(tag)
                                })
                        }
                    }
                }
            }
        }
    }
}