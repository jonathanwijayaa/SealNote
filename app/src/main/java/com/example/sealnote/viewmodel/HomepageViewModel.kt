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
class HomepageViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    val notes: StateFlow<List<Notes>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun trashNote(noteId: String) {
        viewModelScope.launch {
            try {
                repository.trashNote(noteId)
                _eventFlow.emit("Catatan dipindahkan ke sampah.")
            } catch (e: Exception) {
                _eventFlow.emit("Gagal memindahkan catatan.")
            }
        }
    }
    fun toggleSecretStatus(noteId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleSecretStatus(noteId, !currentStatus) // Kirim status kebalikannya
                val message = if (!currentStatus) "Catatan ditambahkan ke rahasia" else "Catatan dihapus dari rahasia"
                _eventFlow.emit(message)
            } catch (e: Exception) {
                _eventFlow.emit("Gagal mengubah status catatan.")
            }
        }
    }
}