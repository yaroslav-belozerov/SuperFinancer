package com.yaabelozerov.superfinancer.stories.domain

data class Story(
    val title: String,
    val description: String?,
    val author: String,
    val link: String,
    val photoUrl: String?,
    val sectionName: String,
    val date: String
)