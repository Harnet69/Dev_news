package com.harnet.devnews.model

import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("id")
    var id: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("by")
    var author: String,
    @SerializedName("url")
    var url: String,
    @SerializedName("time")
    var time: String,
    @SerializedName("score")
    var score: String
)