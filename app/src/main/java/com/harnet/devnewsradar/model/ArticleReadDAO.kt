package com.harnet.devnewsradar.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArticleReadDAO {
    @Insert
    suspend fun insertAll(vararg articleRead: ArticleRead): List<Long>

    @Query("SELECT * FROM articleRead ORDER BY timeWhenRead DESC")
    suspend fun getArticles(): List<ArticleRead>

    @Query("DELETE FROM articleRead")
    suspend fun deleteArticles()

    @Query("DELETE FROM articleRead WHERE timeWhenRead < :deadLine")
    suspend fun deleteOldArticles(deadLine: Long)

    @Query("SELECT * FROM articleRead WHERE uuid = :uuid")
    suspend fun getArticle(uuid: Int): ArticleRead

    @Query("SELECT EXISTS(SELECT * FROM articleRead WHERE id = :id)")
    fun isExists(id: String): Boolean

    @Query("UPDATE ArticleRead SET timeWhenRead = :timeWhenRead WHERE id = :articleId")
    fun updateTimeWhenRead(articleId:String, timeWhenRead: Long?): Int
}