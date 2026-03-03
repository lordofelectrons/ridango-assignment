package com.example.ridangoassignmentnewsapi.ui.screens.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ridangoassignmentnewsapi.data.repository.NewsRepository
import com.example.ridangoassignmentnewsapi.domain.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

data class NewsListUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)

class NewsListViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getTopHeadlines(page = 1, pageSize = PAGE_SIZE).fold(
                onSuccess = { result ->
                    _uiState.update {
                        it.copy(
                            articles = result.articles,
                            isLoading = false,
                            currentPage = 1,
                            hasMorePages = result.articles.isNotEmpty()
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
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
                        it.copy(
                            articles = it.articles + result.articles,
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
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    class Factory(private val repository: NewsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsListViewModel(repository) as T
        }
    }
}
