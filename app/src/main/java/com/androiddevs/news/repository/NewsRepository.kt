package com.androiddevs.news.repository

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.api.NewsApi
import com.androiddevs.news.api.RetrofitInstance
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.model.Article
import com.androiddevs.news.model.NewsResponse
import com.androiddevs.news.utility.Resource
import com.androiddevs.news.utility.ResourceDB
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
                if (Utility.hasInternetConnection(NewsApplication.instance)) {
                    val response = api.getBreakingNews()
                    if (response.isSuccessful) {
                        response.body()?.let { newsResponse ->
                            emit(Resource.Success(newsResponse))
                        }
                    } else {
                        emit(Resource.Error("Api response error: ${response.message()}"))
                    }
                } else {
                    emit(Resource.Error("No Internet connection"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Caught exception: ${e.toString()}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllNews(queryString: String, pageNumber: Int) : Flow<Resource<NewsResponse>> {
        return flow<Resource<NewsResponse>> {
            emit(Resource.Loading())
            try{
                if (Utility.hasInternetConnection(NewsApplication.instance)) {
                    val response = api.getAllNews(queryString, pageNumber)
                    if (response.isSuccessful) {
                        response.body()?.let { newsResponse ->
                            emit(Resource.Success(newsResponse))
                        }
                    } else {
                        emit(Resource.Error(response.message()))
                    }
                } else {
                    emit(Resource.Error("No Internet connection"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }


        //api.getAllNews(queryString, pageNumber)

    suspend fun insertArticle(article: Article) : Flow<ResourceDB<Article>> {
        return flow {
            try {
                db.getArticleDao().insertArticle(article)
                emit(ResourceDB.Success())
            } catch (e: SQLiteException) {
                emit(ResourceDB.Error("fail to insert in db: ${e.message}"))
            }
        }
    }
    suspend fun deleteArticle(article: Article) : Flow<ResourceDB<Article>> {
        return flow {
            try {
                db.getArticleDao().deleteArticle(article)
                emit(ResourceDB.Success())
            } catch (e: SQLiteException) {
                emit(ResourceDB.Error("fail to delete in db: ${e.message}"))
            }
        }
    }
    fun getSavedArticles() = db.getArticleDao().getSavedArticles()
}