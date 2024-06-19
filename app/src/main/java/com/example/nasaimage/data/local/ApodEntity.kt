package com.example.nasaimage.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nasaimage.data.model.ApodModel

@Entity(tableName = "apod")
data class ApodEntity(
    val copyright: String?,
    @PrimaryKey
    val date: String,
    val explanation: String,
    val hdurl: String?,
    val mediaType: String,
    val serviceVersion: String,
    val title: String,
    val url: String
){
    companion object {
        fun fromModel(apodModel: ApodModel): ApodEntity {
            return ApodEntity(
                copyright = apodModel.copyRight,
                date = apodModel.date,
                explanation = apodModel.explanation,
                hdurl = apodModel.hdurl,
                mediaType = apodModel.mediaType,
                serviceVersion = apodModel.serviceVersion,
                title = apodModel.title,
                url = apodModel.url
            )
        }
    }
}