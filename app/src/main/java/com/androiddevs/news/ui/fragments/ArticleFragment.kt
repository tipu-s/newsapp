package com.androiddevs.news.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.R
import com.androiddevs.news.model.Article
import com.androiddevs.news.ui.NewsActivity
import com.androiddevs.news.ui.viewModel.NewsViewModel
import com.androiddevs.news.ui.viewModel.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment: Fragment(R.layout.fragment_article) {
    lateinit var viewModel: SavedNewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appService = (requireActivity().application as NewsApplication).appService
        viewModel = ViewModelProvider(this, appService.savedViewModelProviderFactory)
            .get(SavedNewsViewModel::class.java)

        val article = arguments?.getSerializable("article") as Article?
        webView.apply {
            webViewClient = WebViewClient()
            article?.url?.let { loadUrl(it) }
        }

        fab.setOnClickListener {
            article?.let {
                viewModel.insertArticle(it)
            }
            Snackbar.make(view,"Article saved", Snackbar.LENGTH_SHORT).show()
        }
    }


}