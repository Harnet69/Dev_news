package com.harnet.devnews.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.harnet.devnews.R
import com.harnet.devnews.model.Article
import com.harnet.devnews.viewModel.ArticlesListViewModel

class ArticlesListFragment : Fragment() {
    private lateinit var viewModel: ArticlesListViewModel
    val articlesListAdapter = ArticlesListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ArticlesListViewModel::class.java)
        viewModel.refresh()


    }

}