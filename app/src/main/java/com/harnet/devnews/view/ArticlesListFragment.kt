package com.harnet.devnews.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnews.R
import com.harnet.devnews.viewModel.ArticlesListViewModel
import kotlinx.android.synthetic.main.fragment_articles_list.*

class ArticlesListFragment : Fragment() {
    private lateinit var viewModel: ArticlesListViewModel
    val articlesListAdapter = ArticlesListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ArticlesListViewModel::class.java)
        viewModel.refresh()

        articles_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesListAdapter
        }

        // Swiper refresh listener(screen refreshing process)
        refreshLayout.setOnRefreshListener {
            articles_list.visibility = View.GONE
            listError_TextView.visibility = View.GONE
            loadingView_ProgressBar.visibility = View.VISIBLE
            viewModel.refresh()
            refreshLayout.isRefreshing = false // disappears little spinner on the top

        }

        observeViewModel()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.articles_list_menu, menu)
    }

    // handle with options menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.favourites){
            Toast.makeText(context, "Favourites", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(view!!).navigate(ArticlesListFragmentDirections.actionArticlesListFragmentToFavouritesListFragment())
           return true
        }
        return NavigationUI.onNavDestinationSelected(item!!,
            view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }


    fun observeViewModel(){
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mArticles.observe(this, Observer {articles ->
            articles?.let {
                articles_list.visibility = View.VISIBLE
                articlesListAdapter.updateArticlesList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsArticleLoadError.observe(this, Observer {isError ->
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