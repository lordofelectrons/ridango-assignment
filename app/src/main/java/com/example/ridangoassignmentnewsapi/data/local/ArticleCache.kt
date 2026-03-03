package com.example.ridangoassignmentnewsapi.data.local

import android.content.Context
import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface ArticleCache {
    fun save(articles: List<Article>)
    fun load(): List<Article>
    fun clear()
}

class ArticleCacheImpl(context: Context) : ArticleCache {

    private val prefs = context.getSharedPreferences("article_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun save(articles: List<Article>) {
        prefs.edit()
            .putString(KEY_ARTICLES, gson.toJson(articles))
            .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    override fun load(): List<Article> {
        val json = prefs.getString(KEY_ARTICLES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Article>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ARTICLES = "cached_articles"
        private const val KEY_TIMESTAMP = "cache_timestamp"
    }
}
