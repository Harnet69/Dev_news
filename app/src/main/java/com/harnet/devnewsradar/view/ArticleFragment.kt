package com.harnet.devnewsradar.view

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.util.getProgressDrawable
import com.harnet.devnewsradar.util.loadImage
import com.harnet.devnewsradar.viewModel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ArticleFragment : Fragment() {
    lateinit var viewModel: ArticleViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        viewModel.mIsFavourite.value = false
        // get ID from Articles list adapter, receive arguments from sending fragment
        arguments?.let {
            val articleId = ArticleFragmentArgs.fromBundle(it).articleId
            val isFavourite = ArticleFragmentArgs.fromBundle(it).isFavourite
            //get the article from a database with coroutine for invoking a suspended function
            GlobalScope.launch {
                viewModel.fetch(view.context, articleId.toString(), isFavourite)
            }
        }
        openWebsite(article_url)
        observeViewModel()
        observeIsFav()
    }

    // observes article object and binds its data to view elements
    private fun observeViewModel() {
        viewModel.mArticleLiveData.observe(viewLifecycleOwner, Observer { article ->
            article?.let {
                article_id.text = "Article id: " + article.id
                article_title.text = article.title
                article_author.text = "Author: " + article.author
                article_time.text = "Time: " + article.time
                article_url.paintFlags = article_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                article_url.text = article.url
                // set image for favourite star
//                if (viewModel.mIsFavourite.value!!) {
//                    article_favourite.setImageResource(android.R.drawable.btn_star_big_on)
//                } else {
//                    article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
//                }
                makeFavourite(article_favourite, article)
                // parse page source code and insert image to an article
                //TODO if article is favourite record page content to a database
                article.let {
                    context?.let { it1 -> getProgressDrawable(it1) }?.let { it2 ->
                        article_image.loadImage(
                            article.imageUrl,
                            it2
                        )
                    }
                }
                loadingView_ProgressBar.visibility = View.GONE
            }
        })
    }

    fun observeIsFav() {
        viewModel.mIsFavourite.observe(viewLifecycleOwner, Observer { isFav ->
            if (isFav) {
                article_favourite.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
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

    // handle favourite image
    private fun makeFavourite(viewFavourite: ImageView?, article: Article) {
        article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
        Log.i("ArticleIsddd", "makeFavourite: " + viewModel.mIsFavourite.value)
        viewFavourite?.setOnClickListener {
            if (viewModel.mIsFavourite.value != null) {
                if (viewModel.mIsFavourite.value!!) {
                    article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
                    // remove from favourites
                    context?.let { it1 -> viewModel.removeFromFavourites(it1, article.id) }
                    article.isFavourite = false
                    viewModel.mIsFavourite.value = false
                } else {
                    article.isFavourite = true
                    article_favourite.setImageResource(android.R.drawable.btn_star_big_on)
                    // add to favourites
                    context?.let { it1 -> viewModel.addToFavourite(it1, article) }
                    viewModel.mIsFavourite.value = true
                }
                //TODO record changes to table of favourites articles
                context?.let { it1 -> viewModel.getFavourites(it1) }

            }
        }
    }
}