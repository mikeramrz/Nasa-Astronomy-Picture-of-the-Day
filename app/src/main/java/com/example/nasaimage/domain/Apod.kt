package com.example.nasaimage.domain

data class Apod(
    val copyright: String? = "",
    val date: String,
    val explanation: String,
    val hdurl: String? = "",
    val mediaType: String,
    val title: String,
    val url: String
)