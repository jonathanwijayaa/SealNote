// path: app/src/main/java/com/example/sealnote/viewmodel/AddEditNoteViewModel.kt

package com.example.sealnote.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class untuk event yang akan dikirim ke UI
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NoteSaved : UiEvent()
}

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val savedStateHandle: SavedStateHandle // Hilt akan menyediakan ini secara otomatis
) : ViewModel() {

    // State untuk judul, konten, dan status rahasia dari catatan
    val title = MutableStateFlow("")
    val content = MutableStateFlow("")
    private val isSecret = MutableStateFlow(false)

    // Flow untuk mengirim event ke UI (misal: "Catatan Disimpan!")
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val noteId: String? = savedStateHandle["noteId"]

    init {
        // Cek apakah kita mengedit catatan yang ada atau membuat yang baru
        if (noteId != null && noteId != "null") {
            // Mode Edit: Muat data catatan
            viewModelScope.launch {
                repository.getNoteById(noteId).collect { existingNote ->
                    if (existingNote != null) {
                        title.value = existingNote.title
                        content.value = existingNote.content
                        isSecret.value = existingNote.isSecret
                    }
                }
            }
        } else {
            // Mode Tambah Baru: Ambil status isSecret dari argumen navigasi
            val isSecretFromNav: Boolean = savedStateHandle["isSecret"] ?: false
            isSecret.value = isSecretFromNav
        }
    }

    /**
     * Fungsi yang dipanggil oleh UI untuk menyimpan catatan.
     * Tidak perlu parameter karena semua data sudah ada di dalam state ViewModel ini.
     */
    fun onSaveNoteClick() {
        viewModelScope.launch {
            try {
                // Panggil fungsi BARU di Repository dengan parameter yang benar
                // Perhatikan, kita tidak perlu lagi mengirim userId dari sini!
                repository.saveNote(
                    noteId = noteId,
                    title = title.value.ifEmpty { "Untitled Note" },
                    content = content.value,
                    isSecret = isSecret.value // Kirim status rahasia yang benar
                )
                // Kirim event ke UI bahwa penyimpanan berhasil
                _eventFlow.emit(UiEvent.NoteSaved)
            } catch (e: Exception) {
                // Kirim event error jika penyimpanan gagal
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Couldn't save note"))
            }
        }
    }
}
