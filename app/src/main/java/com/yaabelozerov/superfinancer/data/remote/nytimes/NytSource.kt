package com.yaabelozerov.superfinancer.data.remote.nytimes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yaabelozerov.superfinancer.BuildConfig
import com.yaabelozerov.superfinancer.data.remote.Net
import com.yaabelozerov.superfinancer.data.remote.RateLimiter
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.SectionsDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoriesDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url

class NytSource(private val client: HttpClient = Net.Client) {
    suspend fun getLatestStories(section: String, limit: Int, offset: Int, token: String = BuildConfig.NYT_TOKEN): Result<StoriesDto> = runCatching {
        rateLimiter.acquire()
        println("Fetching stories $section $limit $offset")
        client.get {
            url("${BASE_URL}content/nyt/$section.json?limit=$limit&offset=$offset&api-key=$token")
        }.body()
    }

    suspend fun getSections(token: String = BuildConfig.NYT_TOKEN): Result<SectionsDto> = runCatching {
        rateLimiter.acquire()
        println("Fetching sections")
        client.get {
            url("${BASE_URL}content/section-list.json?api-key=$token")
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/news/v3/"
        private val rateLimiter = RateLimiter(12)
    }
}