package com.harnet.devnews.model

import androidx.room.Insert
import androidx.room.Query


interface ArticleDAO {
    @Insert
    suspend fun insertAll(vararg article: Article): List<Long>

    @Query("SELECT * FROM article")
    suspend fun getArticles(): List<Article>

    @Query("SELECT * FROM article WHERE uuid = :articleId")
    suspend fun getArticle(articleId: Int): Article
}