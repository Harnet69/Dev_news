package com.harnet.devnews.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ArticleAPI {
    // whole URL is https://hacker-news.firebaseio.com/v0/item/24517792.json

    // annotation used for knowing how this method can be used
    //TODO implement dynamic URLs
    @GET("v0/item/{articleId}.json")// !!!this is an end point, not all url
    fun getArticle(@Path("articleId") articleId: String ): Single<Article>

    // it can be several methods for different kinds of a data
}