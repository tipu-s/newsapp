package com.androiddevs.news

import android.app.Application
import android.content.Context
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.ui.viewModel.NewsViewModelProviderFactory
import com.androiddevs.news.ui.viewModel.SavedNewsViewModelProviderFactory

class AppService(private val context: Context) {
    private val repository = NewsRepository(ArticleDatabase(context.applicationContext))
    val viewModelProviderFactory = NewsViewModelProviderFactory(context as NewsApplication, repository)
    val savedViewModelProviderFactory = SavedNewsViewModelProviderFactory(repository)
}