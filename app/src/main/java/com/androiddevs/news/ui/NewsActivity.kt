package com.androiddevs.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.R
import com.androiddevs.news.api.RetrofitInstance
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.ui.viewModel.NewsViewModel
import com.androiddevs.news.ui.viewModel.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val appService = (application as NewsApplication).appService
        viewModel = ViewModelProvider(this, appService.viewModelProviderFactory).get(NewsViewModel::class.java)

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}
