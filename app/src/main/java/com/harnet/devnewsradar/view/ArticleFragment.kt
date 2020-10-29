package com.harnet.devnewsradar.view

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.FragmentArticleBinding
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.service.PaletteService
import com.harnet.devnewsradar.viewModel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleFragment : Fragment() {
    private lateinit var viewModel: ArticleViewModel
    private lateinit var dataBinding: FragmentArticleBinding
    private var paletteService = PaletteService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // DataBinding approach
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // switch on a menu
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        viewModel.mIsFavourite.value = false

        loadingView_ProgressBar.visibility = View.VISIBLE

        // get id from Articles list adapter, receive arguments from sending fragment
        arguments?.let {
            val articleId = ArticleFragmentArgs.fromBundle(it).articleId
            val isFavourite = ArticleFragmentArgs.fromBundle(it).isFavourite
            //get the article from a database with coroutine for invoking a suspended function
            GlobalScope.launch {
                viewModel.fetch(view.context, articleId.toString(), isFavourite)
            }
        }
        observeViewModel()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    // click listener for menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                Toast.makeText(context, "Send SMS", Toast.LENGTH_SHORT).show()

            }
            R.id.action_share -> {
                // ACTION_SEND generic flag for sending
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interesting article")
                intent.putExtra(Intent.EXTRA_TEXT, viewModel.mArticleLiveData.value?.url)
                intent.putExtra(Intent.EXTRA_STREAM,  viewModel.mArticleLiveData.value?.imageUrl)
                // give user the possibility to chose the application for getting this data
                startActivity(Intent.createChooser(intent, "Share with:"))
            }
        }
            return super.onOptionsItemSelected(item)
        }

        // observes article object and binds its data to view elements
        private fun observeViewModel() {
            viewModel.mArticleLiveData.observe(viewLifecycleOwner, Observer { article ->
                article?.let {
                    // bind article to layout
                    dataBinding.article = article

                    clickOnUrlLink(article_url, article)
                    // Palette background handler
                    it.imageUrl.let { url ->
                        context?.let { it1 ->
                            paletteService.setupBackgroundColor(
                                it1,
                                url,
                                dataBinding,
                                null
                            )
                        }
                    }
                    // color URL link
                    it.imageUrl.let { url ->
                        context?.let { it1 ->
                            paletteService.setColorToUrl(
                                it1,
                                url,
                                dataBinding,
                                null
                            )
                        }
                    }
                    // underscore URL
                    article_url.paintFlags = article_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    // set favourite image
                    observeIsFav()
                    makeFavourite(article_favourite, article)
                    loadingView_ProgressBar.visibility = View.GONE
                    article_image.visibility = View.VISIBLE
                }
            })
        }

        // observe is article favourite and set the appropriate image
        private fun observeIsFav() {
            viewModel.mIsFavourite.observe(viewLifecycleOwner, Observer { isFav ->
                if (isFav) {
                    article_favourite.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
                }
            })
        }

        // when click to URl link
        private fun clickOnUrlLink(view: TextView?, article: Article) {
            view?.setOnClickListener {
                // add to read articles
                viewModel.addToArticlesRead(article)
                // open the link in default browser
                openWebsite(view)
            }
        }

        // open a website by URL from the view value
        private fun openWebsite(view: TextView) {
            val webPage = view.text.toString()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webPage))
            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Wrong URL", Toast.LENGTH_SHORT).show()
            }
        }

        // handle favourite image
        private fun makeFavourite(viewFavourite: ImageView?, article: Article) {
            article_favourite.setImageResource(android.R.drawable.btn_star_big_off)
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
                }
            }
        }
    }