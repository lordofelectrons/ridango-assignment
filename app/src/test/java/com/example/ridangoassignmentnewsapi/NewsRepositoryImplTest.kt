package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.data.remote.ArticleDto
import com.example.ridangoassignmentnewsapi.data.remote.NewsResponse
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepositoryImpl
import com.example.ridangoassignmentnewsapi.domain.model.toDomain
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewsRepositoryImplTest {

    @Test
    fun `maps response to domain articles`() = runTest {
        val fakeService = FakeNewsApiService()
        fakeService.response = NewsResponse(
            status = "ok",
            totalResults = 2,
            articles = listOf(
                FakeNewsApiService.createArticleDto(title = "First Article"),
                FakeNewsApiService.createArticleDto(title = "Second Article")
            )
        )
        val repository = NewsRepositoryImpl(fakeService)

        val result = repository.getTopHeadlines(page = 1)

        assertTrue(result.isSuccess)
        val headlinesResult = result.getOrThrow()
        assertEquals(2, headlinesResult.articles.size)
        assertEquals("First Article", headlinesResult.articles[0].title)
        assertEquals("Second Article", headlinesResult.articles[1].title)
        assertEquals(2, headlinesResult.totalResults)
    }

    @Test
    fun `filters out removed articles`() = runTest {
        val fakeService = FakeNewsApiService()
        fakeService.response = NewsResponse(
            status = "ok",
            totalResults = 2,
            articles = listOf(
                FakeNewsApiService.createArticleDto(title = "Valid Article"),
                FakeNewsApiService.createArticleDto(title = "[Removed]")
            )
        )
        val repository = NewsRepositoryImpl(fakeService)

        val result = repository.getTopHeadlines(page = 1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().articles.size)
    }

    @Test
    fun `handles network error`() = runTest {
        val fakeService = FakeNewsApiService()
        fakeService.errorToThrow = RuntimeException("Connection failed")
        val repository = NewsRepositoryImpl(fakeService)

        val result = repository.getTopHeadlines(page = 1)

        assertTrue(result.isFailure)
        assertEquals("Connection failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handles null fields with defaults`() = runTest {
        val fakeService = FakeNewsApiService()
        fakeService.response = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                FakeNewsApiService.createArticleDto(
                    title = "Title Only",
                    author = null,
                    description = null,
                    url = null,
                    urlToImage = null,
                    content = null
                )
            )
        )
        val repository = NewsRepositoryImpl(fakeService)

        val result = repository.getTopHeadlines(page = 1)

        assertTrue(result.isSuccess)
        val article = result.getOrThrow().articles.first()
        assertEquals("Title Only", article.title)
        assertEquals("", article.author)
        assertEquals("", article.description)
        assertEquals("", article.url)
        assertEquals("", article.urlToImage)
        assertEquals("", article.content)
    }

    // --- toDomain content stripping tests ---

    @Test
    fun `toDomain strips trailing chars marker from content`() {
        val dto = FakeNewsApiService.createArticleDto(
            content = "Some article text here... [+1234 chars]"
        )
        val article = dto.toDomain()!!
        assertEquals("Some article text here...", article.content)
    }

    @Test
    fun `toDomain strips chars marker in middle of content`() {
        val dto = FakeNewsApiService.createArticleDto(
            content = "Part one [+500 chars] part two"
        )
        val article = dto.toDomain()!!
        assertEquals("Part one  part two", article.content)
    }

    @Test
    fun `toDomain returns empty for content that is only chars marker`() {
        val dto = FakeNewsApiService.createArticleDto(
            content = "[+999 chars]"
        )
        val article = dto.toDomain()!!
        assertEquals("", article.content)
    }

    @Test
    fun `toDomain returns null for null title`() {
        val dto = FakeNewsApiService.createArticleDto(title = "placeholder").copy(title = null)
        assertNull(dto.toDomain())
    }
}
