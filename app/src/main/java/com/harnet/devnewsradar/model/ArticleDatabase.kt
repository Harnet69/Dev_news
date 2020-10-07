package com.harnet.devnewsradar.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Article::class, Favourite:: class), version = 1)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun articleDAO(): ArticleDAO
    abstract fun favouriteDAO(): FavouriteDAO

    companion object{
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // create an instance of Articles Database
            instance ?: buildDatabase(context).also {
                // attach an instance to variable and return an instance to invoker
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "articlesDatabase"
        ).build()
    }
}