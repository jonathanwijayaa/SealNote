// path: app/src/main/java/com/example/sealnote/viewmodel/AddEditNoteViewModel.kt

package com.example.sealnote.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sealnote.data.NotesRepository
import com.example.sealnote.model.Notes
// import com.google.firebase.storage.FirebaseStorage // <--- HAPUS IMPORT INI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
// import kotlinx.coroutines.tasks.await // <--- HAPUS IMPORT INI jika tidak ada lagi tasks Firebase
import java.util.UUID
import javax.inject.Inject
import android.webkit.MimeTypeMap // Pastikan import ini ada

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
    // private val firebaseStorage: FirebaseStorage, // <--- HAPUS INJEKSI INI
    savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {

    val title = MutableStateFlow("")
    val content = MutableStateFlow("")
    val isBookmarked = MutableStateFlow(false)
    val isSecret = MutableStateFlow(false)
    val currentImageUri = MutableStateFlow<Uri?>(null)  // tetap
    val imageUrl        = MutableStateFlow<String?>(null)   // nama lama kembali

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var noteId: String? = null

    init {
        noteId = savedStateHandle.get<String>("noteId")

        noteId?.let { id ->
            viewModelScope.launch {
                repository.getNoteById(id).firstOrNull()?.let { note ->
                    title.value = note.title
                    content.value = note.content
                    isBookmarked.value = note.bookmarked
                    isSecret.value = note.secret
                    // Inisialisasi URI lokal jika ada dari notes
                    note.imageUrl?.let { uriString ->
                    currentImageUri.value = Uri.parse(uriString)
                    imageUrl.value        = uriString
                    }
                } ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Catatan tidak ditemukan."))
                }
            }
        }
        val navIsSecret = savedStateHandle.get<Boolean>("isSecret") ?: false
        isSecret.value = navIsSecret
    }

    fun onImageSelected(uri: Uri?) {
        currentImageUri.value = uri
        if (uri != null) {
            imageUrl.value = uri?.toString()
        }
    }

    fun onSaveNoteClick() {
        if (title.value.isBlank() && content.value.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Judul atau konten tidak boleh kosong."))
            }
            return
        }

        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar("Menyimpan catatan..."))

            // Gunakan URI lokal yang saat ini dipilih/diambil
            val finalImageUrl: String? = currentImageUri.value?.toString()

            try {
                repository.saveNote(
                    noteId = noteId,
                    title = title.value,
                    content = content.value,
                    isSecret = isSecret.value,
                    imageUrl = finalImageUrl,
                )

                noteId?.let { id ->
                    repository.toggleBookmarkStatus(id, isBookmarked.value)
                }

                _eventFlow.emit(UiEvent.ShowSnackbar("Catatan berhasil disimpan!"))
                _eventFlow.emit(UiEvent.NoteSaved)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Gagal menyimpan catatan: ${e.localizedMessage}"))
                Log.e("AddEditNoteViewModel", "Note save failed: ${e.message}", e)
            }
        }
    }

    fun toggleBookmarkStatus() {
        isBookmarked.value = !isBookmarked.value
    }

    fun toggleSecretStatus() {
        isSecret.value = !isSecret.value
    }

    // VVVVVV HAPUS FUNGSI INI KARENA TIDAK ADA LAGI UPLOAD KE FIREBASE STORAGE VVVVVV
    /*
    private fun getImageExtension(uri: Uri): String {
        val contentResolver = application.contentResolver
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
    }
    */
    // ^^^^^^ HAPUS FUNGSI INI ^^^^^^
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NoteSaved : UiEvent()
}