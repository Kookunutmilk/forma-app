package com.forma.app.data.repository

import com.forma.app.data.catalog.ArticleCatalog
import com.forma.app.data.local.dao.LearnDao
import com.forma.app.data.local.entity.ArticleStateEntity
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.remote.FormaCloudStore
import com.forma.app.domain.model.Article
import com.forma.app.domain.model.ArticleCategory
import com.forma.app.domain.repository.LearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearnRepositoryImpl @Inject constructor(
    private val dao: LearnDao,
    private val cloud: FormaCloudStore,
    private val sync: CloudSyncManager,
) : LearnRepository {

    private val withState: Flow<List<Article>> = dao.observeStates().map { states ->
        val saved = states.filter { it.saved }.map { it.articleId }.toSet()
        ArticleCatalog.all.map { article -> article.copy(saved = article.id in saved) }
    }

    override fun articles(category: ArticleCategory?): Flow<List<Article>> =
        withState.map { list ->
            if (category == null) list else list.filter { it.category == category }
        }

    override fun article(id: String): Flow<Article?> =
        withState.map { list -> list.firstOrNull { it.id == id } }

    override fun recommended(): Flow<List<Article>> =
        withState.map { list -> list.filterNot { it.featured }.take(6) }

    override suspend fun toggleSaved(id: String) {
        val current = dao.state(id)?.saved ?: false
        val state = ArticleStateEntity(articleId = id, saved = !current)
        dao.upsert(state)
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.upsertArticleState(uid, state) }
    }
}
