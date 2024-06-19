package com.example.nasaimage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaimage.domain.FetchNasaImageOftheDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NasaImageViewModel
@Inject constructor(
    private val fetchNasaImageOftheDayUseCase: FetchNasaImageOftheDayUseCase

) : ViewModel() {

    private val _apodUiState = MutableStateFlow<ApodUiState>(ApodUiState.Loading)
    val apodUiState = _apodUiState.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
        initialValue = ApodUiState.Loading
    )


    fun fetchApod(date: String) {
        viewModelScope.launch {
            try {
                fetchNasaImageOftheDayUseCase(date).collect { result ->
                    result.fold(
                        onSuccess = { apod ->
                            _apodUiState.value = ApodUiState.Success(apod)
                        },
                        onFailure = { error ->
                            _apodUiState.value =
                                ApodUiState.Error(error.message ?: "An unexpected error occurred")
                        }
                    )

                }

            } catch (e: Exception) {
                Log.d("NasaImageViewModel", "fetchApod: ${e.message}")
                _apodUiState.value = ApodUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}