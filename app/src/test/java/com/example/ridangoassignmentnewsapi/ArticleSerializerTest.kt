package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.util.ArticleSerializer
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleSerializerTest {

    private val testArticle = Article(
        sourceName = "BBC News",
        author = "John Doe",
        title = "Breaking News",
        description = "Something important happened",
        url = "https://bbc.com/article",
        urlToImage = "https://bbc.com/image.jpg",
        publishedAt = "2024-01-15T10:30:00Z",
        content = "Full article content here"
    )

    @Test
    fun `protobuf round-trip preserves all fields`() {
        val bytes = ArticleSerializer.toProtobufBytes(testArticle)
        val restored = ArticleSerializer.fromProtobufBytes(bytes)

        assertEquals(testArticle.sourceName, restored.sourceName)
        assertEquals(testArticle.author, restored.author)
        assertEquals(testArticle.title, restored.title)
        assertEquals(testArticle.description, restored.description)
        assertEquals(testArticle.url, restored.url)
        assertEquals(testArticle.urlToImage, restored.urlToImage)
        assertEquals(testArticle.publishedAt, restored.publishedAt)
        assertEquals(testArticle.content, restored.content)
    }

    @Test
    fun `protobuf bytes are non-empty`() {
        val bytes = ArticleSerializer.toProtobufBytes(testArticle)
        assert(bytes.isNotEmpty())
    }

    @Test
    fun `protobuf round-trip with full article equality`() {
        val bytes = ArticleSerializer.toProtobufBytes(testArticle)
        val restored = ArticleSerializer.fromProtobufBytes(bytes)
        assertEquals(testArticle, restored)
    }

    @Test
    fun `handles empty fields`() {
        val emptyArticle = Article(
            sourceName = "",
            author = "",
            title = "Title",
            description = "",
            url = "",
            urlToImage = "",
            publishedAt = "",
            content = ""
        )

        val bytes = ArticleSerializer.toProtobufBytes(emptyArticle)
        val restored = ArticleSerializer.fromProtobufBytes(bytes)

        assertEquals(emptyArticle, restored)
    }
}
