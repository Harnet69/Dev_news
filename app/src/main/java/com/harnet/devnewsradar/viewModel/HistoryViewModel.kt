package com.harnet.devnewsradar.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.devnewsradar.model.ArticleDatabase
import com.harnet.devnewsradar.model.ArticleRead
import com.harnet.devnewsradar.util.SharedPreferencesHelper
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class HistoryViewModel(application: Application): BaseViewModel(application){
    // how long keeping articles were read(in days)
    private var deadLineTime = 7

    // helper for SharedPreferences functionality
    private var sharedPrefHelper = SharedPreferencesHelper(getApplication())

    val mArticles = MutableLiveData<List<ArticleRead>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data
    fun refresh() {
        checkDeadLineTime()
        //delete old articles
        deleteOldArticles()
        launch {
            val historyList: List<ArticleRead> = ArticleDatabase.invoke(getApplication()).articleReadDAO().getArticles()
            retrieveArticle(historyList)
        }
    }

    // retrieve articles
    private fun retrieveArticle(historyArticles: List<ArticleRead>) {
        // set received list to observable mutable list
        mArticles.postValue(historyArticles)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // delete old news from History List 1 week/2 weeks / 1 month
    private fun deleteOldArticles(){
        val timeToLive: Long = deadLineTime * 86400000L
        val deadLineTime = System.currentTimeMillis() - timeToLive
        launch {
            ArticleDatabase.invoke(getApplication()).articleReadDAO().deleteOldArticles(deadLineTime)
        }
    }

    private fun checkDeadLineTime(){
        //get value from SharedPreferences
        val daysToDeadLine = sharedPrefHelper.getHistoryKeepingDays()
        try {
            // check if value can be converted to Int, if can't - assign 7 as default
            //TODO can be used in paid version functionality
            val daysToDeadLineInt = daysToDeadLine?.toInt() ?: 7
            deadLineTime = daysToDeadLineInt
            Toast.makeText(getApplication(), "History keeping $deadLineTime days", Toast.LENGTH_SHORT).show()
            Log.i("HistoryKeepsDays", "checkDeadLineTime: $deadLineTime")
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }
    }
}