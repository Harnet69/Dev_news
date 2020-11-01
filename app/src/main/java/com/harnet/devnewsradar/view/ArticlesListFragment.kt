package com.harnet.devnewsradar.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.adapter.ArticlesListAdapter
import com.harnet.devnewsradar.viewModel.ArticlesListViewModel
import com.harnet.devnewsradar.viewModel.FavouritesListViewModel
import kotlinx.android.synthetic.main.fragment_articles_list.*
import java.io.IOException

class ArticlesListFragment : Fragment() {
    private lateinit var viewModel: ArticlesListViewModel
    private lateinit var favouritesListViewModel: FavouritesListViewModel
    lateinit var articlesListAdapter: ArticlesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // switch on a menu
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_articles_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesListViewModel = ViewModelProvider(this).get(FavouritesListViewModel::class.java)
        articlesListAdapter = ArticlesListAdapter(arrayListOf())
        viewModel = ViewModelProvider(this).get(ArticlesListViewModel::class.java)

        showAboutDialog()

        viewModel.refresh()

        articles_list.apply {
            layoutManager = LinearLayoutManager(context)
            //Fix blinking RecyclerView
            articlesListAdapter.setHasStableIds(true)
            adapter = articlesListAdapter
        }

        // add separation line between items
        articles_list.addItemDecoration(DividerItemDecoration(articles_list.context, DividerItemDecoration.VERTICAL))

        // click listener for favourites btn
        fab_btn.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(ArticlesListFragmentDirections.actionArticlesListFragmentToFavouritesListFragment())
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
        var message = ""
        when (item.itemId) {
            // navigate to Favourites
            R.id.favourites -> {
                message = "Favourites"
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ArticlesListFragmentDirections.actionArticlesListFragmentToFavouritesListFragment())
                }
            }
            // navigate to History
            R.id.history -> {
                message = "Reading history"
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ArticlesListFragmentDirections.actionArticlesListFragmentToHistoryFragment())
                }
            }
            // navigate to Settings
            R.id.settings -> {
                message = "Settings"
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ArticlesListFragmentDirections.actionArticlesListFragmentToSettingsFragment())
                }
            }
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        // update the layout using values of mutable variables from a ViewModel
        viewModel.mArticles.observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                articles_list.visibility = View.VISIBLE
                articlesListAdapter.updateArticlesList(articles)
            }
        })

        // make error TextViewVisible
        viewModel.mIsArticleLoadError.observe(viewLifecycleOwner, Observer { isError ->
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

        viewModel.mIsSmthNew.observe(viewLifecycleOwner, Observer { isSmthNew ->
            // check isError not null
            isSmthNew?.let {
                if (it) {
                    Toast.makeText(context, "New Articles are ready", Toast.LENGTH_SHORT).show()
                    viewModel.mIsSmthNew.postValue(false)
                }
            }
        })
    }

    // show About app dialog window
    private fun showAboutDialog(){
        if(!viewModel.getIsAboutShowed()!!){
            AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("About the app")
                .setMessage("This is the best app, provides fresh news of software & tech industry")
                .setPositiveButton("I've got it") { dialogInterface: DialogInterface, i: Int ->
                    try {
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.show()

            viewModel.setIsAboutShowed(true)
        }
    }
}