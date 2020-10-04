package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleDatabase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ArticleViewModel(application: Application) : BaseViewModel(application) {
    val mArticleLiveData = MutableLiveData<Article>()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String) {
        launch {
            var articleToShow = ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            try {
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