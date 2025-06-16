package com.example.sealnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.NotesRepository
import com.example.sealnote.model.Notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    val trashedNotes: StateFlow<List<Notes>> = repository.getTrashedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun restoreNote(noteId: String) {
        viewModelScope.launch {
            try {
                repository.restoreNoteFromTrash(noteId)
                _eventFlow.emit("Catatan berhasil dipulihkan.")
            } catch (e: Exception) {
                _eventFlow.emit("Gagal memulihkan catatan.")
            }
        }
    }

    fun deletePermanently(noteId: String) {
        viewModelScope.launch {
            try {
                repository.deleteNotePermanently(noteId)
                _eventFlow.emit("Catatan dihapus permanen.")
            } catch (e: Exception) {
                _eventFlow.emit("Gagal menghapus catatan.")
            }
        }
    }
}