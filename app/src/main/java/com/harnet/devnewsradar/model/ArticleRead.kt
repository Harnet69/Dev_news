package com.harnet.devnewsradar.model

import androidx.room.Entity

@Entity
class ArticleRead(
    id: String,
    title: String,
    author: String,
    url: String,
    time: String,
    score: String,
    val timeWhenRead: Long
): Article(id, title, author, url, time, score)