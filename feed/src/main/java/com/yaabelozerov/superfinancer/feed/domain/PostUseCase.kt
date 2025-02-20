package com.yaabelozerov.superfinancer.feed.domain

import androidx.room.withTransaction
import com.yaabelozerov.superfinancer.feed.FeedModule
import com.yaabelozerov.superfinancer.feed.data.PostDao
import com.yaabelozerov.superfinancer.feed.data.PostDb
import com.yaabelozerov.superfinancer.feed.data.PostEntity
import com.yaabelozerov.superfinancer.feed.data.PostImageEntity
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDao
import com.yaabelozerov.superfinancer.stories.domain.Story
import kotlinx.coroutines.flow.map

class PostUseCase(
    private val postDb: PostDb = FeedModule.postDb,
    private val postDao: PostDao = FeedModule.postDao,
    private val storiesDao: StoriesDao = FeedModule.storiesDao
) {
    val postFlow = postDao.getAllPosts().map {
        it.map { (post, images) ->
            Post(
                id = post.id,
                contents = post.contents,
                images = images.map { img ->
                    PostImage(
                        path = img.path,
                        altText = img.altText
                    )
                },
                article = post.articleId?.let { id ->
                    storiesDao.getByUrl(id).also { println(it) }.run {
                        Story(
                            title = title,
                            description = abstract,
                            author = byline,
                            link = url,
                            photoUrl = imageUrl,
                            date = createdDate,
                            sectionName = sectionKey
                        )
                    }
                }
            )
        }
    }

    suspend fun createPost(contents: String, images: List<Pair<String, String>>, articleUrl: String?) {
        postDb.withTransaction {
            val postId = postDao.createPost(
                PostEntity(
                    id = 0,
                    contents = contents,
                    articleId = articleUrl
                )
            )
            postDao.createImageRecord(
                images.map {
                    PostImageEntity(
                        postId = postId,
                        path = it.first,
                        altText = it.second
                    )
                }
            )
        }
    }
}