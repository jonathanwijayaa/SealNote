package com.example.sealnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sealnote.model.Notes

class SecretNotesViewModel : ViewModel() {
    private val _secretNotes = MutableLiveData<List<Notes>>()
    val secretNotes: LiveData<List<Notes>> get() = _secretNotes

    init {
        loadSecretNotes()
    }

    private fun loadSecretNotes() {
        val dummyNotes = listOf(
            Notes(1, "Password List", "Saved passwords"),
            Notes(2, "Private Diary", "Today's secret thoughts"),
            Notes(3, "Top Secret", "Confidential Information")
        )
        _secretNotes.value = dummyNotes
    }
}