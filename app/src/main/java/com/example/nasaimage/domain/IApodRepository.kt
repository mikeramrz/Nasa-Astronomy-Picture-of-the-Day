package com.example.nasaimage.domain

import kotlinx.coroutines.flow.Flow


interface IApodRepository {

    suspend fun fetchNasaImageOfTheDay(date: String): Flow<Result<Apod>>
}