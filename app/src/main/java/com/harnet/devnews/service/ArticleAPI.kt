package com.harnet.devnews.service

import com.harnet.devnews.model.Article
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleAPI {
    // example of article URL: https://hacker-news.firebaseio.com/v0/item/24517792.json

    // annotation used for knowing how this method can be used
    //dynamic URLs
    @GET("v0/item/{articleId}.json")// !!!this is an end point, not all url
    fun getArticle(@Path("articleId") articleId: String ): Single<Article>
}