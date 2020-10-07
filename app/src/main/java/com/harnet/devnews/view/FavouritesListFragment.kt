package com.harnet.devnews.view

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnews.R
import com.harnet.devnews.viewModel.ArticlesListViewModel
import com.harnet.devnews.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.favourites_list_fragment.*
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.android.synthetic.main.fragment_articles_list.listError_TextView
import kotlinx.android.synthetic.main.fragment_articles_list.loadingView_ProgressBar
import kotlinx.android.synthetic.main.fragment_articles_list.refreshLayout

class FavouritesListFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesListFragment()
    }

    private lateinit var viewModel: FavouritesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.favourites_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavouritesListViewModel::class.java)
        context?.let { viewModel.refresh(it) }

//        articles_list.apply {
//            layoutManager = LinearLayoutManager(context)
////            adapter = articlesListAdapter
//        }

        // Swiper refresh listener(screen refreshing process)
        refreshLayout.setOnRefreshListener {
            favourites_list.visibility = View.GONE
            listError_TextView.visibility = View.GONE
            loadingView_ProgressBar.visibility = View.VISIBLE
            context?.let { viewModel.refresh(it) }
            refreshLayout.isRefreshing = false // disappears little spinner on the top

        }

//        context?.let { viewModel.refresh(context!!) }
        observeViewModel()
    }

    fun observeViewModel(){
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mFavourites.observe(this, Observer {articles ->
            articles?.let {
                favourites_list.visibility = View.VISIBLE
//                articlesListAdapter.updateArticlesList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsFavouriteLoadError.observe(this, Observer {isError ->
            // check isError not null
            isError?.let {
                listError_TextView.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(this, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingView_ProgressBar.visibility = if(it) View.VISIBLE else View.GONE
                if(it){
                    //hide all views when progress bar is visible
                    listError_TextView.visibility = View.GONE
                    articles_list.visibility = View.GONE
                }
            }
        })
    }
}