package com.example.sealnote.model

// To hold user information
data class User(
    val uid: String = "", // Pastikan nama field ini adalah `uid`
    val fullName: String = "",
    val email: String = "",
    val passwordHash: String? = null,
    val authenticationType: String = "email/password",
    val createdAt: Long = System.currentTimeMillis() // Timestamp pembuatan akun
    // Tambahkan field profil kustom lainnya di sini
)