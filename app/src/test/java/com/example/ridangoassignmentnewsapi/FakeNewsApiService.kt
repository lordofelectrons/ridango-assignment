package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.data.remote.ArticleDto
import com.example.ridangoassignmentnewsapi.data.remote.NewsApiService
import com.example.ridangoassignmentnewsapi.data.remote.NewsResponse
import com.example.ridangoassignmentnewsapi.data.remote.SourceDto

class FakeNewsApiService : NewsApiService {

    var response: NewsResponse = NewsResponse(
        status = "ok",
        totalResults = 0,
        articles = emptyList()
    )
    var errorToThrow: Exception? = null

    override suspend fun getTopHeadlines(
        country: String,
        pageSize: Int,
        page: Int,
        apiKey: String
    ): NewsResponse {
        errorToThrow?.let { throw it }
        return response
    }

    companion object {
        fun createArticleDto(
            title: String = "Test Title",
            sourceName: String = "Test Source",
            author: String? = "Test Author",
            description: String? = "Test Description",
            url: String? = "https://example.com",
            urlToImage: String? = "https://example.com/image.jpg",
            publishedAt: String? = "2024-01-01T00:00:00Z",
            content: String? = "Test content"
        ) = ArticleDto(
            source = SourceDto(id = null, name = sourceName),
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content
        )
    }
}
