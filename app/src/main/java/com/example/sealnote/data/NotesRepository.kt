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
import java.util.Calendar
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

    // --- FUNGSI GET (Sudah Baik, Tidak Perlu Diubah) ---
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

    fun getNoteById(noteId: String): Flow<Notes?> {
        val collection = getUserNotesCollection() ?: return flowOf(null)
        return collection.document(noteId)
            .snapshots()
            .map { it.toObject<Notes>() }
    }

    // --- FUNGSI CREATE / UPDATE (Sudah Baik, Tidak Perlu Diubah) ---
    suspend fun saveNote(noteId: String?, title: String, content: String, userId: String) {
        val collection = firestore.collection("users").document(userId).collection("notes")
        val currentTime = Date()

        if (noteId == null) {
            val newNoteDocument = collection.document()
            val newNote = Notes(
                id = newNoteDocument.id,
                userId = userId,
                title = title,
                content = content,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            newNoteDocument.set(newNote).await()
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

    // --- FUNGSI AKSI (DENGAN PENINGKATAN) ---

    suspend fun toggleSecretStatus(noteId: String, isSecret: Boolean) {
        getUserNotesCollection()?.document(noteId)?.update(
            mapOf(
                "isSecret" to isSecret,
                "updatedAt" to Date() // Perbarui updatedAt
            )
        )?.await()
    }

    // --- FUNGSI BARU ---
    /**
     * Mengubah status bookmark pada catatan.
     */
    suspend fun toggleBookmarkStatus(noteId: String, isBookmarked: Boolean) {
        getUserNotesCollection()?.document(noteId)?.update(
            mapOf(
                "isBookmarked" to isBookmarked,
                "updatedAt" to Date() // Perbarui updatedAt
            )
        )?.await()
    }
    // --------------------

    suspend fun trashNote(noteId: String) {
        val collection = getUserNotesCollection() ?: return

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val expireTime = calendar.time

        collection.document(noteId).update(
            mapOf(
                "isTrashed" to true,
                "expireAt" to expireTime,
                "updatedAt" to Date() // Perbarui updatedAt
            )
        ).await()
    }

    suspend fun restoreNoteFromTrash(noteId: String) {
        val collection = getUserNotesCollection() ?: return
        collection.document(noteId).update(
            mapOf(
                "isTrashed" to false,
                "expireAt" to null,
                "updatedAt" to Date() // Perbarui updatedAt
            )
        ).await()
    }

    suspend fun deleteNotePermanently(noteId: String) {
        getUserNotesCollection()?.document(noteId)?.delete()?.await()
    }
}