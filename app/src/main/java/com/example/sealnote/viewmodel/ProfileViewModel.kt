// path: app/src/main/java/com/example/sealnote/viewmodel/ProfileViewModel.kt

package com.example.sealnote.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.AuthRepository
import com.example.sealnote.data.AuthRepositoryEvent // Import AuthRepositoryEvent
import com.example.sealnote.data.AuthenticatorType
import com.example.sealnote.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val editedName: String = "",
    val editedPassword: String = "",
    val isEditing: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = true,
    val triggerBiometric: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isSignedOut: Boolean = false
)

sealed class ProfileEvent {
    object Idle : ProfileEvent()
    object Loading : ProfileEvent()
    data class ShowToast(val message: String) : ProfileEvent()
    data class Error(val message: String) : ProfileEvent()
    object TogglePasswordVisibilitySuccess : ProfileEvent()
    object SignOutSuccess : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _profileEvent = MutableSharedFlow<ProfileEvent>()
    val profileEvent: SharedFlow<ProfileEvent> = _profileEvent.asSharedFlow()

    init {
        fetchUserProfile()
        viewModelScope.launch {
            authRepository.authRepoEvent.collectLatest { event ->
                when (event) {
                    is AuthRepositoryEvent.BiometricSuccess -> {
                        _profileEvent.emit(ProfileEvent.TogglePasswordVisibilitySuccess)
                        _profileEvent.emit(ProfileEvent.ShowToast(event.message ?: "Biometric successful!"))
                    }
                    is AuthRepositoryEvent.BiometricError -> {
                        _profileEvent.emit(ProfileEvent.Error(event.message))
                    }
                    is AuthRepositoryEvent.SignOutSuccess -> {
                        _profileEvent.emit(ProfileEvent.SignOutSuccess)
                        _uiState.value = _uiState.value.copy(isLoading = false) // <--- PERBAIKAN DI SINI: Reset loading
                        Log.d("ProfileViewModel", "AuthRepository emitted SignOutSuccess event. Setting isLoading=false.")
                    }
                    is AuthRepositoryEvent.GeneralError -> {
                        _profileEvent.emit(ProfileEvent.Error(event.message))
                        _uiState.value = _uiState.value.copy(isLoading = false) // <--- PERBAIKAN DI SINI: Reset loading on error
                    }
                    else -> {}
                }
            }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val currentUser = firebaseAuth.currentUser

            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User not logged in.")
                return@launch
            }

            try {
                val userProfile = authRepository.getUserProfile(currentUser.uid)

                if (userProfile != null) {
                    _uiState.value = _uiState.value.copy(
                        user = userProfile,
                        editedName = userProfile.fullName,
                        isLoading = false
                    )
                    Log.d("ProfileViewModel", "User profile loaded from Firestore: ${userProfile.fullName}")
                } else {
                    val basicUser = User(
                        uid = currentUser.uid,
                        fullName = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                        passwordHash = null,
                        authenticationType = currentUser.providerData.firstOrNull()?.providerId ?: "unknown",
                        createdAt = System.currentTimeMillis()
                    )
                    try {
                        authRepository.saveUserProfileToFirestore(currentUser, basicUser.authenticationType, basicUser.fullName)
                        Log.d("ProfileViewModel", "Basic user profile created in Firestore for UID: ${currentUser.uid}")
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Failed to create basic user profile in Firestore: ${e.message}")
                    }

                    _uiState.value = _uiState.value.copy(
                        user = basicUser,
                        editedName = basicUser.fullName,
                        isLoading = false,
                        infoMessage = "Basic profile loaded. Complete your profile details."
                    )
                    Log.w("ProfileViewModel", "User document not found for UID: ${currentUser.uid}. Created/Loading basic profile.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load profile: ${e.message}"
                )
                Log.e("ProfileViewModel", "Error fetching user profile: ${e.message}", e)
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(editedName = newName)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(editedPassword = newPassword)
    }

    fun onEditToggle() {
        _uiState.value = _uiState.value.copy(
            isEditing = !_uiState.value.isEditing,
            editedName = _uiState.value.user?.fullName ?: "",
            editedPassword = ""
        )
    }

    fun onSaveChanges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val currentFirebaseUser = firebaseAuth.currentUser
            if (currentFirebaseUser == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Not logged in.")
                return@launch
            }

            try {
                val updates = hashMapOf<String, Any>(
                    "fullName" to _uiState.value.editedName
                )
                firestore.collection("users").document(currentFirebaseUser.uid).update(updates).await()

                if (_uiState.value.editedPassword.isNotBlank() && _uiState.value.user?.authenticationType != "google") {
                    currentFirebaseUser.updatePassword(_uiState.value.editedPassword).await()
                    _uiState.value = _uiState.value.copy(infoMessage = "Name and password updated successfully!")
                } else if (_uiState.value.editedPassword.isNotBlank() && _uiState.value.user?.authenticationType == "google") {
                    _uiState.value = _uiState.value.copy(infoMessage = "Password cannot be changed for Google accounts here.")
                } else {
                    _uiState.value = _uiState.value.copy(infoMessage = "Name updated successfully!")
                }

                _uiState.value = _uiState.value.copy(
                    user = _uiState.value.user?.copy(
                        fullName = _uiState.value.editedName
                    ),
                    isEditing = false,
                    editedPassword = "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save changes: ${e.message}"
                )
            }
        }
    }

    fun onTogglePasswordVisibilityRequest(activity: FragmentActivity, context: Context) {
        if (_uiState.value.isPasswordVisible) {
            _uiState.value = _uiState.value.copy(isPasswordVisible = false)
            return
        }

        viewModelScope.launch {
            try {
                if (authRepository.canAuthenticate(context, AuthenticatorType.FINGERPRINT)) {
                    authRepository.startBiometricAuthentication(activity, AuthenticatorType.FINGERPRINT)
                } else if (authRepository.canAuthenticate(context, AuthenticatorType.FACE)) {
                    authRepository.startBiometricAuthentication(activity, AuthenticatorType.FACE)
                } else {
                    _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
                    _profileEvent.emit(ProfileEvent.ShowToast("Biometric authentication not available. Toggling visibility directly."))
                }
            } catch (e: Exception) {
                _profileEvent.emit(ProfileEvent.Error("Biometric prompt failed: ${e.message}"))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            Log.d("ProfileViewModel", "SignOut initiated. Calling AuthRepository.")
            try {
                authRepository.signOut()
                // Hasil sign out akan diamati di init block
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error initiating sign out from ProfileViewModel: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Sign out failed: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onSignOutNavigated() {
        _uiState.value = _uiState.value.copy(isSignedOut = false)
        Log.d("ProfileViewModel", "onSignOutNavigated called. isSignedOut reset.")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, infoMessage = null)
    }
}