package com.harnet.devnewsradar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Favourite(
    id: String,
    title: String,
    author: String,
    url: String,
    time: String,
    score: String,
) : Article(id, title, author, url, time, score)