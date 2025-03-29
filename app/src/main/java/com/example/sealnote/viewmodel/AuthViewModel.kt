package com.example.sealnote.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    // Tambahkan logika untuk login dan signup di sini
    fun login(username: String, password: String): Boolean {
        // Logika login
        return username == "admin" && password == "admin"
    }

    fun signup(username: String, password: String) {
        // Logika signup
    }
}