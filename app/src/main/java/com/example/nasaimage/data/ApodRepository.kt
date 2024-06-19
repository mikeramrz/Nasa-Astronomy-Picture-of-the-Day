package com.example.nasaimage.data

import android.util.Log
import com.example.nasaimage.data.local.ApodDao
import com.example.nasaimage.data.local.ApodEntity
import com.example.nasaimage.data.remote.ApiService
import com.example.nasaimage.domain.Apod
import com.example.nasaimage.domain.IApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ApodRepository
@Inject constructor(
    private val apiService: ApiService,
    private val apodDao: ApodDao,

    ) : IApodRepository {

    // YOUR API KEY HERE or have it live somewhere else
    //private val API_KEY = ""

    override suspend fun fetchNasaImageOfTheDay(date: String): Flow<Result<Apod>> = flow {
        // First check the local database
        val localApod = apodDao.getApodByDate(date).firstOrNull()
        if (localApod != null) {
            emit(Result.success(localApod.toDomainModel()))
        } else {
            // If not found locally, fetch from the API
            try {
                val response = apiService.getNasaImageOfTheDayByDate(API_KEY, date)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let { apod ->
                        apodDao.insertApod(apod.toEntity())
                        emit(Result.success(apod.toEntity().toDomainModel()))
                    } ?: throw Exception("Response body is null")
                } else {
                    throw Exception("Failed to fetch APOD: ${response.message()}")
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }
}