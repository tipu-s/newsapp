package com.androiddevs.news.utility

import com.androiddevs.news.model.Article

sealed class ResourceDB<T>(
    val data: T? = null, val message: String? = null
) {
    class Success<T>(data: T? = null) : ResourceDB<T>(data)
    class Error<T>(message: String, data: T? = null) : ResourceDB<T>(data, message)
}
