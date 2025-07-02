package com.example.myapplication.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteAd::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteAdDao(): FavoriteAdDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context, AppDatabase::class.java, "favorite_ads"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return INSTANCE!!
        }
    }
}