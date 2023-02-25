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
            newsAdapter.differ.submitList(response)
        })

        viewModel.loadingBreakingNews.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                showProgressbar()
            } else {
                hideProgressbar()
            }
        })

        viewModel.errorBreakingNews.observe(viewLifecycleOwner, Observer {message->
            Toast.makeText(activity, "Error occurred: $message", Toast.LENGTH_SHORT).show()
        })
    }

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
            val itemCount = layoutManager.itemCount
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            val shouldPaginate = (itemCount - 1 == lastVisibleItemPosition) && isScrolling
            Log.d("BreakingNewsFragment", "ShouldPaginate: $shouldPaginate")
            if (shouldPaginate) {
                viewModel.loadBreakingNews()
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun hideProgressbar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressbar() {
        paginationProgressBar.visibility = View.VISIBLE
    }
}