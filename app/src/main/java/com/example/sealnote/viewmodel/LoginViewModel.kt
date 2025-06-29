// path: app/src/main/java/com/example/sealnote/viewmodel/LoginViewModel.kt

package com.example.sealnote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Import viewModelScope
import com.example.sealnote.data.AuthRepository // Import AuthRepository
import com.google.firebase.auth.FirebaseAuth // Tidak lagi digunakan langsung, tapi biarkan import jika ada
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository // <--- Gunakan AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    // Check if a user is already logged in (Didelegasikan ke AuthRepository)
    val isLoggedIn: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = authRepository.getCurrentUser() != null
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Email and Password must be filled!")
            return
        }

        _loginResult.value = LoginResult.Loading

        viewModelScope.launch { // Gunakan viewModelScope untuk suspend function
            try {
                val firebaseUser = authRepository.signInWithEmailPassword(email, password)
                authRepository.saveUserProfileToFirestore(firebaseUser, "email/password") // Simpan profil ke Firestore
                _loginResult.value = LoginResult.Success
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Login failed: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.signOut() // Mendelegasikan logout ke AuthRepository
            } catch (e: Exception) {
                // Handle logout error jika diperlukan
                Log.e("LoginViewModel", "Logout failed: ${e.message}", e)
                // Anda mungkin ingin memperbarui _loginResult dengan error di sini
            }
        }
    }

    sealed class LoginResult {
        object Loading: LoginResult()
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}