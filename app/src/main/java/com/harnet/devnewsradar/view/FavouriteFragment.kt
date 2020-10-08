package com.harnet.devnewsradar.view

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.devnewsradar.viewModel.FavouriteViewModel
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.model.Favourite
import com.harnet.devnewsradar.util.getProgressDrawable
import com.harnet.devnewsradar.util.loadImage
import kotlinx.android.synthetic.main.favourite_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavouriteFragment()
    }

    private lateinit var viewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favourite_fragment, container, false)
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
                favourite_id.text = "Article id: " + article.id
                favourite_title.text = article.title
                favourite_author.text = "Author: " + article.author
                favourite_time.text = "Time: " + article.time
                favourite_url.paintFlags = favourite_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                favourite_url.text = article.url
                // set is article favourite

                // set image for favourite star
                isFavourite.setImageResource(android.R.drawable.btn_star_big_on)

                // parse page source code and insert image to an article
                article.let {
                    context?.let { it1 -> getProgressDrawable(it1) }?.let { it2 ->
                        favourite_image.loadImage(
                            article.imageUrl,
                            it2
                        )
                    }
                    handleFavourite(isFavourite, article)
                }
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

    // handle favourites
    private fun handleFavourite(viewFavourite: ImageView?, favourite: Favourite) {
        var isArticleFavourite = true
        viewFavourite?.setOnClickListener {
            if (isArticleFavourite) {
                context?.let { it1 -> viewModel.removeFromFavourites(it1, favourite.uuid) }
                isFavourite.setImageResource(android.R.drawable.btn_star_big_off)
            } else {
                context?.let { it1 -> viewModel.addToFavourites(it1, favourite) }
                isFavourite.setImageResource(android.R.drawable.btn_star_big_on)
            }
            isArticleFavourite = !isArticleFavourite
        }
    }
}