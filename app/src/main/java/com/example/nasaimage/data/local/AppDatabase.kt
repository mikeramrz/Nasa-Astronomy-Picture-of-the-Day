package com.example.nasaimage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ApodEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
}