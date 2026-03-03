package com.example.ridangoassignmentnewsapi.ui.screens.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ridangoassignmentnewsapi.data.local.ArticleCache
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepository
import com.example.ridangoassignmentnewsapi.domain.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException

// newsapi.org returns (pageSize - 1) articles per request, so request 22 to get 21
private const val PAGE_SIZE = 22

data class NewsListUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)

class NewsListViewModel(
    private val repository: NewsRepository,
    private val articleCache: ArticleCache? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    init {
        val diskCache = articleCache?.load() ?: emptyList()
        if (diskCache.isNotEmpty()) {
            _uiState.update { it.copy(articles = diskCache) }
        }
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            val cachedArticles = _uiState.value.articles
            _uiState.update { it.copy(isLoading = cachedArticles.isEmpty(), isLoadingMore = cachedArticles.isNotEmpty(), error = null) }
            repository.getTopHeadlines(page = 1, pageSize = PAGE_SIZE).fold(
                onSuccess = { result ->
                    articleCache?.save(result.articles)
                    _uiState.update {
                        it.copy(
                            articles = result.articles,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = 1,
                            hasMorePages = result.articles.isNotEmpty()
                        )
                    }
                },
                onFailure = { e ->
                    val reason = errorReason(e)
                    val errorMsg = if (cachedArticles.isNotEmpty()) {
                        "Showing previously loaded news which may be outdated. Reason: $reason"
                    } else {
                        "Couldn't load articles. $reason"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = errorMsg
                        )
                    }
                }
            )
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingMore || state.isLoading || !state.hasMorePages) return

        viewModelScope.launch {
            val nextPage = state.currentPage + 1
            _uiState.update { it.copy(isLoadingMore = true) }
            repository.getTopHeadlines(page = nextPage, pageSize = PAGE_SIZE).fold(
                onSuccess = { result ->
                    _uiState.update {
                        val allArticles = it.articles + result.articles
                        articleCache?.save(allArticles)
                        it.copy(
                            articles = allArticles,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            hasMorePages = result.articles.isNotEmpty()
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = "Couldn't load more articles. ${errorReason(e)}"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun errorReason(e: Throwable): String {
        return when {
            e is UnknownHostException -> "No internet connection."
            e.cause is UnknownHostException -> "No internet connection."
            e is java.net.SocketTimeoutException -> "Connection timed out."
            e.message?.contains("rateLimited", ignoreCase = true) == true ||
                e.message?.contains("rate limit", ignoreCase = true) == true ||
                e.message?.contains("too many requests", ignoreCase = true) == true ->
                "API rate limit exceeded. Try again later."
            e.message != null -> e.message!!
            else -> "Unknown error."
        }
    }

    class Factory(
        private val repository: NewsRepository,
        private val articleCache: ArticleCache? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsListViewModel(repository, articleCache) as T
        }
    }
}
