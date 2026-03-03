package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.data.repository.HeadlinesResult
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepository
import com.example.ridangoassignmentnewsapi.domain.model.Article

class FakeNewsRepository : NewsRepository {

    var articlesToReturn: List<Article> = emptyList()
    var totalResults: Int = 0
    var errorToThrow: Exception? = null
    var callCount = 0

    override suspend fun getTopHeadlines(page: Int, pageSize: Int): Result<HeadlinesResult> {
        callCount++
        errorToThrow?.let { return Result.failure(it) }
        return Result.success(HeadlinesResult(articlesToReturn, totalResults))
    }
}
