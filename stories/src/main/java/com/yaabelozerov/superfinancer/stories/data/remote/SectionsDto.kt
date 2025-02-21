package com.yaabelozerov.superfinancer.stories.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SectionsDto(
    val copyright: String,
    @SerialName("results") val list: List<SectionDto>,
)

@Serializable
internal data class SectionDto(
    val section: String,
    @SerialName("display_name") val name: String
)