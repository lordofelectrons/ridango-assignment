package com.example.ridangoassignmentnewsapi.ui.screens.articledetail

import androidx.lifecycle.ViewModel
import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.example.ridangoassignmentnewsapi.util.ArticleSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ArticleDetailUiState(
    val article: Article? = null,
    val isSaved: Boolean = false
)

class ArticleDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    fun setArticle(article: Article) {
        _uiState.update { it.copy(article = article) }
    }

    fun saveArticle() {
        val article = _uiState.value.article ?: return
        ArticleSerializer.sendToMockApi(article)
        _uiState.update { it.copy(isSaved = true) }
    }
}
