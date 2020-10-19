package com.harnet.devnewsradar.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.FavouriteFragmentBinding
import com.harnet.devnewsradar.model.ArticlePalette
import com.harnet.devnewsradar.model.Favourite
import com.harnet.devnewsradar.service.PaletteService
import com.harnet.devnewsradar.viewModel.FavouriteViewModel
import kotlinx.android.synthetic.main.favourite_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var dataBinding: FavouriteFragmentBinding
    private val paletteService = PaletteService()

    companion object {
        fun newInstance() = FavouriteFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.favourite_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        // get ID from Articles list adapter, receive arguments from sending fragment
        arguments?.let {
            val favouriteUuId = ArticleFragmentArgs.fromBundle(it).articleId
            //get the article from a database with coroutine for invoking a suspended function
            GlobalScope.launch {
                viewModel.fetch(view.context, favouriteUuId)
            }
        }
        openWebsite(favourite_url)
        observeViewModel()

    }

    // observes article object and binds its data to view elements
    private fun observeViewModel() {
        viewModel.mFavoriteLiveData.observe(viewLifecycleOwner, Observer { article ->
            article?.let {
                dataBinding.favourite = article

                // Palette handler
                it.imageUrl.let { url ->
                    context?.let { it1 ->
                        paletteService.setupBackgroundColor(
                            it1,
                            url,
                            null,
                            dataBinding
                        )
                    }
                }

                // color URL link
                it.imageUrl.let { url ->
                    context?.let { it1 -> paletteService.setColorToUrl(it1, url, null, dataBinding) }
                }
                // underscore URL address
                favourite_url.paintFlags = favourite_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                handleFavourite(isFavourite, article)

                loadingView_ProgressBar.visibility = View.GONE
                favourite_image.visibility = View.VISIBLE
                isFavourite.visibility = View.VISIBLE

            }
        })
    }

    // open a website by URL from view value
    private fun openWebsite(view: TextView?) {
        view?.setOnClickListener {
            val webPage = view.text.toString()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webPage))
            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Wrong URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // handle with favourites
    private fun handleFavourite(viewFavourite: ImageView?, favourite: Favourite) {
        var isArticleFavourite = true
        viewFavourite?.setOnClickListener {
            if (isArticleFavourite) {
                AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete")
                    .setMessage("Do you want to remove the article from favorite?")
                    .setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                        context?.let { it1 -> viewModel.removeFromFavourites(it1, favourite.uuid) }
                        isFavourite.setImageResource(android.R.drawable.btn_star_big_off)
                        isArticleFavourite = false
                    }.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                    }.show()
            } else {
                context?.let { it1 -> viewModel.addToFavourites(it1, favourite) }
                isFavourite.setImageResource(android.R.drawable.btn_star_big_on)
                isArticleFavourite = true
            }
        }
    }
}