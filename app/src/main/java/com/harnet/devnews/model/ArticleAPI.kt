package com.harnet.devnews.model

import io.reactivex.Single
import retrofit2.http.GET

interface ArticleAPI {
    // whole URL is https://hacker-news.firebaseio.com/v0/item/24517792.json

    // annotation used for knowing how this method can be used
    @GET("v0/item/24517715.json?print=pretty")// !!!this is an end point, not all url
    fun getArticle(): Single<Article>

    // it can be several methods for different kinds of a data
}