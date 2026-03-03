package com.example.ridangoassignmentnewsapi.domain.model

import com.example.ridangoassignmentnewsapi.data.remote.ArticleDto

data class Article(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)

fun ArticleDto.toDomain(): Article? {
    if (title == null || title == "[Removed]") return null
    return Article(
        sourceName = source?.name ?: "",
        author = author ?: "",
        title = title,
        description = description ?: "",
        url = url ?: "",
        urlToImage = urlToImage ?: "",
        publishedAt = publishedAt ?: "",
        content = (content ?: "").replace(Regex("\\[\\+\\d+ chars]"), "").trim()
    )
}
