package com.harnet.devnews.view

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harnet.devnews.R
import com.harnet.devnews.util.getProgressDrawable
import com.harnet.devnews.util.loadImage
import com.harnet.devnews.viewModel.ArticleViewModel
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
        viewModel = ViewModelProviders.of(this).get(ArticleViewModel::class.java)
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
    }

    // observes article object and binds its data to view elements
    private fun observeViewModel() {
        viewModel.mArticleLiveData.observe(this, Observer { article ->
            article?.let {
                article_id.text = "Article id: " + article.id
                article_title.text = article.title
                article_author.text = "Author: " + article.author
                article_time.text = "Time: " + article.time
                article_url.paintFlags = article_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                article_url.text = article.url
                // parse page source code and insert image to an article
                article.let {
                    context?.let { it1 -> getProgressDrawable(it1) }?.let { it2 ->
                        article_image.loadImage(
                            article.imageUrl,
                            it2
                        )
                    }
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
}