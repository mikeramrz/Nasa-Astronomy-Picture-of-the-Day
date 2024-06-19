package com.example.nasaimage.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNasaImageOftheDayUseCase @Inject
constructor(private val apodRepository: IApodRepository) {
    suspend operator fun invoke(date: String): Flow<Result<Apod>> =
        apodRepository.fetchNasaImageOfTheDay(date)
}