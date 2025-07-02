package com.example.myapplication.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteAdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(ad: FavoriteAd)

    @Delete
    suspend fun removeFavorite(ad: FavoriteAd)

    @Query("SELECT * FROM favorite_ads")
    suspend fun getAllFavorites(): List<FavoriteAd>

    @Query("SELECT * FROM favorite_ads WHERE userType = :type")
    suspend fun getFavoritesByUserType(type: Int): List<FavoriteAd>

    @Query("SELECT * FROM favorite_ads WHERE adId = :id")
    suspend fun getFavoriteById(id: String): FavoriteAd?
}