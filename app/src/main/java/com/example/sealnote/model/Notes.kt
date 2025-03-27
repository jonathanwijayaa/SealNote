package com.example.sealnote.model

data class Notes(
    val id: Int,
    val title: String,
    val content: String,
    val isBookmarked: Boolean=false,
    val isTrashed: Boolean=false
)
