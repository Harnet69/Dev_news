package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.service.ParseService
import com.harnet.devnews.service.WebContentDownloader
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class ArticleViewModel(application: Application) : BaseViewModel(application) {
    val mArticleLiveData = MutableLiveData<Article>()
    private val webContentDownloader = WebContentDownloader()
    private val parseService = ParseService()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String) {
        launch {
            var articleToShow = ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            try {
                val pageContent = webContentDownloader.execute(articleToShow.url).get()
                //TODO implement images parsing here
                var imagesURL = parseService.parseImages(pageContent)
                Log.i("ArticleData", "fetch: Images " + (imagesURL?.get(0) ?: 0))

//                Log.i("ArticleData", "fetch: " + pageContent)
                val articleDate = Date(articleToShow.time.toLong() * 1000)
                articleToShow.time = articleDate.toString()
            } catch (e: Exception) {
                Log.i("ArticleData", "fetch: No Data")
            }
            val article: Article = articleToShow

            mArticleLiveData.value = article
        }
    }
}