package com.example.sealnote.data

import com.example.sealnote.model.Notes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar // <-- DITAMBAHKAN: Import yang diperlukan untuk Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun getUserId(): String? = auth.currentUser?.uid

    private fun getUserNotesCollection() = getUserId()?.let {
        firestore.collection("users").document(it).collection("notes")
    }

    fun getAllNotes(): Flow<List<Notes>> {
        val collection = getUserNotesCollection() ?: return flowOf(emptyList())
        return collection.whereEqualTo("isTrashed", false)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toObject<Notes>() } }
    }

    fun getSecretNotes(): Flow<List<Notes>> {
        val collection = getUserNotesCollection() ?: return flowOf(emptyList())
        return collection.whereEqualTo("isSecret", true)
            .whereEqualTo("isTrashed", false)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toObject<Notes>() } }
    }

    fun getTrashedNotes(): Flow<List<Notes>> {
        val collection = getUserNotesCollection() ?: return flowOf(emptyList())
        return collection.whereEqualTo("isTrashed", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toObject<Notes>() } }
    }

    fun getBookmarkedNotes(): Flow<List<Notes>> {
        val collection = getUserNotesCollection() ?: return flowOf(emptyList())
        return collection.whereEqualTo("isBookmarked", true)
            .whereEqualTo("isTrashed", false)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toObject<Notes>() } }
    }

    fun getNoteById(noteId: String): Flow<Notes?>? {
        return getUserNotesCollection()
            ?.document(noteId)
            ?.snapshots()
            ?.map { it.toObject<Notes>() }
    }

    suspend fun saveNote(noteId: String?, title: String, content: String) {
        val collection = getUserNotesCollection() ?: throw Exception("User not logged in.")
        val currentTime = Date()

        if (noteId == null) {
            val newNote = Notes(
                userId = getUserId()!!,
                title = title,
                content = content,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            collection.add(newNote).await()
        } else {
            collection.document(noteId).update(
                mapOf(
                    "title" to title,
                    "content" to content,
                    "updatedAt" to currentTime
                )
            ).await()
        }
    }

    suspend fun toggleSecretStatus(noteId: String, isSecret: Boolean) {
        getUserNotesCollection()?.document(noteId)?.update("isSecret", isSecret)?.await()
    }

    // DIHAPUS: Fungsi trashNote yang duplikat dan lebih sederhana telah dihapus dari sini.

    /**
     * Memindahkan catatan ke sampah dan mengatur waktu kadaluwarsa 30 hari.
     */
    suspend fun trashNote(noteId: String) {
        val collection = getUserNotesCollection() ?: return

        // Hitung waktu 30 hari dari sekarang
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val expireTime = calendar.time

        collection.document(noteId).update(
            mapOf(
                "isTrashed" to true,
                "expireAt" to expireTime // Atur waktu penghapusan otomatis
            )
        ).await()
    }

    /**
     * Memulihkan catatan dari sampah.
     */
    suspend fun restoreNoteFromTrash(noteId: String) {
        val collection = getUserNotesCollection() ?: return
        collection.document(noteId).update(
            mapOf(
                "isTrashed" to false,
                "expireAt" to null // Hapus waktu kadaluwarsa
            )
        ).await()
    }

    /**
     * Menghapus catatan secara permanen dari database.
     */
    suspend fun deleteNotePermanently(noteId: String) {
        getUserNotesCollection()?.document(noteId)?.delete()?.await()
    }
}