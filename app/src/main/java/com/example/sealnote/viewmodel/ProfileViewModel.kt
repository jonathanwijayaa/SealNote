package com.example.sealnote.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val userName = MutableLiveData<String>()

    fun loadUserProfile() {
        // Logika mengambil data profil
        userName.value = "John Doe"
    }
}
