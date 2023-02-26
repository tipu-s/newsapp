package com.androiddevs.news.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.news.model.Article
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.utility.ResourceDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedNewsViewModel(
    private val repository: NewsRepository
): ViewModel() {
    private val _savedArticle = MutableStateFlow<List<Article>>(emptyList())
    val savedArticle: StateFlow<List<Article>> = _savedArticle

    private val _dbInsertSuccess = MutableLiveData<Boolean>()
    val dbInsertSuccess: LiveData<Boolean> = _dbInsertSuccess

    private val _dbInsertError = MutableLiveData<String>()
    val dbInsertError: LiveData<String> = _dbInsertError

    private val _dbDeleteSuccess = MutableLiveData<Boolean>()
    val dbDeleteSuccess: LiveData<Boolean> = _dbDeleteSuccess

    private val _dbDeleteError = MutableLiveData<String>()
    val dbDeleteError: LiveData<String> = _dbDeleteError

    init {
        getSavedArticles()
    }

    private fun getSavedArticles() = viewModelScope.launch {
        repository.getSavedArticles().collectLatest {
            _savedArticle.value = it
        }
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        repository.insertArticle(article).collect { insertResponse ->
            when (insertResponse) {
                is ResourceDB.Success -> {
                    _dbInsertSuccess.value = true
                }
                is ResourceDB.Error -> {
                    _dbInsertError.value = insertResponse.message
                }
            }
        }
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article).collect { insertResponse ->
            when (insertResponse) {
                is ResourceDB.Success -> {
                    _dbDeleteSuccess.value = true
                }
                is ResourceDB.Error -> {
                    _dbDeleteError.value = insertResponse.message
                }
            }
        }
    }
}