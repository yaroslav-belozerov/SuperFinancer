package com.yaabelozerov.superfinancer.ui.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.ui.viewmodel.SectionState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SectionList(sections: SectionState, onSetSection: (Section?) -> Unit, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(true, onClick = {
                isExpanded = !isExpanded
            }, label = {
                Text(
                    if (isExpanded) "Collapse" else "Expand",
                    color = MaterialTheme.colorScheme.primary
                )
            }, trailingIcon = {
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            })
            sections.selected?.let {
                FilterChip(false, onClick = {
                    onSetSection(null)
                }, label = {
                    Text(it.name, color = MaterialTheme.colorScheme.primary)
                }, trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                })
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(isExpanded) { expandSectionList ->
            if (expandSectionList) FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sections.list.forEach {
                    val selected = it.key == sections.selected?.key
                    FilterChip(selected, onClick = {
                        onSetSection(it)
                    }, label = { Text(it.name) }, leadingIcon = if (selected) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    } else null)
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sections.list) {
                        val selected = it.key == sections.selected?.key
                        FilterChip(selected, onClick = {
                            onSetSection(it)
                        }, label = { Text(it.name) })
                    }
                }
            }
        }
    }
}