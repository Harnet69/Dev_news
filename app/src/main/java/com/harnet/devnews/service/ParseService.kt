package com.harnet.devnews.service

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException

class ParseService {
    //TODO implement argument with max parsed articles list

    // get articles ids to Retrofit
    fun getArticlesIDs(url: String?, articlesToShow: Int): MutableList<String>? {
        val articlesIDs = mutableListOf<String>()
        // get content from a page
        var content: String? = null
        try {
            content = WebContentDownloader().execute(url).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        try {
            val jsonArray = JSONArray(content)
            for (i in 0 until articlesToShow) {
                articlesIDs.add(jsonArray.getString(i))
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return articlesIDs
    }

    //get list of articles URL for parser
    fun getArticlesURLs(url: String?, articlesToShow: Int): MutableList<URL>? {
        // get content from a page
        var content: String? = null
        try {
            content = WebContentDownloader().execute(url).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return content?.let { getAticlesIds(it, articlesToShow) }
    }

    // get articles ids for parser
    private fun getAticlesIds (content: String, articlesToShow: Int): MutableList<URL> {
        val articlesURLs: MutableList<URL> = ArrayList()

        try {
            val jsonArray = JSONArray(content)
            for (i in 0 until articlesToShow) {
                val articleId = jsonArray.getString(i).toInt()
                // TODO think about do it final or move to ENUM
                val atricleURL = URL("https://hacker-news.firebaseio.com/v0/item/$articleId.json?print=pretty")
                articlesURLs.add(atricleURL)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return articlesURLs
    }

    // parse site content
    fun parse(urlString: String?): String {
        val site = StringBuilder()

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            val iN = connection.inputStream
            val reader = InputStreamReader(iN)
            var data = reader.read()

            while (data != -1) {
                val current = data.toChar()
                site.append(current)
                data = reader.read()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return site.toString()
    }

    fun parseImages(pageContent: String?): List<String>? {
        var imagesURL = mutableListOf<String>()
//        println(pageContent)
        val testLink3 = pageContent as CharSequence
        val word = "jpg"
//        val matcher = "(?i)(?<!\\p{L})$word(?!\\p{L})".toRegex()
        val matcher = "http(.*?)jpg".toRegex()
        val articleImage = matcher.findAll(testLink3).map { it.value }.toList()
//        Log.i("JPGimages", "parseImages: "  + matcher.findAll(testLink3).map { it.value }.toList())
//        Log.i("JPGimages", "parseImages: counter" + matcher.findAll(testLink3).count() )
            // => [cBa, Cba, cbA]
            // => 3
        imagesURL.add(articleImage[0])

        return imagesURL
    }
}