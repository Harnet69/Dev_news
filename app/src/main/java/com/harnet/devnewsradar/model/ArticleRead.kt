package com.harnet.devnewsradar.model

import androidx.room.Entity

@Entity
class ArticleRead(
    id: String,
    title: String,
    author: String,
    url: String,
    time: String,
    score: String
): Article(id, title, author, url, time, score)