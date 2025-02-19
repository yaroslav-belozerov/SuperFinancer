package com.yaabelozerov.superfinancer.stories.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.stories.domain.Story

@Composable
fun StoryCard(
    story: Story,
    onClick: () -> Unit,
    onClickSectionName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Box {
            story.photoUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        Column(
            modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                story.title,
                style = MaterialTheme.typography.displaySmall
            )
            story.description?.let {
                Text(it)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(selected = false,
                    onClick = { onClickSectionName(story.sectionName) },
                    label = { Text(story.sectionName) })
                Spacer(Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        story.author,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        story.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.75f)
                    )
                }
            }
        }
    }
}