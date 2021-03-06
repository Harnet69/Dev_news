package com.harnet.devnewsradar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.adapter.FavouritesListAdapter
import com.harnet.devnewsradar.databinding.FavouritesListFragmentBinding
import com.harnet.devnewsradar.databinding.FragmentArticlesListBinding
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.favourites_list_fragment.*
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.android.synthetic.main.fragment_articles_list.listError_TextView
import kotlinx.android.synthetic.main.fragment_articles_list.loadingView_ProgressBar
import kotlinx.android.synthetic.main.fragment_articles_list.refreshLayout

class FavouritesListFragment : Fragment() {
    private lateinit var viewModel: FavouritesListViewModel
    lateinit var favouritesListAdapter: FavouritesListAdapter
    private lateinit var dataBinding: FavouritesListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // DataBinding approach
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.favourites_list_fragment, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavouritesListViewModel::class.java)
        context?.let { viewModel.refresh(it) }
        favouritesListAdapter = FavouritesListAdapter(arrayListOf(), viewModel)

        favourites_list.apply {
            layoutManager = LinearLayoutManager(context)
            favouritesListAdapter.setHasStableIds(true)
            adapter = favouritesListAdapter
        }

        // bind a fake article to layout for functions working
        dataBinding.article = Article("1", "1", "1", "1", "1", "1")

        // add separation line between items
        favourites_list.addItemDecoration(DividerItemDecoration(favourites_list.context, DividerItemDecoration.VERTICAL))

        // Swiper refresh listener(screen refreshing process)
        refreshLayout.setOnRefreshListener {
            favourites_list.visibility = View.GONE
            listError_TextView.visibility = View.GONE
            loadingView_ProgressBar.visibility = View.VISIBLE
            context?.let { viewModel.refresh(it) }
            refreshLayout.isRefreshing = false // disappears little spinner on the top
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mFavourites.observe(viewLifecycleOwner, Observer { favourites ->
            favourites?.let {
                favourites_list.visibility = View.VISIBLE
                favouritesListAdapter.updateFavouritesList(favourites.reversed())
            }
        })

        // make error TextViewVisible
        viewModel.mIsFavouriteLoadError.observe(viewLifecycleOwner, Observer { isError ->
            // check isError not null
            isError?.let {
                listError_TextView.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // loading spinner
        viewModel.mIsLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            //check isLoading not null
            isLoading?.let {
                // if data still loading - show spinner, else - remove it
                loadingView_ProgressBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    //hide all views when progress bar is visible
                    listError_TextView.visibility = View.GONE
                    articles_list.visibility = View.GONE
                }
            }
        })
    }
}