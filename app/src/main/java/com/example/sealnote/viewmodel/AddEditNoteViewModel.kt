package com.example.sealnote.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.NotesRepository
import com.example.sealnote.model.Notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
    savedStateHandle: SavedStateHandle // Untuk mengambil noteId dari argumen navigasi
) : ViewModel() {

    private val noteId: String? = savedStateHandle.get("noteId")

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    // StateFlow untuk menampung data catatan yang sedang diedit
    private val _note = MutableStateFlow<Notes?>(null)
    val note = _note.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // Jika noteId tidak null (mode edit), muat data catatan
        if (noteId != null && noteId != "null") {
            viewModelScope.launch {
                repository.getNoteById(noteId)?.collect { existingNote ->
                    if (existingNote != null) {
                        _title.value = existingNote.title
                        _content.value = existingNote.content
                        _note.value = existingNote
                    }
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun saveNote() {
        viewModelScope.launch {
            try {
                if (_title.value.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Judul tidak boleh kosong"))
                    return@launch
                }
                repository.saveNote(
                    noteId = if (noteId == "null") null else noteId,
                    title = _title.value,
                    content = _content.value
                )
                _eventFlow.emit(UiEvent.SaveNote)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Gagal menyimpan catatan: ${e.message}"))
            }
        }
    }

    // Kelas untuk event UI
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent() // Event untuk navigasi kembali setelah simpan
    }
}