package com.example.ridangoassignmentnewsapi.util

import android.util.Log
import com.example.ridangoassignmentnewsapi.domain.model.Article
import com.google.protobuf.CodedInputStream
import com.google.protobuf.CodedOutputStream
import java.io.ByteArrayOutputStream

/**
 * Serializes/deserializes Article using Protocol Buffers wire format (protobuf-javalite).
 *
 * Schema (matching article.proto):
 *   field 1: source_name (string)
 *   field 2: author (string)
 *   field 3: title (string)
 *   field 4: description (string)
 *   field 5: url (string)
 *   field 6: url_to_image (string)
 *   field 7: published_at (string)
 *   field 8: content (string)
 */
object ArticleSerializer {

    private const val TAG = "ArticleSerializer"

    private const val FIELD_SOURCE_NAME = 1
    private const val FIELD_AUTHOR = 2
    private const val FIELD_TITLE = 3
    private const val FIELD_DESCRIPTION = 4
    private const val FIELD_URL = 5
    private const val FIELD_URL_TO_IMAGE = 6
    private const val FIELD_PUBLISHED_AT = 7
    private const val FIELD_CONTENT = 8

    fun toProtobufBytes(article: Article): ByteArray {
        val baos = ByteArrayOutputStream()
        val output = CodedOutputStream.newInstance(baos)

        if (article.sourceName.isNotEmpty()) {
            output.writeString(FIELD_SOURCE_NAME, article.sourceName)
        }
        if (article.author.isNotEmpty()) {
            output.writeString(FIELD_AUTHOR, article.author)
        }
        if (article.title.isNotEmpty()) {
            output.writeString(FIELD_TITLE, article.title)
        }
        if (article.description.isNotEmpty()) {
            output.writeString(FIELD_DESCRIPTION, article.description)
        }
        if (article.url.isNotEmpty()) {
            output.writeString(FIELD_URL, article.url)
        }
        if (article.urlToImage.isNotEmpty()) {
            output.writeString(FIELD_URL_TO_IMAGE, article.urlToImage)
        }
        if (article.publishedAt.isNotEmpty()) {
            output.writeString(FIELD_PUBLISHED_AT, article.publishedAt)
        }
        if (article.content.isNotEmpty()) {
            output.writeString(FIELD_CONTENT, article.content)
        }

        output.flush()
        return baos.toByteArray()
    }

    fun fromProtobufBytes(bytes: ByteArray): Article {
        val input = CodedInputStream.newInstance(bytes)
        var sourceName = ""
        var author = ""
        var title = ""
        var description = ""
        var url = ""
        var urlToImage = ""
        var publishedAt = ""
        var content = ""

        while (!input.isAtEnd) {
            val tag = input.readTag()
            when (tag ushr 3) {
                FIELD_SOURCE_NAME -> sourceName = input.readString()
                FIELD_AUTHOR -> author = input.readString()
                FIELD_TITLE -> title = input.readString()
                FIELD_DESCRIPTION -> description = input.readString()
                FIELD_URL -> url = input.readString()
                FIELD_URL_TO_IMAGE -> urlToImage = input.readString()
                FIELD_PUBLISHED_AT -> publishedAt = input.readString()
                FIELD_CONTENT -> content = input.readString()
                else -> input.skipField(tag)
            }
        }

        return Article(
            sourceName = sourceName,
            author = author,
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content
        )
    }

    fun sendToMockApi(article: Article) {
        val bytes = toProtobufBytes(article)
        Log.d(TAG, "Saved article via protobuf (${bytes.size} bytes): ${article.title}")
        Log.d(TAG, "Protobuf data: ${bytes.joinToString(",") { it.toString() }}")
    }
}
