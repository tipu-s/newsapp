package com.androiddevs.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.news.R
import com.androiddevs.news.adapter.NewsAdapter
import com.androiddevs.news.ui.NewsActivity
import com.androiddevs.news.ui.viewModel.NewsViewModel
import com.androiddevs.news.utility.Constants
import com.androiddevs.news.utility.Constants.Companion.TOTAL_QUERY_SIZE
import com.androiddevs.news.utility.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {
    private val TAG = "BreakingNewsFragment"

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPage = newsResponse.totalResults / TOTAL_QUERY_SIZE + 1
                        isLastPage = viewModel.breakingNewsPage == totalPage
                        if (isLastPage) {
                            //rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Error occured: $message", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressbar()
                }
            }
        })
    }

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItem = layoutManager.childCount
            val totalItem = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItem >= totalItem
            val isNotAtBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItem <= Constants.TOTAL_QUERY_SIZE || true

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem &&
                    isNotAtBegining && isTotalMoreThanVisible && isScrolling
//            Log.d("BreakingNewsFragment","isNotLoadingAndNotLastPage: $isNotLoadingAndNotLastPage" +
//                    " && isLoading: $isLoading" +
//                    " && isLastPage: $isLastPage" +
//                    " && isAtLastItem: $isAtLastItem" +
//                    " isNotAtBegining: $isNotAtBegining" +
//                    " isTotalMoreThanVisible: $isTotalMoreThanVisible" +
//                    " isScrolling: $isScrolling")
            Log.d("BreakingNewsFragment", "ShouldPaginate: $shouldPaginate")
            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "$TAG in setupRecyclerView")
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun hideProgressbar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressbar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
}