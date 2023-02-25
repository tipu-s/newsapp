package com.androiddevs.news.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.model.Article
import com.androiddevs.news.model.NewsResponse
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.utility.Constants.Companion.TOTAL_QUERY_SIZE
import com.androiddevs.news.utility.Resource
import com.androiddevs.news.utility.Utility.hasInternetConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    private val repository: NewsRepository
): ViewModel() {
    private val TAG = "NewsViewModel"

    private val _breakingNews = MutableLiveData<List<Article>>(emptyList())
    val breakingNews: LiveData<List<Article>> = _breakingNews

    private val _loadingBreakingNews = MutableLiveData<Boolean>()
    val loadingBreakingNews: LiveData<Boolean> = _loadingBreakingNews

    private val _errorBreakingNews = MutableLiveData<String>()
    val errorBreakingNews: LiveData<String> = _errorBreakingNews

    var breakingNewsPage = 1
    var isBreakingNewsLastPage = false

    private val _searchNews = MutableLiveData<List<Article>>(emptyList())
    val searchNews: LiveData<List<Article>> = _searchNews

    private val _loadingSearchNews = MutableLiveData<Boolean>()
    val loadingSearchNews: LiveData<Boolean> = _loadingSearchNews

    private val _errorSearchNews = MutableLiveData<String>()
    val errorSearchNews: LiveData<String> = _errorSearchNews

    var searchNewsPage = 1
    var isSearchLastPage = false


    init {
        getBreakingNews("us")
    }

    fun loadBreakingNews() {
        if (!isBreakingNewsLastPage) {
            getBreakingNews("us")
        }
    }

    fun loadSearchNews(queryString: String) {
        if (!isSearchLastPage) {
            getSearchedNews(queryString)
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        repository.getBreakingNews(countryCode, breakingNewsPage++).collect {newsResponse->
            when(newsResponse) {
                is Resource.Success -> {
                    val fetchNews = newsResponse.data?.articles
                    _breakingNews.value = fetchNews
                    _loadingBreakingNews.value = false
                    isBreakingNewsLastPage = fetchNews!!.size < TOTAL_QUERY_SIZE
                }
                is Resource.Loading -> {
                    _loadingBreakingNews.value = true
                }
                is Resource.Error -> {
                    _errorBreakingNews.value = newsResponse.message
                    _loadingBreakingNews.value = false
                }
            }
        }
    }

    fun getSearchedNews(queryString: String) = viewModelScope.launch {
        repository.getAllNews(queryString, searchNewsPage++).collect {newsResponse->
            when(newsResponse) {
                is Resource.Success -> {
                    val fetchNews = newsResponse.data?.articles
                    _searchNews.value = fetchNews
                    _loadingSearchNews.value = false

                    isSearchLastPage = fetchNews!!.size < TOTAL_QUERY_SIZE
                }
                is Resource.Loading -> {
                    _loadingSearchNews.value = true
                }
                is Resource.Error -> {
                    _errorSearchNews.value = newsResponse.message
                    _loadingSearchNews.value = false
                }
            }
        }
    }
}