package com.harnet.devnewsradar.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.model.ArticleDatabase
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application): BaseViewModel(application){
    val mArticles = MutableLiveData<List<Article>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data
    fun refresh() {
        launch {
            val historyList = ArticleDatabase.invoke(getApplication()).articleReadDAO().getArticles()

            retrieveArticle(historyList)
        }
    }

    // retrieve articles
    private fun retrieveArticle(historyArticles: List<Article>) {
        // set received list to observable mutable list
        mArticles.postValue(historyArticles)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

}