package com.harnet.devnewsradar.service

import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.ExecutionException

interface Parsable {
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
    private fun getAticlesIds(content: String, articlesToShow: Int): MutableList<URL> {
        val articlesURLs: MutableList<URL> = ArrayList()

        try {
            val jsonArray = JSONArray(content)
            for (i in 0 until articlesToShow) {
                val articleId = jsonArray.getString(i).toInt()
                // ! think about do it final or move to ENUM
                val atricleURL =
                    URL("https://hacker-news.firebaseio.com/v0/item/$articleId.json?print=pretty")
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

    // images parser
    fun parseImages(pageContent: String?): List<String>? {
        val imagesURL = mutableListOf<String>()
        val charSequence = pageContent as CharSequence
        val matcher = "http(.*?)jpg".toRegex()
        val articleImage = matcher.findAll(charSequence).map { it.value }.toList()

        imagesURL.add(articleImage[0])

        return imagesURL
    }
}