package com.harnet.devnews.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Article(
    @ColumnInfo
    @SerializedName("id")
    var id: String,

    @ColumnInfo
    @SerializedName("title")
    var title: String,

    @ColumnInfo
    @SerializedName("by")
    var author: String,

    @ColumnInfo
    @SerializedName("url")
    var url: String,

    @ColumnInfo
    @SerializedName("time")
    var time: String,

    @ColumnInfo
    @SerializedName("score")
    var score: String
){
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0

    var isFavourite: Boolean = false

    var imageUrl: String = ""
}