package com.example.ridangoassignmentnewsapi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ridangoassignmentnewsapi.data.repository.HeadlinesResult
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepository
import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.ui.screens.newslist.NewsListScreen
import com.example.ridangoassignmentnewsapi.ui.screens.newslist.NewsListViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NewsListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestArticles(count: Int = 3): List<Article> {
        return (1..count).map { i ->
            Article(
                sourceName = "Source $i",
                author = "Author $i",
                title = "Article Title $i",
                description = "Description $i",
                url = "https://example.com/$i",
                urlToImage = "",
                publishedAt = "2024-01-0${i}T00:00:00Z",
                content = "Content $i"
            )
        }
    }

    private fun createFakeRepository(
        articles: List<Article> = emptyList(),
        error: Exception? = null
    ): NewsRepository {
        return object : NewsRepository {
            override suspend fun getTopHeadlines(page: Int, pageSize: Int): Result<HeadlinesResult> {
                error?.let { return Result.failure(it) }
                return Result.success(HeadlinesResult(articles, articles.size))
            }
        }
    }

    @Test
    fun displaysArticlesAfterLoading() {
        val articles = createTestArticles()
        val viewModel = NewsListViewModel(createFakeRepository(articles = articles))

        composeTestRule.setContent {
            NewsListScreen(viewModel = viewModel, onArticleClick = {})
        }

        composeTestRule.onNodeWithText("Article Title 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Article Title 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Article Title 3").assertIsDisplayed()
    }

    @Test
    fun displaysSourceOnCard() {
        val articles = createTestArticles(1)
        val viewModel = NewsListViewModel(createFakeRepository(articles = articles))

        composeTestRule.setContent {
            NewsListScreen(viewModel = viewModel, onArticleClick = {})
        }

        composeTestRule.onNodeWithText("Source 1").assertIsDisplayed()
    }

    @Test
    fun clickArticleCallsCallback() {
        val articles = createTestArticles(1)
        var clickedArticle: Article? = null
        val viewModel = NewsListViewModel(createFakeRepository(articles = articles))

        composeTestRule.setContent {
            NewsListScreen(
                viewModel = viewModel,
                onArticleClick = { clickedArticle = it }
            )
        }

        composeTestRule.onNodeWithText("Article Title 1").performClick()
        assertEquals("Article Title 1", clickedArticle?.title)
    }

    @Test
    fun displaysErrorWithRetryButton() {
        val viewModel = NewsListViewModel(
            createFakeRepository(error = RuntimeException("Network error"))
        )

        composeTestRule.setContent {
            NewsListScreen(viewModel = viewModel, onArticleClick = {})
        }

        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun displaysTopHeadlinesTitle() {
        val viewModel = NewsListViewModel(createFakeRepository(articles = createTestArticles()))

        composeTestRule.setContent {
            NewsListScreen(viewModel = viewModel, onArticleClick = {})
        }

        composeTestRule.onNodeWithText("Top Headlines").assertIsDisplayed()
    }
}
