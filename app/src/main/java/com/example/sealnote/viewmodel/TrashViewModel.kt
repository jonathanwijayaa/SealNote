package com.example.sealnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sealnote.model.Notes

class TrashViewModel : ViewModel() {
    private val _trashedNotes = MutableLiveData<List<Notes>>()
    val trashedNotes: LiveData<List<Notes>> get() = _trashedNotes

    init {
        loadTrashedNotes()
    }

    private fun loadTrashedNotes() {
        val dummyNotes = listOf(
            Notes(1, "Meeting Notes", "Discuss project details", false, false),
            Notes(2, "Grocery List", "Buy milk and bread", true,false),
            Notes(3, "Meeting Notes", "Discuss project details", false, true)
        )
        _trashedNotes.value = dummyNotes.filter { it.isTrashed }
    }
}
