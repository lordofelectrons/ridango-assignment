package com.example.ridangoassignmentnewsapi.di

import com.example.ridangoassignmentnewsapi.data.remote.NewsApiService
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepository
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val newsApiService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

    val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(newsApiService)
    }
}
