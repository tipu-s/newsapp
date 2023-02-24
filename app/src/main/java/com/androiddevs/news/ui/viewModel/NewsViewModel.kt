package com.androiddevs.news.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.model.Article
import com.androiddevs.news.model.NewsResponse
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.utility.Resource
import com.androiddevs.news.utility.Utility.hasInternetConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    app: Application,
    private val repository: NewsRepository
): AndroidViewModel(app) {

    private val _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews

    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        _breakingNews.value = Resource.Loading()
        getBreakingNews("us")
    }

    private suspend fun safeSearchNews(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication())) {
                val response = repository.getAllNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("IO exception"))
                else -> searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun safeBreakingNews(countryCode: String) {
        _breakingNews.value = Resource.Loading()
        try {
            if (hasInternetConnection(getApplication())) {
                repository.getBreakingNews(countryCode, breakingNewsPage).collect {
                    _breakingNews.value = handleBreakingNewsResponse(it)
                }
            } else {
                _breakingNews.value = (Resource.Error("No Internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _breakingNews.value = (Resource.Error("IO exception"))
                else -> _breakingNews.value = (Resource.Error("Conversion error"))
            }
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNews(countryCode)
    }

    fun getAllNews(queryString: String) = viewModelScope.launch {
        safeSearchNews(queryString)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            breakingNewsPage++
            response.body()?.let {resultResponse ->
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        repository.insertArticle(article)
    }
}