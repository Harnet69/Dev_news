package com.harnet.devnews.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.devnews.model.Article
import com.harnet.devnews.model.ArticleLists
import com.harnet.devnews.service.ParseService
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.CompletableFuture

class ArticlesListViewModel : ViewModel() {
    private val ARTICLES_TO_SHOW = 20

    private val parseService: ParseService = ParseService()

    val articlesLists = ArticleLists()

    val mArticles = MutableLiveData<List<Article>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data
    fun refresh() {
        val article1 = Article(
            "1",
            "This electrical transmission tower has a problem (twitter.com)",
            "Twi",
            "",
            "54",
            "4"
        )
        val articles = listOf(article1)

        mArticles.value = articles
        mIsArticleLoadError.value = false
        mIsLoading.value = false

        makeArticlesList(articlesLists.NEW_STORIES)
    }


    // generate articles list from webController data
    private fun makeArticlesList(articlesList: String) {
        CompletableFuture.supplyAsync{
            parseService.getArticlesURLs(articlesList)
        }
            .thenAccept { URLs: List<URL> ->
                // waiting spinner appears
//                mIsUpdating.postValue(true)
                //clear the dataSet list
//                articlesDataSet.clear()
                try {
                    for (i in 0 until URLs.size) {
                        val article: String = parseService.parse(URLs.get(i).toString())
                        Log.i("Parsers", "makeArticlesList: " +parseArticleDetails(article))

                        //set the quantity of showed articles
                        if (i >= ARTICLES_TO_SHOW - 1) {
                            // spinner disappears
//                            mIsUpdating.postValue(false)
                            return@thenAccept
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

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
//            articlesDataSet.add(Article(title, author, url, data, score))
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