package com.example.nasaimage

import android.content.Context
import androidx.room.Room
import com.example.nasaimage.data.ApodRepository
import com.example.nasaimage.data.local.ApodDao
import com.example.nasaimage.data.local.AppDatabase
import com.example.nasaimage.data.remote.ApiService
import com.example.nasaimage.data.remote.RetrofitInstance
import com.example.nasaimage.domain.FetchNasaImageOftheDayUseCase
import com.example.nasaimage.domain.IApodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitInstance.api
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "apod_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideApodDao(appDatabase: AppDatabase) = appDatabase.apodDao()

    @Provides
    @Singleton
    fun provideApodRepository(apiService: ApiService, apodDao: ApodDao): IApodRepository =
        ApodRepository(apiService, apodDao)


    @Provides
    @Singleton
    fun provideFetchNasaImageOfTheDayUseCase(apodRepository: IApodRepository) =
        FetchNasaImageOftheDayUseCase(apodRepository)
}