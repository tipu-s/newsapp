package com.androiddevs.news.repository

import com.androiddevs.news.api.RetrofitInstance
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.model.Article
import com.androiddevs.news.model.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Flow<Response<NewsResponse>> {
        return flow {
            emit(RetrofitInstance.api.getBreakingNews())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllNews(queryString: String, pageNumber: Int) =
        RetrofitInstance.api.getAllNews(queryString, pageNumber)

    suspend fun insertArticle(article: Article) = db.getArticleDao().insertArticle(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
    fun getSavedArticles() = db.getArticleDao().getSavedArticles()
}