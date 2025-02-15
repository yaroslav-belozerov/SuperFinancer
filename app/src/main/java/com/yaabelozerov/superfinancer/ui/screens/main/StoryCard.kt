package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yaabelozerov.superfinancer.domain.model.Story

@Composable
fun LazyItemScope.StoryCard(story: Story, onClickSectionName: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .animateItem()
            .fillParentMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                FilterChip(selected = true,
                    onClick = {
                        onClickSectionName(story.sectionName)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    label = {
                        Text(story.sectionName)
                    })
            }
            Text(
                story.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            story.description?.let {
                Text(it)
            }
        }
    }
}