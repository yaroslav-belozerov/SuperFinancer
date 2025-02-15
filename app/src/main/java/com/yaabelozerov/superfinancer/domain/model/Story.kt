package com.yaabelozerov.superfinancer.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

data class Story(
    val title: String,
    val description: String?,
    val author: String,
    val link: String,
    val photoUrl: String?,
    val sectionName: String,
    val date: String
)