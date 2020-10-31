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
import java.lang.Exception
import java.util.*

class ArticleViewModel(application: Application) : BaseViewModel(application) {
//    //TODO FOR SETTING PURPOSES!!! 1 WEEK / 2 WEEK / 1 MONTH
//    val DEAD_LINE_TIME = 7 // in days
    val mArticleLiveData = MutableLiveData<Article>()
    val mIsFavourite = MutableLiveData<Boolean>()

    private val webContentDownloader = WebContentDownloader()
    private val parseService = ParseService()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String, isFavourite: Boolean) {
        launch {
            val articleToShow = ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            try {
                isArtFav(context, articleToShow.id)
                // parse webpage and get an article image
                //!!can use this pageContent to store this webpage in our database
                val pageContent = webContentDownloader.execute(articleToShow.url).get()
                val imagesURL = parseService.parseImages(pageContent)
                //!!can make a image checker for all images in imagesURL
                articleToShow.imageUrl = imagesURL?.get(0) as String
                // set article date
                val articleDate = Date(articleToShow.time.toLong() * 1000)
                articleToShow.time = articleDate.toString()
                // set is article in favourite
                articleToShow.isFavourite = isFavourite

            } catch (e: Exception) {
                Log.i("ArticleData", "fetch: No Data")
            }
            val article: Article = articleToShow
//            // add to read articles
//            addToArticlesRead(article)
            //update mutableData
            mArticleLiveData.value = article
        }
    }

    // create Favourite from Article and add it to Favourites table
    fun addToFavourite(context: Context, article: Article) {
        val favourite = Favourite(
            article.id,
            article.title,
            article.author,
            article.url,
            article.time,
            article.score
        )
        favourite.imageUrl = article.imageUrl
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().insertAll(favourite)
            Toast.makeText(context, "Article added to favourites", Toast.LENGTH_SHORT).show()
        }
    }

    // create ArticleRead from Article and add it to ArticlesRead table
    fun addToArticlesRead(article: Article) {
        val articleRead = ArticleRead(
            article.id,
            article.title,
            article.author,
            article.url,
            article.time,
            article.score,
            System.currentTimeMillis()
        )
        articleRead.imageUrl = article.imageUrl

        launch {
            if(!ArticleDatabase.invoke(getApplication()).articleReadDAO().isExists(articleRead.id)){
                // add the article to have been read list
                ArticleDatabase.invoke(getApplication()).articleReadDAO().insertAll(articleRead)
            }else{
                // set to the readArticle new time of reading
                ArticleDatabase.invoke(getApplication()).articleReadDAO().updateTimeWhenRead(articleRead.id, articleRead.timeWhenRead)
            }
        }
    }

    // remove from favourite by Article id
    fun removeFromFavourites(context: Context, id: String) {
        launch {
            ArticleDatabase.invoke(context).favouriteDAO().deleteFavourite(id)
            Toast.makeText(context, "Article removed from favourites", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // check if article is favourite
    private fun isArtFav(context: Context, id: String) {
        mIsFavourite.value = false
        launch {
            if (ArticleDatabase.invoke(context).favouriteDAO().getFavourite(id) != null) {
                mIsFavourite.value = true
            } else {
                mIsFavourite.value = false
            }
        }
    }
}