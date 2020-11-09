package com.harnet.devnewsradar.view

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.harnet.devnewsradar.databinding.FavouriteFragmentBinding
import com.harnet.devnewsradar.model.Favourite
import com.harnet.devnewsradar.service.PaletteService
import com.harnet.devnewsradar.service.ShareService
import com.harnet.devnewsradar.util.SharedPreferencesHelper
import com.harnet.devnewsradar.viewModel.FavouriteViewModel
import kotlinx.android.synthetic.main.favourite_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var dataBinding: FavouriteFragmentBinding
    private val paletteService = PaletteService()
    private var shareService = ShareService()
    private var isSendSmsStarted = false // define is sending process was started

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

        // switch on a menu
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        // get ID from Articles list adapter, receive arguments from sending fragment
        arguments?.let {
            val favouriteUuId = ArticleFragmentArgs.fromBundle(it).articleId
            //get the article from a database with coroutine for invoking a suspended function
            GlobalScope.launch {
                viewModel.fetch(view.context, favouriteUuId)
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
            // share the article url with other apps
            R.id.action_share -> {
                activity?.let {
                    viewModel.mFavoriteLiveData.value?.url?.let { it1 ->
                        shareService.shareUrlWith(
                            it,
                            it1
                        )
                    }
                }
            }
            R.id.action_send_sms -> {
                //check if SMS sending is allowed in Settings
                if (context?.let {
                        SharedPreferencesHelper.invoke(it).getIsSmsSendingAllowed()
                    }!!) {
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
        viewModel.mFavoriteLiveData.observe(viewLifecycleOwner, Observer { article ->
            article?.let {
                dataBinding.favourite = article

                clickOnUrlLink(favourite_url, article)

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
                    context?.let { it1 ->
                        paletteService.setColorToUrl(
                            it1,
                            url,
                            null,
                            dataBinding
                        )
                    }
                }
                // underscore URL address
                favourite_url.paintFlags = favourite_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                handleFavourite(isFavourite, article)

                loadingView_ProgressBar.visibility = View.GONE
                favourite_image.visibility = View.VISIBLE
                isFavourite.visibility = View.VISIBLE
                favourite_details_block.visibility = View.VISIBLE

            }
        })
    }

    // when click to URl link
    private fun clickOnUrlLink(view: TextView?, favourite: Favourite) {
        view?.setOnClickListener {
            // add to read articles
            viewModel.addToArticlesRead(favourite)
            // open the link in default browser
            openWebsite(view)
        }
    }

    // open a website by URL from view value
    private fun openWebsite(view: TextView?) {
        val webPage = view?.text.toString()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webPage))
        try {
            startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Wrong URL", Toast.LENGTH_SHORT).show()
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

    // method will called when activity get a result of user decision
    fun onPermissionsResult(permissionGranted: Boolean) {
    }
}