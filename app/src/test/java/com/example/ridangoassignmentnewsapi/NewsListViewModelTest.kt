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
        val articles = createArticles(20)
        fakeRepository.articlesToReturn = articles
        fakeRepository.totalResults = 50

        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(20, state.articles.size)
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
        assertEquals("Couldn't load articles. Network error", state.error)
    }

    @Test
    fun `loadNextPage appends articles`() = runTest {
        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 50
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = createArticles(10)
        fakeRepository.totalResults = 50
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(30, state.articles.size)
        assertEquals(2, state.currentPage)
        assertTrue(state.hasMorePages) // returned non-empty, so more might exist
    }

    @Test
    fun `loadNextPage stops when API returns empty`() = runTest {
        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 20
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = emptyList()
        fakeRepository.totalResults = 20
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(20, state.articles.size)
        assertFalse(state.hasMorePages)
    }

    @Test
    fun `loadNextPage does nothing when no more pages`() = runTest {
        // Initial load returns empty -> hasMorePages = false
        fakeRepository.articlesToReturn = emptyList()
        fakeRepository.totalResults = 0
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        val callCountBefore = fakeRepository.callCount
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(callCountBefore, fakeRepository.callCount)
    }

    @Test
    fun `loadNextPage does not duplicate when already loading`() = runTest {
        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 60
        val viewModel = NewsListViewModel(fakeRepository)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 60
        viewModel.loadNextPage()
        viewModel.loadNextPage() // should be ignored
        advanceUntilIdle()

        assertEquals(3, fakeRepository.callCount) // 1 initial + 1 next page (second call ignored)
    }

    @Test
    fun `disk cache is shown immediately then replaced by fresh data`() = runTest {
        val cachedArticles = createArticles(5)
        val fakeCache = FakeArticleCache()
        fakeCache.save(cachedArticles)

        val freshArticles = createArticles(20)
        fakeRepository.articlesToReturn = freshArticles
        fakeRepository.totalResults = 50

        val viewModel = NewsListViewModel(fakeRepository, fakeCache)

        // Before network completes, disk cache is shown
        val stateBeforeNetwork = viewModel.uiState.value
        assertEquals(5, stateBeforeNetwork.articles.size)

        advanceUntilIdle()

        // After network completes, fresh data replaces cache
        val state = viewModel.uiState.value
        assertEquals(20, state.articles.size)
        assertEquals("Title 1", state.articles[0].title)
    }

    @Test
    fun `pagination works after loading from disk cache`() = runTest {
        val cachedArticles = createArticles(5)
        val fakeCache = FakeArticleCache()
        fakeCache.save(cachedArticles)

        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 60

        val viewModel = NewsListViewModel(fakeRepository, fakeCache)
        advanceUntilIdle()

        // hasMorePages should be true, pagination should work
        assertTrue(viewModel.uiState.value.hasMorePages)

        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 60
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(40, viewModel.uiState.value.articles.size)
        assertEquals(2, viewModel.uiState.value.currentPage)
    }

    @Test
    fun `disk cache preserved when network fails`() = runTest {
        val cachedArticles = createArticles(5)
        val fakeCache = FakeArticleCache()
        fakeCache.save(cachedArticles)

        fakeRepository.errorToThrow = RuntimeException("No internet")

        val viewModel = NewsListViewModel(fakeRepository, fakeCache)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.articles.size)
        assertTrue(state.error!!.contains("previously loaded"))
        assertTrue(state.error!!.contains("outdated"))
    }

    @Test
    fun `successful load persists articles to disk cache`() = runTest {
        val fakeCache = FakeArticleCache()
        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 50

        val viewModel = NewsListViewModel(fakeRepository, fakeCache)
        advanceUntilIdle()

        assertEquals(20, fakeCache.load().size)
    }

    @Test
    fun `pagination persists all articles to disk cache`() = runTest {
        val fakeCache = FakeArticleCache()
        fakeRepository.articlesToReturn = createArticles(20)
        fakeRepository.totalResults = 60

        val viewModel = NewsListViewModel(fakeRepository, fakeCache)
        advanceUntilIdle()

        fakeRepository.articlesToReturn = createArticles(20)
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(40, fakeCache.load().size)
    }
}
