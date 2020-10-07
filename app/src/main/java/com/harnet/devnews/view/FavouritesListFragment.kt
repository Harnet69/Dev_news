package com.harnet.devnews.view

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harnet.devnews.R
import com.harnet.devnews.viewModel.FavouritesListViewModel

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FavouritesListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}