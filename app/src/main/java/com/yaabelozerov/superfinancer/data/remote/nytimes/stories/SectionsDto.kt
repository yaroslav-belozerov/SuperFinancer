package com.yaabelozerov.superfinancer.data.remote.nytimes.stories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SectionsDto(
    val copyright: String,
    @SerialName("results") val list: List<SectionDto>,
)

@Serializable
data class SectionDto(
    val section: String,
    @SerialName("display_name") val name: String
)