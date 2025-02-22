package com.yaabelozerov.superfinancer.feed.domain

import androidx.room.withTransaction
import com.yaabelozerov.superfinancer.feed.FeedModule
import com.yaabelozerov.superfinancer.feed.data.PostDao
import com.yaabelozerov.superfinancer.feed.data.PostDb
import com.yaabelozerov.superfinancer.feed.data.PostEntity
import com.yaabelozerov.superfinancer.feed.data.PostImageEntity
import com.yaabelozerov.superfinancer.stories.StoriesModule
import com.yaabelozerov.superfinancer.stories.StoriesToPostAdapter
import kotlinx.coroutines.flow.map

private suspend fun Map<PostEntity, List<PostImageEntity>>.toDomain(storiesToPostAdapter: StoriesToPostAdapter): List<Post> {
    return map { (post, images) ->
        Post(
            id = post.id,
            contents = post.contents,
            images = images.map { it.path },
            article = post.articleId?.let { id ->
                storiesToPostAdapter.getByUrl(id).run {
                    PostStory(
                        title = first,
                        imageUrl = second,
                        url = third
                    )
                }
            },
            tags = post.tags.split(";").filter { it.isNotEmpty() },
            isFavourite = post.isFavorite
        )
    }
}

internal class PostUseCase(
    private val postDb: PostDb = FeedModule.postDb,
    private val postDao: PostDao = FeedModule.postDao,
    private val storiesToPostAdapter: StoriesToPostAdapter = StoriesModule.postAdapter
) {
    val postFlow = postDao.getAllPosts().map { it.toDomain(storiesToPostAdapter) }
    val favouriteFlow = postDao.getAllFavourites().map { it.toDomain(storiesToPostAdapter) }

    suspend fun createPost(contents: String, images: List<String>, articleUrl: String?, tags: List<String>) {
        postDb.withTransaction {
            val postId = postDao.createPost(
                PostEntity(
                    id = 0,
                    contents = contents,
                    articleId = articleUrl,
                    tags = tags.joinToString(";")
                )
            )
            postDao.createImageRecords(images.map { PostImageEntity(it, postId) })
        }
    }

    suspend fun switchFavourite(it: Post) {
        postDao.setFavourite(it.id, !it.isFavourite)
    }
}