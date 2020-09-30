package com.harnet.devnews.model

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class ArticleDatabase: RoomDatabase() {
    abstract fun articleDAO(): ArticleDAO

    companion object{
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // create an instance of DogDatabase
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