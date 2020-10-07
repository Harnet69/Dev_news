package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.model.Favourite
import com.harnet.devnews.service.ParseService
import com.harnet.devnews.service.WebContentDownloader
import kotlinx.coroutines.launch
import java.util.*

class FavouriteViewModel(application: Application) : BaseViewModel(application) {
    val mFavoriteLiveData = MutableLiveData<Favourite>()
    private val webContentDownloader = WebContentDownloader()
    private val parseService = ParseService()

    //retrieve data from a database by id
    fun fetch(context: Context, articleUuId: Int) {
        launch {
            val favouriteToShow =
                ArticleDatabase.invoke(context).favouriteDAO().getFavourite(articleUuId)
            try {
                // set article image
                val pageContent = webContentDownloader.execute(favouriteToShow.url).get()
                val imagesURL = parseService.parseImages(pageContent)
                favouriteToShow.imageUrl = imagesURL?.get(0) as String
                // set article date
                val articleDate = Date(favouriteToShow.time.toLong() * 1000)
                favouriteToShow.time = articleDate.toString()

            } catch (e: Exception) {
                Log.i("ArticleData", "fetch: No Data")
            }
            val article: Favourite = favouriteToShow

            mFavoriteLiveData.value = article
        }
    }

    fun removeFromFavourites(context: Context, uuid: Int) {
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().deleteFavourite(uuid)
            Toast.makeText(context, "Article removed from favourites", Toast.LENGTH_SHORT)
                .show()
        }
    }
}