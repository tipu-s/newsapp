package com.androiddevs.news.repository

import android.content.Context
import com.androiddevs.news.api.NewsApi
import com.androiddevs.news.api.RetrofitInstance
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.model.Article
import com.androiddevs.news.model.NewsResponse
import com.androiddevs.news.utility.Resource
import com.androiddevs.news.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class NewsRepository(
    val db: ArticleDatabase,
    val api: NewsApi
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Flow<Resource<NewsResponse>> {
        return flow<Resource<NewsResponse>> {
            emit(Resource.Loading())
            try{
                val response = api.getBreakingNews()
                if (response.isSuccessful) {
                    response.body()?.let { newsResponse ->
                        emit(Resource.Success(newsResponse))
                    }
                } else {
                    emit(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllNews(queryString: String, pageNumber: Int) =
        api.getAllNews(queryString, pageNumber)

    suspend fun insertArticle(article: Article) = db.getArticleDao().insertArticle(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
    fun getSavedArticles() = db.getArticleDao().getSavedArticles()
}