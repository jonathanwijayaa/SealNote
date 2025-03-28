package com.example.sealnote.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    init {
        _isLoggedIn.value = sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun login(username: String, password: String) {
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            if (username == savedUsername && password == savedPassword) {
                _isLoggedIn.value = true
                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            } else {
                _isLoggedIn.value = false
            }
        } else {
            _isLoggedIn.value = false
        }
    }

    fun signup(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            sharedPreferences.edit().apply {
                putString("username", username)
                putString("password", password)
                putBoolean("isLoggedIn", false)
                apply()
            }
        }
    }

    fun logout() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        _isLoggedIn.value = false
    }
}
