package com.example.sealnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sealnote.model.Notes

class BookmarksViewModel : ViewModel() {
    private val _bookmarkedNotes = MutableLiveData<List<Notes>>()
    val bookmarkedNotes: LiveData<List<Notes>> get() = _bookmarkedNotes

    init {
        loadBookmarksNotes()
    }

    private fun loadBookmarksNotes() {
        val dummyNotes = listOf(
            Notes(1, "Meeting Notes", "Discuss project details", false, false),
            Notes(2, "Grocery List", "Buy milk and bread", true,false),
            Notes(3, "Meeting Notes", "Discuss project details", false, true)
        )
        _bookmarkedNotes.value = dummyNotes.filter { it.isBookmarked }
    }
}
