package com.yaabelozerov.superfinancer.stories.domain

internal data class Story(
    val title: String,
    val description: String?,
    val author: String,
    val source: String,
    val link: String,
    val photoUrl: String?,
    val sectionName: String,
    val date: String
)