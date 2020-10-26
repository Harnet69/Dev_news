package com.harnet.devnewsradar.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.adapter.ArticlesListAdapter
import com.harnet.devnewsradar.viewModel.ArticlesListViewModel
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryFragment : Fragment() {
    private lateinit var viewModel: ArticlesListViewModel
    lateinit var historyListAdapter: ArticlesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyListAdapter = ArticlesListAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(ArticlesListViewModel::class.java)
        viewModel.refresh()

            history_list.apply {
                layoutManager = LinearLayoutManager(context)
                //Fix blinking RecyclerView
                historyListAdapter.setHasStableIds(true)
                adapter = historyListAdapter
            }
    }

}