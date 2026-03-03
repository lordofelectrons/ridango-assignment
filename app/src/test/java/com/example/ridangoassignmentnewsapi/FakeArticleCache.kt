package com.example.ridangoassignmentnewsapi

import com.example.ridangoassignmentnewsapi.data.local.ArticleCache
import com.example.ridangoassignmentnewsapi.domain.model.Article

class FakeArticleCache : ArticleCache {

    private var stored: List<Article> = emptyList()
    var saveCount = 0

    override fun save(articles: List<Article>) {
        stored = articles
        saveCount++
    }

    override fun load(): List<Article> = stored

    override fun clear() {
        stored = emptyList()
    }
}
