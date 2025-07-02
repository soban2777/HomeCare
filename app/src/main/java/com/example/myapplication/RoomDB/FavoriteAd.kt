package com.example.myapplication.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_ads")
data class FavoriteAd(
    @PrimaryKey (autoGenerate = true)
    val id:Int=0,
    val adId: String, // Firestore doc ID
    val userType: Int,                  // 0 = Customer, 1 = Worker
) {

}