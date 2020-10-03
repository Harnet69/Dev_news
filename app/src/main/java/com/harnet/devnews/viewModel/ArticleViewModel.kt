package com.harnet.devnews.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleDatabase
import kotlinx.coroutines.launch

class ArticleViewModel(application: Application) : BaseViewModel(application) {
    val mArticleLiveData = MutableLiveData<Article>()

    //retrieve data from a database by id
    fun fetch(context: Context, articleId: String){
        launch {
            val article: Article = ArticleDatabase.invoke(context).articleDAO().getArticle(articleId.toInt())
            //mock data for testing
//            Log.i("articleIdToDisplay", "fetch: $article")
//            val article1 = Article(
//                articleId,
//                "This electrical transmission tower",
//                "Twitter",
//                "www.somewhere.com",
//                "54",
//                "4"
//            )
            mArticleLiveData.value = article
        }
    }
}