package com.example.sealnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sealnote.model.Notes

class HomepageViewModel : ViewModel() {
    private val _notes = MutableLiveData<List<Notes>>() // LiveData untuk daftar catatan
    val notes: LiveData<List<Notes>> get() = _notes

    fun loadNotes() {
        // Simulasi data sementara, bisa diganti dengan data dari database
        _notes.value = listOf(
            Notes(1, "Meeting Notes", "Discuss project details", false, false),
            Notes(2, "Grocery List", "Buy milk and bread", true,false),
            Notes(3, "Meeting Notes", "Discuss project details", false, true)
        )
    }

}