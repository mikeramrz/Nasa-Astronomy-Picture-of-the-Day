package com.example.nasaimage.data

import android.util.Log
import com.example.nasaimage.data.local.ApodEntity
import com.example.nasaimage.data.model.ApodModel
import com.example.nasaimage.domain.Apod

fun ApodModel.toEntity(): ApodEntity = ApodEntity.fromModel(this)

fun ApodEntity.toDomainModel(): Apod {
    return Apod(
        copyright = this.copyright,
        date = this.date,
        explanation = this.explanation,
        hdurl = this.hdurl,
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}