package com.example.sealnote.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val darkModeEnabled = MutableLiveData<Boolean>()

    fun toggleDarkMode(enabled: Boolean) {
        darkModeEnabled.value = enabled
    }
}
