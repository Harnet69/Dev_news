package com.harnet.devnewsradar.view

import  android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
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
import com.harnet.devnewsradar.service.Paletteable
import com.harnet.devnewsradar.service.SMSable
import com.harnet.devnewsradar.service.Shareable
import com.harnet.devnewsradar.util.SharedPreferencesHelper
import com.harnet.devnewsradar.viewModel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArticleFragment : Fragment(), SMSable, Shareable, Paletteable {
    private lateinit var viewModel: ArticleViewModel
    private lateinit var dataBinding: FragmentArticleBinding
    private var isSendSmsStarted = false // define is sending process was started
    private var currentArticle: Article? = null // for SMS purposes

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
            R.id.action_share -> {
                // share the article url with other apps
                activity?.let {
                    viewModel.mArticleLiveData.value?.url?.let { it1 ->
                        shareUrlWith(
                            it,
                            it1
                        )
                    }
                }
            }
            R.id.action_send_sms -> {
                //check if SMS sending is allowed in Settings
                val isSmsAllowed = context?.let { SharedPreferencesHelper.invoke(it).getIsSmsSendingAllowed() }
                if (isSmsAllowed != null && isSmsAllowed) {
                    isSendSmsStarted = true
                    // ask user for SMS permission
                    // it's crucial to call permission checking on a Activity
                    (activity as MainActivity).appPermissions.smsPermissionService.checkPermission()
                    //TODO add realisation of SMS sending functionality here
                } else {
                    Toast.makeText(
                        context,
                        "SMS sending is turned off in settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // observes article object and binds its data to view elements
    private fun observeViewModel() {
        viewModel.mArticleLiveData.observe(viewLifecycleOwner, Observer { article ->
            // for SMS purposes
            currentArticle = article

            article?.let {
                // bind article to layout
                dataBinding.article = article

                clickOnUrlLink(article_url, article)
                // Palette background handler
                it.imageUrl.let { url ->
                    context?.let { it1 ->
                        setupBackgroundColor(
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
                        setColorToUrl(
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
                article_details_block.visibility = View.VISIBLE
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
        val isFavourite = viewModel.mIsFavourite.value

        viewFavourite?.setOnClickListener {
            if (isFavourite != null) {
                if (isFavourite) {
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

    // method is called when activity get a result of user  permission decision
    fun onPermissionsResult(permissionGranted: Boolean) {
        // create sms dialog and send SMS
        createSmsDialog(context, currentArticle, isSendSmsStarted, permissionGranted)
    }
}