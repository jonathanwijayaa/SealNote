package com.example.sealnote.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddNotesViewModel : ViewModel() {
    val noteText = MutableLiveData<String>()

    fun saveNote(text: String) {
        noteText.value = text
        // Tambahkan logika penyimpanan di database
    }
}
