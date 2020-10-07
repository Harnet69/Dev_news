package com.harnet.devnewsradar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favourite(
    @ColumnInfo
    var id: String,

    @ColumnInfo
    var title: String,

    @ColumnInfo
    var author: String,

    @ColumnInfo
    var url: String,

    @ColumnInfo
    var time: String,

    @ColumnInfo
    var score: String
){
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0

    var imageUrl: String = ""
}