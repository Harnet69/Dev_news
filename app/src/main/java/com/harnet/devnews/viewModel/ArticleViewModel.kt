package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleDatabase
import com.harnet.devnews.service.WebContentDownloader
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class ArticleViewModel(application: Application) : BaseViewModel(application) {
    val mArticleLiveData = MutableLiveData<Article>()
    val webContentDownloader = WebContentDownloader()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String) {
        launch {
            var articleToShow = ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            Log.i("ArticleData", "fetch: " + articleToShow.url)
            try {
                val pageContent = webContentDownloader.execute(articleToShow.url).get()
                Log.i("ArticleData", "fetch: " + pageContent)
                val articleDate = Date(articleToShow.time.toLong() * 1000)
                articleToShow.time = articleDate.toString()
                Log.i("ArticleData", "fetch: " + articleDate)
            } catch (e: Exception) {
                Log.i("ArticleData", "fetch: No Data")
            }
            val article: Article = articleToShow

            mArticleLiveData.value = article
        }
    }
}