package com.example.ridangoassignmentnewsapi.data.repository

import com.example.ridangoassignmentnewsapi.domain.model.Article

interface NewsRepository {
    suspend fun getTopHeadlines(page: Int, pageSize: Int = 21): Result<List<Article>>
}
