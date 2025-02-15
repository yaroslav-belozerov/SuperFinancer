package com.yaabelozerov.superfinancer.data.remote.nytimes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.yaabelozerov.superfinancer.BuildConfig
import com.yaabelozerov.superfinancer.data.remote.Net
import com.yaabelozerov.superfinancer.data.remote.RateLimiter
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.NytStoryPagingSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.SectionsDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoriesDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingDefaults
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingDefaults.SECTION
import com.yaabelozerov.superfinancer.domain.model.Story
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NytSource(private val client: HttpClient = Net.Client) {
    suspend fun getLatestStories(section: String, limit: Int, offset: Int, token: String = BuildConfig.NYT_TOKEN): Result<StoriesDto> = runCatching {
        rateLimiter.acquire()
        client.get {
            url("${BASE_URL}content/nyt/$section.json?limit=$limit&offset=$offset&api-key=$token")
        }.body()
    }

    suspend fun getSections(token: String = BuildConfig.NYT_TOKEN): Result<SectionsDto> = runCatching {
        rateLimiter.acquire()
        client.get {
            url("${BASE_URL}content/section-list.json?api-key=$token")
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/news/v3/"
        private val rateLimiter = RateLimiter(0.2)
    }
}