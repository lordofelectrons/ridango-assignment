package com.example.ridangoassignmentnewsapi.data.repository

import com.example.ridangoassignmentnewsapi.BuildConfig
import com.example.ridangoassignmentnewsapi.data.remote.ApiErrorResponse
import com.example.ridangoassignmentnewsapi.data.remote.NewsApiService
import com.example.ridangoassignmentnewsapi.domain.model.toDomain
import com.google.gson.Gson
import retrofit2.HttpException

class NewsRepositoryImpl(
    private val apiService: NewsApiService
) : NewsRepository {

    override suspend fun getTopHeadlines(page: Int, pageSize: Int): Result<HeadlinesResult> {
        return try {
            val response = apiService.getTopHeadlines(
                page = page,
                pageSize = pageSize,
                apiKey = BuildConfig.NEWS_API_KEY
            )
            val articles = response.articles.mapNotNull { it.toDomain() }
            Result.success(HeadlinesResult(articles, response.totalResults))
        } catch (e: HttpException) {
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val apiError = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
            apiError?.message ?: "HTTP ${e.code()}"
        } catch (_: Exception) {
            "HTTP ${e.code()}"
        }
    }
}
