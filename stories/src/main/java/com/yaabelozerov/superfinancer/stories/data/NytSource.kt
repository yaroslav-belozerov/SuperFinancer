package com.yaabelozerov.superfinancer.stories.data

import com.yaabelozerov.superfinancer.common.BuildConfig
import com.yaabelozerov.superfinancer.common.remote.Net
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url

class NytSource(private val client: HttpClient = Net.Client) {
    suspend fun getLatestStories(
        section: String,
        limit: Int,
        offset: Int,
        token: String = BuildConfig.NYT_TOKEN,
    ): Result<StoriesDto> = runCatching {
        println("getLatestStories")
        client.get {
            url("${BASE_URL}content/nyt/$section.json?limit=$limit&offset=$offset&api-key=$token")
        }.body()
    }

    suspend fun getSections(token: String = BuildConfig.NYT_TOKEN): Result<SectionsDto> =
        runCatching {
            println("getSections")
            client.get {
                url("${BASE_URL}content/section-list.json?api-key=$token")
            }.body()
        }

    suspend fun searchStories(
        query: String,
        token: String = BuildConfig.NYT_TOKEN,
    ): Result<StoriesSearchDto> = runCatching {
        client.get {
            url("${SEARCH_BASE_URL}articlesearch.json?q=$query&api-key=$token")
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/news/v3/"
        private const val SEARCH_BASE_URL = "https://api.nytimes.com/svc/search/v2/"
    }
}