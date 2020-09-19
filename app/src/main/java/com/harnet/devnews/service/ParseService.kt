package com.harnet.devnews.service

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

    //get list of articles URL
    fun getArticlesURLs(url: String?): List<URL> {
        val articlesURLs: MutableList<URL> = ArrayList()

        // get content from a page
        var content: String? = null
        try {
            content = WebContentDownloader().execute(url).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        // get articles id
        content?.let {
            try {
                val jsonArray = JSONArray(content)
                for (i in 0 until jsonArray.length()) {
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
        }

        return articlesURLs
    }

    // parse site content
    fun parse(urlString: String?): String {
        val site = StringBuilder()

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            val `in` = connection.inputStream
            val reader = InputStreamReader(`in`)
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
}