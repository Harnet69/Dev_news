package com.harnet.devnewsradar.viewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.model.ArticleDatabase
import com.harnet.devnewsradar.model.ArticleLists
import com.harnet.devnewsradar.service.ArticleApiServis
import com.harnet.devnewsradar.service.ParseService
import com.harnet.devnewsradar.util.NotificationsHelper
import com.harnet.devnewsradar.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.CompletableFuture

class ArticlesListViewModel(application: Application) : BaseViewModel(application) {
    private val ARTICLES_TO_SHOW: Int = 20
    private val API_UPDATING_TIME_QUANTITY = 1
    private var lastArticleId = 0

    // helper for SharedPreferences functionality
    private var sharedPrefHelper = SharedPreferencesHelper(getApplication())

    // old fashion parse service
    private val parseService: ParseService = ParseService()

    // Retrofit service
    private val articleApiService = ArticleApiServis()
    private val disposable = CompositeDisposable()

    private val articlesLists = ArticleLists()

    val mArticles = MutableLiveData<List<Article>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    val mIsSmthNew = MutableLiveData<Boolean>()
    //implement here observable articles list switcher

    // refresh mArticles with a new data TWO WAYS TO DO IT: PARSER & RETROFIT
    fun refresh() {
        mIsArticleLoadError.value = false
        val timeToUpd: Long? = sharedPrefHelper.getLastUpdateTime()
            ?.plus(convertMinToNanosec(API_UPDATING_TIME_QUANTITY))
        //make observable variable for time!!!
        if (System.nanoTime() > timeToUpd!!) {
            //make a switcher between two ways of parsing

            //get data by old fashion manner parser
            launch {
                makeArticlesListByParser(articlesLists.NEW_STORIES)
            }
//        get data by retrofit
//        fetchFromRemote(articlesLists.NEW_STORIES)
//            var newLastArtId = mArticles.value?.get(0)?.id
            Toast.makeText(getApplication(), "Getting news", Toast.LENGTH_LONG).show()
            // create a notification

//            NotificationsHelper(getApplication()).createNotification()
        } else {
            launch {
                retrieveArticle(ArticleDatabase.invoke(getApplication()).articleDAO().getArticles())
                Toast.makeText(
                    getApplication(),
                    "Freeware. " + timeToRefreshFromAPI(timeToUpd) + " left",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    // fetches data from remote API using Retrofit
    private fun fetchFromRemote(articlesList: String) {
        //TODO !!!DON'T FORGET TO ADD INTERNET PERMISSION BEFORE IMPLEMENTING!!!
        val articlesFromAPI = mutableListOf<Article>()
        // list of articles ids
        val articlesIDs = parseService.getArticlesIDs(articlesList, ARTICLES_TO_SHOW)

        // set loading flag to true
        mIsLoading.value = true

        articlesIDs?.let {
            for (i in 0 until articlesIDs.size) {
                disposable.add(
                    // set it to a different thread(passing this call to the background thread)
                    articleApiService.getArticle(articlesIDs[i])
                        .subscribeOn(Schedulers.newThread())
                        // retrieve it from a background to the main thread for displaying
                        .observeOn(AndroidSchedulers.mainThread())
                        // pass our Single object here, it's created and implemented
                        .subscribeWith(object : DisposableSingleObserver<Article>() {
                            // get list of DogBreed objects
                            override fun onSuccess(article: Article) {
                                if (i != articlesIDs.size - 1) {
                                    // prevent from 'NOT NULL constraint failed' exception
                                    if (article.url == null) {
                                        article.url = ""
                                    }
                                    article.imageUrl = ""
                                    // add article to a list
                                    articlesFromAPI.add(article)
                                } else {
                                    //TODO set articles were read
                                    val updatedArticles = setArticlesHaveBeenRead(articlesFromAPI)
                                    storeArticleInDatabase(updatedArticles)
                                }
                            }

                            // get an error
                            override fun onError(e: Throwable) {
                                mIsArticleLoadError.value = true
                                mIsLoading.value = false
                                // print stack of error to handling it
                                e.printStackTrace()
                            }
                        })
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    // get data by old fashion parser
    // generate articles list from webController data
    private fun makeArticlesListByParser(articlesList: String) {
        val articlesFromAPI = mutableListOf<Article>()
        mIsSmthNew.postValue(false)
        CompletableFuture.supplyAsync {
            parseService.getArticlesURLs(articlesList, ARTICLES_TO_SHOW)
        }
            .thenAccept { URLs: MutableList<URL>? ->
                try {
                    for (i in 0 until ARTICLES_TO_SHOW) {

                        val article: String = parseService.parse(URLs?.get(i).toString())
                        val parsedArticle: Article? = parseArticleDetails(article)
                        parsedArticle?.let {
                            articlesFromAPI.add(it)
                            // add the first articles id for a notification purposes
                            if(i == 0){
                                // if it's a new article exists
                                if(it.id.toIntOrNull() != lastArticleId){
                                    // new articles notification
                                    NotificationsHelper(getApplication()).createNotification()
                                    lastArticleId = it.id.toIntOrNull()!!
                                    // new articles toast
                                    mIsSmthNew.postValue(true)
                                }
                            }
                        }
                    }
                    //TODO set articles were read
                    val updatedArticles = setArticlesHaveBeenRead(articlesFromAPI)
                    storeArticleInDatabase(updatedArticles)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    // retrieve articles
    private fun retrieveArticle(articlesFromAPI: List<Article>) {
        // set received list to observable mutable list
        mArticles.postValue(articlesFromAPI)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // store articles in a database
    private fun storeArticleInDatabase(articlesList: List<Article>) {
        launch {
            val dao = ArticleDatabase(getApplication()).articleDAO()
            // delete previous version
            dao.deleteArticles()
            // add newly parced articles to a database
            val result = dao.insertAll(*articlesList.toTypedArray())
            // assign id to article uuid field
            for (i in result.indices) {
                if (i < articlesList.size - 1 || i < result.size - 1) {
                    articlesList[i].uuid = result[i].toInt()
                }
            }
            retrieveArticle(articlesList)
        }
        // save the current time of storing to a database
        sharedPrefHelper.saveTimeOfUpd(System.nanoTime())
    }

    //TODO implement check if articles were read
    private fun setArticlesHaveBeenRead(articlesFromAPI: List<Article>): List<Article>{
        Log.i("HereSetAll", "setArticlesHaveBeenRead: ")
        return articlesFromAPI
    }

    // get data by old fashion parser
    // parse for article details and add an article to articleDataSet
    private fun parseArticleDetails(articleJSON: String): Article? {
        var article: Article? = null
        try {
            val jsonObject = JSONObject(articleJSON)
            val id = jsonObject.getString("id")
            val title = jsonObject.getString("title")
            val author = jsonObject.getString("by")
            val url = jsonObject.getString("url")
            val data = jsonObject.getString("time")
            val score = jsonObject.getString("score")

            article = Article(id, title, author, url, data, score)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return article
    }

    // parse article HTML
    fun parseHTML(urlString: String?): String? {
        return parseService.parse(urlString)
    }

    private fun convertMinToNanosec(min: Int): Long {
        return min * 60 * 1000 * 1000 * 1000L
    }

    private fun timeToRefreshFromAPI(timeToUpd: Long?): String {
        val timeLeftSec = timeToUpd?.minus(System.nanoTime())?.div(1000_000_000.0.toLong())
        val formatted: String = "${(timeLeftSec?.div(60)).toString().padStart(2, '0')} min : ${(timeLeftSec?.rem(
            60
        )).toString().padStart(2, '0')} sec"
        return formatted
    }
}
