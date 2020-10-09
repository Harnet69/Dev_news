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

    @Query("DELETE FROM favourite WHERE uuid = :uuId")
    suspend fun deleteFavourite(uuId: Int)

    @Query("DELETE FROM favourite WHERE id = :id")
    suspend fun deleteFavourite(id: String)

    @Query("SELECT * FROM favourite WHERE uuid = :uuId")
        suspend fun getFavourite(uuId: Int): Favourite
}