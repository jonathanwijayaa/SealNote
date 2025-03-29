package com.example.sealnote.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    fun login(username: String, password: String, context: Context) {
        if (username.isEmpty() || password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Username dan Password harus diisi!")
            return
        }

        // Replace with your actual authentication logic
        if (username == "admin" && password == "admin") {
            // Simulate successful login
            // In a real app, you'd likely interact with a backend or database here.
            // For example, you might use a Repository to handle authentication.

            // Save login status (you might want to move this to a Repository as well)
            val sharedPreferences = context.getSharedPreferences("SealNotePrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

            _loginResult.value = LoginResult.Success
        } else {
            _loginResult.value = LoginResult.Error("Username atau password salah!")
        }
    }

    sealed class LoginResult {
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}