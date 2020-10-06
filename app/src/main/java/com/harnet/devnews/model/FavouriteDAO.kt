package com.harnet.devnews.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouriteDAO {
    @Insert
    suspend fun insertAll(vararg favourite: Favourite): List<Long>

    @Query("SELECT * FROM favourite")
    suspend fun getFavourites(): List<Favourite>

    @Query("DELETE FROM favourite")
    suspend fun deleteFavourites()

    @Query("SELECT * FROM favourite WHERE uuid = :favouriteId")
    suspend fun getFavourite(favouriteId: Int): Favourite
}