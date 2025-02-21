package com.yaabelozerov.superfinancer.stories

class StoriesToPostAdapter {
    private val dao by lazy {
        StoriesModule.storyCacheDao
    }

    suspend fun getByUrl(url: String): Triple<String, String?, String> {
        return dao.getByUrl(
            url
        ).run {
            Triple(title, imageUrl, url)
        }
    }

    suspend fun purgeCache(allUsedArticleIds: List<String>) = dao.purgeCache(allUsedArticleIds)

    suspend fun getCachedStoryByUrl(url: String) = dao.getByUrl(url).run {
        Triple(title, imageUrl, url)
    }
}