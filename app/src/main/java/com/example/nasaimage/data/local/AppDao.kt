package com.example.nasaimage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    @Query("SELECT * FROM apod WHERE date = :date LIMIT 1")
    fun getApodByDate(date: String) : Flow<ApodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertApod(apodEntity: ApodEntity)
}