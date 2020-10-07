package com.harnet.devnewsradar.model

data class ArticleLists(
    val TOP_STORIES: String = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty",
    val BEST_STORIES: String = "https://hacker-news.firebaseio.com/v0/beststories.json?print=pretty",
    val NEW_STORIES: String = "https://hacker-news.firebaseio.com/v0/newstories.json?print=pretty"
)