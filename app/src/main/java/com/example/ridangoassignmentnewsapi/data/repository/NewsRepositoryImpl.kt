package com.example.ridangoassignmentnewsapi.data.repository

import com.example.ridangoassignmentnewsapi.BuildConfig
import com.example.ridangoassignmentnewsapi.data.remote.NewsApiService
import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.domain.model.toDomain

class NewsRepositoryImpl(
    private val apiService: NewsApiService
) : NewsRepository {

    override suspend fun getTopHeadlines(page: Int, pageSize: Int): Result<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines(
                page = page,
                pageSize = pageSize,
                apiKey = BuildConfig.NEWS_API_KEY
            )
            val articles = response.articles.mapNotNull { it.toDomain() }
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
