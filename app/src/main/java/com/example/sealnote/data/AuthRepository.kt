// path: app/src/main/java/com/example/sealnote/data/AuthRepository.kt

package com.example.sealnote.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.sealnote.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class AuthenticatorType {
    FINGERPRINT,
    FACE
}

sealed class AuthRepositoryEvent {
    object Idle : AuthRepositoryEvent()
    object Loading : AuthRepositoryEvent()
    data class BiometricSuccess(val message: String? = null) : AuthRepositoryEvent()
    data class BiometricError(val message: String) : AuthRepositoryEvent()
    data class GoogleSignInSuccess(val message: String? = null) : AuthRepositoryEvent()
    data class GoogleSignInError(val message: String) : AuthRepositoryEvent()
    data class SignOutSuccess(val message: String? = null) : AuthRepositoryEvent() // Event ini akan dipancarkan
    data class GeneralError(val message: String) : AuthRepositoryEvent()
    data class GoogleSignInInitiated(val intent: Intent) : AuthRepositoryEvent()
}

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore,
    private val application: Application
) {
    private val _authRepoEvent = MutableSharedFlow<AuthRepositoryEvent>()
    val authRepoEvent: SharedFlow<AuthRepositoryEvent> = _authRepoEvent.asSharedFlow()

    // --- Biometric Authentication Logic ---
    fun canAuthenticate(context: Context, type: AuthenticatorType): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = when (type) {
            AuthenticatorType.FINGERPRINT -> BIOMETRIC_STRONG
            AuthenticatorType.FACE -> BIOMETRIC_STRONG or BIOMETRIC_WEAK
        }
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    suspend fun startBiometricAuthentication(activity: FragmentActivity, type: AuthenticatorType): Boolean {
        return suspendCancellableCoroutine { continuation ->
            if (!canAuthenticate(activity, type)) {
                continuation.resumeWithException(Exception("This biometric type is not available or not set up."))
                return@suspendCancellableCoroutine
            }

            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (continuation.isActive) {
                        val errorMessage = if (errString.contains("Cancel", ignoreCase = true)) {
                            "Authentication cancelled by user."
                        } else {
                            "Biometric auth error: $errString ($errorCode)"
                        }
                        continuation.resumeWithException(Exception(errorMessage))
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (continuation.isActive) {
                        continuation.resume(true)
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

            val authenticators = when (type) {
                AuthenticatorType.FINGERPRINT -> BIOMETRIC_STRONG
                AuthenticatorType.FACE -> BIOMETRIC_STRONG or BIOMETRIC_WEAK
            }

            val title = if (type == AuthenticatorType.FINGERPRINT) "Fingerprint Authentication" else "Face Authentication"

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle("Unlock your secret notes")
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(authenticators)
                .build()

            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resumeWithException(Exception("Failed to launch biometric prompt: ${e.message}"))
                }
            }

            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
        }
    }

    // --- Email/Password Authentication Logic ---
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email and password cannot be empty.")
        }
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw Exception("Sign-in failed: User is null.")
    }

    // --- Google Sign-In Logic ---
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleGoogleSignInResult(data: Intent?): FirebaseUser {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java) ?: throw ApiException(Status(CommonStatusCodes.INTERNAL_ERROR))
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        return authResult.user ?: throw Exception("Google sign-in failed: User is null.")
    }

    // Fungsi untuk menyimpan atau memperbarui profil pengguna di Firestore
    suspend fun saveUserProfileToFirestore(user: FirebaseUser, authType: String, fullNameOverride: String? = null, passwordHash: String? = null) {
        val userRef = firestore.collection("users").document(user.uid)
        val docSnapshot = userRef.get().await()

        if (!docSnapshot.exists()) {
            val newProfile = User(
                uid = user.uid,
                fullName = fullNameOverride ?: user.displayName ?: "",
                email = user.email ?: "",
                passwordHash = passwordHash,
                authenticationType = authType,
                createdAt = System.currentTimeMillis()
            )
            try {
                userRef.set(newProfile).await()
                Log.d("AuthRepository", "New user profile saved to Firestore for UID: ${user.uid}")
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to save new user profile to Firestore: ${e.message}", e)
                throw e
            }
        } else {
            Log.d("AuthRepository", "User profile already exists in Firestore for UID: ${user.uid}. Skipping creation.")
        }
    }

    // --- Common Functions ---
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            googleSignInClient.revokeAccess().await() // Menggunakan await()
            _authRepoEvent.emit(AuthRepositoryEvent.SignOutSuccess("Signed out successfully! (Google access revoked)")) // <--- PERBAIKAN DI SINI
            Log.d("AuthRepository", "User signed out and Google access revoked")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to sign out or revoke Google access: ${e.message}", e)
            _authRepoEvent.emit(AuthRepositoryEvent.GeneralError("Sign out failed: ${e.message}")) // <--- PERBAIKAN DI SINI
            throw e // Propagasi error agar ViewModel yang memanggil bisa menangkapnya
        }
    }

    // Fungsi untuk mengambil profil pengguna dari Firestore
    suspend fun getUserProfile(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching user profile from Firestore: ${e.message}", e)
            null
        }
    }
}