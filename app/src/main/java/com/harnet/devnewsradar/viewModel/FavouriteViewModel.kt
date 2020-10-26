package com.harnet.devnewsradar.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.model.ArticleDatabase
import com.harnet.devnewsradar.model.ArticleRead
import com.harnet.devnewsradar.model.Favourite
import com.harnet.devnewsradar.service.ParseService
import com.harnet.devnewsradar.service.WebContentDownloader
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

    // create ArticleRead from Article and add it to ArticlesRead table
    fun addToArticlesRead(favourite: Favourite) {
        val articleRead = ArticleRead(
            favourite.id,
            favourite.title,
            favourite.author,
            favourite.url,
            favourite.time,
            favourite.score,
            System.nanoTime()
        )
        articleRead.imageUrl = favourite.imageUrl
        launch {
            if(!ArticleDatabase.invoke(getApplication()).articleReadDAO().isExists(articleRead.id)){
                val addToRead = ArticleDatabase.invoke(getApplication()).articleReadDAO().insertAll(articleRead)
                Toast.makeText(getApplication(), "Article added to ArticleRead $addToRead", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addToFavourites(context: Context, favourite: Favourite) {
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().insertAll(favourite)
            Toast.makeText(context, "Article added to favourites", Toast.LENGTH_SHORT)
                .show()
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