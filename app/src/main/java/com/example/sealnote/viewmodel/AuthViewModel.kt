// path: app/src/main/java/com/example/sealnote/viewmodel/AuthViewModel.kt

package com.example.sealnote.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.AuthRepository
import com.example.sealnote.data.AuthenticatorType // Import AuthenticatorType dari data package
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthEvent {
    object Idle : AuthEvent()
    object Loading : AuthEvent()
    data class BiometricSuccess(val message: String? = null) : AuthEvent()
    data class BiometricError(val message: String) : AuthEvent()
    data class GoogleSignInSuccess(val message: String? = null) : AuthEvent()
    data class GoogleSignInError(val message: String) : AuthEvent()
    data class SignOutSuccess(val message: String? = null) : AuthEvent()
    data class AuthError(val message: String) : AuthEvent()
    data class GoogleSignInInitiated(val intent: Intent) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository, // <--- Gunakan AuthRepository
    application: Application
) : AndroidViewModel(application) {

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent: SharedFlow<AuthEvent> = _authEvent.asSharedFlow()

    // --- Biometric Authentication Logic (Didelegasikan ke AuthRepository) ---
    fun canAuthenticate(context: Context, type: AuthenticatorType): Boolean {
        return authRepository.canAuthenticate(context, type)
    }

    fun startBiometricAuthentication(activity: FragmentActivity, type: AuthenticatorType) {
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.Loading)
            try {
                val success = authRepository.startBiometricAuthentication(activity, type)
                if (success) {
                    _authEvent.emit(AuthEvent.BiometricSuccess("Biometric authentication succeeded!"))
                } else {
                    _authEvent.emit(AuthEvent.BiometricError("Biometric authentication failed unexpectedly."))
                }
            } catch (e: Exception) {
                _authEvent.emit(AuthEvent.BiometricError("Biometric auth error: ${e.message}"))
            }
        }
    }

    // --- Google Sign-In Logic (Didelegasikan ke AuthRepository) ---
    fun startGoogleSignInFlow() {
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.Loading)
            val signInIntent = authRepository.getGoogleSignInIntent()
            _authEvent.emit(AuthEvent.GoogleSignInInitiated(signInIntent))
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val firebaseUser = authRepository.handleGoogleSignInResult(data)
                // saveUserProfileToFirestore sudah dipanggil di dalam handleGoogleSignInResult di AuthRepository
                // Jika ingin dipanggil terpisah, ubah AuthRepository.handleGoogleSignInResult
                authRepository.saveUserProfileToFirestore(firebaseUser, "google") // Pastikan ini dipanggil
                _authEvent.emit(AuthEvent.GoogleSignInSuccess("Google Sign-In successful!"))
                Log.d("AuthViewModel", "Firebase sign-in successful: ${firebaseUser.email}")
            } catch (e: Exception) {
                if (e is ApiException) {
                    _authEvent.emit(AuthEvent.GoogleSignInError("Google Sign-In failed: ${e.statusCode}"))
                } else {
                    _authEvent.emit(AuthEvent.GoogleSignInError("Error during Google Sign-In: ${e.message}"))
                }
                Log.e("AuthViewModel", "Error handling Google sign in result", e)
            }
        }
    }

    // --- Common Functions (Didelegasikan ke AuthRepository) ---
    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    fun signOut() {
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.Loading)
            try {
                authRepository.signOut()
                _authEvent.emit(AuthEvent.SignOutSuccess("Signed out successfully!"))
            } catch (e: Exception) {
                _authEvent.emit(AuthEvent.AuthError("Failed to sign out: ${e.message}"))
            }
        }
    }

    fun eventHandled() {
        // SharedFlow secara otomatis membersihkan event setelah dikonsumsi.
    }
}