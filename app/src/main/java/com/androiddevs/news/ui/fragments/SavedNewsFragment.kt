package com.androiddevs.news.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.news.NewsApplication
import com.androiddevs.news.R
import com.androiddevs.news.adapter.NewsAdapter
import com.androiddevs.news.api.RetrofitInstance
import com.androiddevs.news.db.ArticleDatabase
import com.androiddevs.news.model.Article
import com.androiddevs.news.repository.NewsRepository
import com.androiddevs.news.ui.viewModel.SavedNewsViewModel
import com.androiddevs.news.ui.viewModel.SavedNewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: SavedNewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appService = (requireActivity().application as NewsApplication).appService
        viewModel = ViewModelProvider(this, appService.savedViewModelProviderFactory)
            .get(SavedNewsViewModel::class.java)

        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        lateinit var deletedArticle: Article
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deletedArticle = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(deletedArticle)
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.dbDeleteSuccess.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, "Deleted article", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertArticle(deletedArticle)
                }
                show()
            }
        })

        viewModel.dbInsertError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()
        })

        lifecycleScope.launch {
            viewModel.savedArticle.collectLatest {
                newsAdapter.differ.submitList(it)
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}