package com.harnet.devnews.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.devnews.model.Article
import com.harnet.devnews.service.ArticleApiServis
import com.harnet.devnews.model.ArticleLists
import com.harnet.devnews.service.ParseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.CompletableFuture

class ArticlesListViewModel : ViewModel() {
    private val ARTICLES_TO_SHOW: Int = 20

    // old fashion parse service
    private val parseService: ParseService = ParseService()

    // Retrofit service
    private val articleApiService = ArticleApiServis()
    private val disposable = CompositeDisposable()

    private val articlesLists = ArticleLists()

    val mArticles = MutableLiveData<List<Article>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()
    //TODO implement here observable articles list switcher

    // refresh mArticles with a new data TWO WAYS TO DO IT: PARSER & RETROFIT
    fun refresh() {
        mIsArticleLoadError.value = false//TODO think is it necessary
        // get data by old fashion manner parser
//        makeArticlesListByParser(articlesLists.NEW_STORIES)

//        get data by retrofit
        fetchFromRemote(articlesLists.NEW_STORIES)
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
                    articleApiService.getArticle(articlesIDs.get(i))
                        .subscribeOn(Schedulers.newThread())
                        // retrieve it from a background to the main thread for displaying
                        .observeOn(AndroidSchedulers.mainThread())
                        // pass our Single object here, it's created and implemented
                        .subscribeWith(object : DisposableSingleObserver<Article>() {
                            // get list of DogBreed objects
                            override fun onSuccess(article: Article) {
                                if(i != articlesIDs.size-1 ){
                                    // add article to a list
                                    articlesFromAPI.add(article)
                                }else{
                                    // set received list to observable mutable list
                                    mArticles.value = articlesFromAPI
                                    // switch off error message
                                    mIsArticleLoadError.value = false
                                    // switch off waiting spinner
                                    mIsLoading.value = false
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
        val parsedArticlesList = mutableListOf<Article>()
        CompletableFuture.supplyAsync {
            parseService.getArticlesURLs(articlesList, ARTICLES_TO_SHOW)
        }
            .thenAccept { URLs: MutableList<URL>? ->
                try {
                    for (i in 0 until ARTICLES_TO_SHOW) {
                        val article: String = parseService.parse(URLs?.get(i).toString())
                        val parsedArticle: Article? = parseArticleDetails(article)
                        parsedArticle?.let { parsedArticlesList.add(it) }
                    }
                    // change values
                    mArticles.postValue(parsedArticlesList)
                    mIsArticleLoadError.postValue(false)
                    mIsLoading.postValue(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    // get data by old fashion parser
    // parse for article details and add an article to articleDataSet
    //TODO here we can add another fields for an article
    fun parseArticleDetails(articleJSON: String): Article? {
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
}