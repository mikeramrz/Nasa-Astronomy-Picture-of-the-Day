package com.example.nasaimage

import com.example.nasaimage.domain.Apod

sealed interface    ApodUiState {
     data object Loading : ApodUiState
    data class Success(val apod: Apod) : ApodUiState
    data class Error(val message: String) : ApodUiState
}