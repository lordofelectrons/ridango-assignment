package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.data.remote.NewsResponse
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        val articles = result.getOrThrow()
        assertEquals(2, articles.size)
        assertEquals("First Article", articles[0].title)
        assertEquals("Second Article", articles[1].title)
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
        assertEquals(1, result.getOrThrow().size)
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
        val article = result.getOrThrow().first()
        assertEquals("Title Only", article.title)
        assertEquals("", article.author)
        assertEquals("", article.description)
        assertEquals("", article.url)
        assertEquals("", article.urlToImage)
        assertEquals("", article.content)
    }
}
