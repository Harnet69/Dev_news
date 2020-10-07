package com.harnet.devnewsradar.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArticleDAO {
    @Insert
    suspend fun insertAll(vararg article: Article): List<Long>

    @Query("SELECT * FROM article")
    suspend fun getArticles(): List<Article>

    @Query("DELETE FROM article")
    suspend fun deleteArticles()

    @Query("SELECT * FROM article WHERE uuid = :articleId")
    suspend fun getArticle(articleId: Int): Article
}