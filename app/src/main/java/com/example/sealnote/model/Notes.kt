package com.example.sealnote.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Notes(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val isBookmarked: Boolean = false,
    val isTrashed: Boolean = false,
    val isSecret: Boolean = false,

    @ServerTimestamp
    val createdAt: Date? = null,

    @ServerTimestamp
    val updatedAt: Date? = null,

    // TAMBAHKAN FIELD INI UNTUK PENGHAPUSAN OTOMATIS
    val expireAt: Date? = null
)