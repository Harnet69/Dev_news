package com.harnet.devnewsradar.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.adapter.ArticlesListAdapter
import com.harnet.devnewsradar.adapter.HistoryListAdapter
import com.harnet.devnewsradar.viewModel.ArticlesListViewModel
import com.harnet.devnewsradar.viewModel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    lateinit var historyListAdapter: HistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyListAdapter = HistoryListAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.refresh()

        history_list.apply {
            layoutManager = LinearLayoutManager(context)
            //Fix blinking RecyclerView
            historyListAdapter.setHasStableIds(true)
            adapter = historyListAdapter
        }

        // add separation line between items
        history_list.addItemDecoration(DividerItemDecoration(history_list.context, DividerItemDecoration.VERTICAL))

        observeViewModel()
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mArticles.observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                history_list.visibility = View.VISIBLE
                historyListAdapter.updateArticlesList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsArticleLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                listErrorHistory.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingProgressBarHistory.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    listErrorHistory.visibility = View.GONE
                    history_list.visibility = View.GONE
                }
            }
        })
    }

    // refresh history list after reading an article from it
    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}