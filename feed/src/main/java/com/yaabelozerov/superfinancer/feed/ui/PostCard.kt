package com.yaabelozerov.superfinancer.feed.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaabelozerov.superfinancer.common.components.AsyncImageWithPlaceholder
import com.yaabelozerov.superfinancer.feed.R
import com.yaabelozerov.superfinancer.feed.domain.Post
import com.yaabelozerov.superfinancer.feed.domain.PostStory

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PostCard(it: Post, onClickFavourite: () -> Unit, onClickArticle: (String) -> Unit) {
    Card(modifier = Modifier.animateContentSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (it.images.isNotEmpty()) FlowRow(
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                it.images.forEach {
                    AsyncImageWithPlaceholder(
                        it, modifier = Modifier
                            .sizeIn(
                                minWidth = 48.dp,
                                minHeight = 48.dp,
                                maxWidth = 96.dp,
                                maxHeight = 192.dp
                            )
                            .weight(1f), contentDescription = null
                    )
                }
            }
            var showMoreButton by remember { mutableStateOf(false) }
            var isExpanded by remember { mutableStateOf(false) }
            Text(
                it.contents,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { res ->
                    showMoreButton = res.hasVisualOverflow || res.lineCount > 3
                },
                maxLines = if (isExpanded) Int.MAX_VALUE else 3
            )
            if (showMoreButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(if (isExpanded) stringResource(R.string.show_less) else stringResource(
                            R.string.show_more
                        )
                        )
                    }
                }
            }
            it.article?.let {
                EmbeddedArticleCard(it) { onClickArticle(it.url) }
            }
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onClickFavourite() }
                        .padding(4.dp),
                    contentDescription = null,
                    tint = if (it.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                it.tags.forEach {
                    OutlinedCard(shape = MaterialTheme.shapes.large, modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            it,
                            modifier = Modifier.padding(
                                horizontal = 8.dp, vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}