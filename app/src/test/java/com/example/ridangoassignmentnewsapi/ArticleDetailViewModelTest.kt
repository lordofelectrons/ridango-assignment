package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.ui.screens.articledetail.ArticleDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleDetailViewModelTest {

    private fun createArticle() = Article(
        sourceName = "BBC",
        author = "John",
        title = "Test Article",
        description = "Description",
        url = "https://example.com",
        urlToImage = "https://example.com/img.jpg",
        publishedAt = "2024-01-01T00:00:00Z",
        content = "Full content here"
    )

    @Test
    fun `initial state has no article and not saved`() {
        val viewModel = ArticleDetailViewModel()

        assertNull(viewModel.uiState.value.article)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `setArticle populates article in state`() {
        val viewModel = ArticleDetailViewModel()
        val article = createArticle()

        viewModel.setArticle(article)

        assertEquals(article, viewModel.uiState.value.article)
    }

    @Test
    fun `setArticle preserves all fields`() {
        val viewModel = ArticleDetailViewModel()
        val article = createArticle()

        viewModel.setArticle(article)

        val result = viewModel.uiState.value.article!!
        assertEquals("BBC", result.sourceName)
        assertEquals("John", result.author)
        assertEquals("Test Article", result.title)
        assertEquals("Description", result.description)
        assertEquals("https://example.com", result.url)
        assertEquals("https://example.com/img.jpg", result.urlToImage)
        assertEquals("2024-01-01T00:00:00Z", result.publishedAt)
        assertEquals("Full content here", result.content)
    }

    @Test
    fun `saveArticle without article does not crash or change state`() {
        val viewModel = ArticleDetailViewModel()

        viewModel.saveArticle() // no article set

        assertFalse(viewModel.uiState.value.isSaved)
    }
}
