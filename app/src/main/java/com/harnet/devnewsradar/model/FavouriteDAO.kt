package com.harnet.devnewsradar.model

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

    @Query("DELETE FROM favourite WHERE uuid = :favouriteId" )
    suspend fun deleteFavourite(favouriteId: Int)

    @Query("DELETE FROM favourite WHERE id = :favouriteId" )
    suspend fun deleteFavourite(favouriteId: String)

    @Query("SELECT * FROM favourite WHERE uuid = :favouriteUuId")
        suspend fun getFavourite(favouriteUuId: Int): Favourite
}