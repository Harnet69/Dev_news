package com.harnet.devnewsradar.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.harnet.devnewsradar.databinding.FavouriteFragmentBinding
import com.harnet.devnewsradar.databinding.FragmentArticleBinding
import com.harnet.devnewsradar.model.ArticlePalette

class PaletteService {

    // Palette handler
    fun setupBackgroundColor(
        context: Context,
        url: String,
        articleDataBinding: FragmentArticleBinding?,
        favouriteDataBinding: FavouriteFragmentBinding?
    ) {

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            //extract color. If rgb is null intColor = 0
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            //create an object of Palette
                            val articlePalette = ArticlePalette(intColor)
                            //bind object to View xml

                            if (articleDataBinding != null) {
                                articleDataBinding.palette = articlePalette
                            } else {
                                if (favouriteDataBinding != null) {
                                    favouriteDataBinding.palette = articlePalette
                                }
                            }
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    // set links dolor depends of an article image
    fun setColorToUrl(
        context: Context,
        url: String,
        articleDataBinding: FragmentArticleBinding?,
        favouriteDataBinding: FavouriteFragmentBinding?
    ) {
        //define default values for URL links color
        articleDataBinding?.let{articleDataBinding.paletteURL = ArticlePalette(-14669752)}
        favouriteDataBinding?.let{favouriteDataBinding.paletteURL = ArticlePalette(-14669752)}

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Palette.from(resource)
                        .generate { palette ->
                            //extract color. If rgb is null intColor = 0
                            val intColor = palette?.darkMutedSwatch?.rgb ?: -14669752
                            Log.i("WhatColor", "onResourceReady: " + intColor)
                            //create an object of Palette
                            val articlePalette = ArticlePalette(intColor)
                            //bind object to View xml
                            if (articleDataBinding != null) {
                                articleDataBinding.paletteURL = articlePalette
                            } else {
                                if (favouriteDataBinding != null) {
                                    favouriteDataBinding.paletteURL = articlePalette
                                }
                            }
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }
}