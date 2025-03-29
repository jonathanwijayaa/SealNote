package com.example.sealnote.util

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /** Mengembalikan nilai hanya jika belum dikonsumsi */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /** Mengembalikan nilai tanpa mengubah status konsumsi */
    fun peekContent(): T = content
}
