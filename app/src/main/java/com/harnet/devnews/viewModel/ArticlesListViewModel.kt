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
        makeArticlesList(articlesLists.NEW_STORIES)
    }

    // generate articles list from webController data
    private fun makeArticlesList(articlesList: String) {
        val parsedArticlesList = mutableListOf<Article>()
        CompletableFuture.supplyAsync{
            parseService.getArticlesURLs(articlesList)
        }
            .thenAccept { URLs: List<URL> ->
                try {
                    for (i in 0 until ARTICLES_TO_SHOW) {
                        val article: String = parseService.parse(URLs.get(i).toString())
                        val parsedArticle: Article? = parseArticleDetails(article)
//                        Log.i("Parsers", "makeArticlesList: " +parsedArticle)
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