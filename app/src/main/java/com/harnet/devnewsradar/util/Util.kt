package com.harnet.devnewsradar.util

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.view.ArticlesListFragmentDirections
import java.text.SimpleDateFormat
import java.util.*

//little loading spinner for image loading
fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

//extension for auto loading image of ImageView element using Glide library
fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_dev_news)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        })
        .into(this)// this - extended ImageView class
}

// load image by ViewBinding(uses extended load function to bind this function to imageView)
// @BindingAdapter annotation make function visible from a layout (parameter fill be the name of xml field)
// !!!REBUILD PROJECT AFTER IMPLEMENTING!!!
@BindingAdapter("android:bindImageUrl")
fun loadBindingImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

// transition to an article detail page
@BindingAdapter("android:goToArticle")
fun goToArticle(view: View, articleUuid: Int?) {
    view.setOnClickListener {
        // navigate to appropriate detail fragment
        val action =
            ArticlesListFragmentDirections.actionArticlesListFragmentToArticleFragment()
        // send article id to ArticleFragment
        if (articleUuid != null) {
            action.articleId = articleUuid
        }
        Navigation.findNavController(it).navigate(action)
    }
}

@BindingAdapter("android:goToUrlFromHistory")
fun goToUrlFromHistory(view: View, articleUrl: String?) {
    view.setOnClickListener {
        val browserIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
        try {
            startActivity(view.context, browserIntent, null)
        } catch (e: Exception) {
            Toast.makeText(view.context, "Wrong URL", Toast.LENGTH_SHORT).show()
        }
    }
}

@BindingAdapter("android:getDateTimeFromLong")
fun getDateTime(view: TextView, time: Long) {
    view.text =  Date(time).toString()
}