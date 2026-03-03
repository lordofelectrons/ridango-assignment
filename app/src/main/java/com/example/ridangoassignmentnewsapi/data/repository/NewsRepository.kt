package com.example.ridangoassignmentnewsapi.data.repository

import com.example.ridangoassignmentnewsapi.domain.model.Article

data class HeadlinesResult(
    val articles: List<Article>,
    val totalResults: Int
)

interface NewsRepository {
    suspend fun getTopHeadlines(page: Int, pageSize: Int = 20): Result<HeadlinesResult>
}
