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
import androidx.lifecycle.ViewModel
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
    private val repository: NewsRepository
): ViewModel() {
    private val _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews
    var breakingNewsPage = 1

    private val _searchNews = MutableLiveData<Resource<NewsResponse>>()
    val searchNews: LiveData<Resource<NewsResponse>> = _searchNews
    var searchNewsPage = 1

    init {
        _breakingNews.value = Resource.Loading()
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        repository.getBreakingNews(countryCode, breakingNewsPage++).collect {
            _breakingNews.value = it
        }
    }

    fun getAllNews(queryString: String) = viewModelScope.launch {
        repository.getAllNews(queryString, searchNewsPage++).collect {
            _searchNews.value = it
        }
    }
}