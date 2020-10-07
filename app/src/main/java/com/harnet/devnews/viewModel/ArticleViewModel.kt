package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.model.Favourite
import com.harnet.devnews.service.ParseService
import com.harnet.devnews.service.WebContentDownloader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import java.util.concurrent.CompletableFuture

class ArticleViewModel(application: Application) : BaseViewModel(application) {
    val mArticleLiveData = MutableLiveData<Article>()
    private val webContentDownloader = WebContentDownloader()
    private val parseService = ParseService()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String, isFavourite: Boolean) {
        launch {
            val articleToShow =
                ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            try {
                // set article image
                val pageContent = webContentDownloader.execute(articleToShow.url).get()
                val imagesURL = parseService.parseImages(pageContent)
                articleToShow.imageUrl = imagesURL?.get(0) as String
                // set article date
                val articleDate = Date(articleToShow.time.toLong() * 1000)
                articleToShow.time = articleDate.toString()
                // set is article in favourite
                articleToShow.isFavourite = isFavourite

                //handling in favourite table
                if (articleToShow.isFavourite) {
                    //record the article to favourites
                    addToFavourite(context, articleToShow)
                } else {
                    //TODO delete the article from favourites

                }

            } catch (e: Exception) {
                Log.i("ArticleData", "fetch: No Data")
            }
            val article: Article = articleToShow

            mArticleLiveData.value = article
        }
    }

    fun addToFavourite(context: Context, article: Article) {
        val favouriteArticle = Favourite(
            article.id,
            article.title,
            article.author,
            article.url,
            article.time,
            article.score
        )


        favouriteArticle.imageUrl = favouriteArticle.imageUrl
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().insertAll(favouriteArticle)
            Toast.makeText(context, "Article added to favourites", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFromFavourites(context: Context, uuid: Int) {
        Log.i("ArticleForRem", "removeFromFavourites: " + uuid)
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().deleteFavourite(uuid)
            Toast.makeText(context, "Article removed from favourites", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun removeFromFavourites(context: Context, id: String) {
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().deleteFavourite(id)
            Toast.makeText(context, "Article removed from favourites", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun getFavourites(context: Context){
        launch {
            Log.i(
                "FavouriteArticls",
                "fetch from favourites: " + ArticleDatabase.invoke(context).favouriteDAO()
                    .getFavourites()
            )
        }
    }
}