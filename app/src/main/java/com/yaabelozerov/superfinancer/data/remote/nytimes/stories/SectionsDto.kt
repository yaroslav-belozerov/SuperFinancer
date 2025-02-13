package com.yaabelozerov.superfinancer.data.remote.nytimes.stories

import kotlinx.serialization.SerialName

data class SectionsDto(
    @SerialName("results") val list: List<SectionDto>,
    val copyright: String
)

data class SectionDto(
    val section: String,
    @SerialName("display_name") val name: String
)