package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.ui.screens.newslist.NewsListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeNewsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNewsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createArticles(count: Int): List<Article> {
        return (1..count).map { i ->
            Article(
                sourceName = "Source $i",
                author = "Author $i",
                title = "Title $i",
                description = "Description $i",
                url = "https://example.com/$i",
                urlToImage = "https://example.com/image$i.jpg",
                publishedAt = "2024-01-0${i}T00:00:00Z",
                content = "Content $i"
            )
        }
    }

    @Test
    fun `initial load success populates articles`() = runTest {
        val articles = createArticles(21)
        fakeRepository.articlesToReturn = articles

        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(21, state.articles.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.hasMorePages)
    }

    @Test
    fun `initial load failure sets error`() = runTest {
        fakeRepository.errorToThrow = RuntimeException("Network error")

        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.articles.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `loadNextPage appends articles`() = runTest {
        fakeRepository.articlesToReturn = createArticles(21)
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = createArticles(10)
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(31, state.articles.size)
        assertEquals(2, state.currentPage)
        assertFalse(state.hasMorePages) // 10 < 21, so no more pages
    }

    @Test
    fun `loadNextPage does nothing when no more pages`() = runTest {
        fakeRepository.articlesToReturn = createArticles(10)
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        val callCountBefore = fakeRepository.callCount
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(callCountBefore, fakeRepository.callCount)
    }

    @Test
    fun `loadNextPage does not duplicate when already loading`() = runTest {
        fakeRepository.articlesToReturn = createArticles(21)
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = createArticles(21)
        viewModel.loadNextPage()
        viewModel.loadNextPage() // should be ignored
        advanceUntilIdle()

        assertEquals(3, fakeRepository.callCount) // 1 initial + 1 next page (second call ignored)
    }
}
