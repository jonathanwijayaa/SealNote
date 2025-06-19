// path: app/src/main/java/com/example/sealnote/data/NotesRepository.kt

package com.example.sealnote.data

import com.example.sealnote.model.Notes
import com.example.sealnote.model.User // <-- INI DIA SOLUSINYA: Tambahkan import ini
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
    /**
     * Mendapatkan ID pengguna yang sedang login dari FirebaseAuth.
     * Mengembalikan null jika tidak ada pengguna yang login.
     */
    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Mendapatkan referensi ke sub-koleksi 'notes' milik pengguna yang sedang login.
     * Mengembalikan null jika pengguna tidak login.
     */
    private fun getUserNotesCollection() = getUserId()?.let { userId ->
        firestore.collection("users").document(userId).collection("notes")
    }

    // --- FUNGSI GET (READ) NOTES---
    // ... (Semua fungsi get notes Anda tidak berubah) ...
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


    // --- FUNGSI CREATE / UPDATE NOTE---
    suspend fun saveNote(noteId: String?, title: String, content: String, isSecret: Boolean) {
        val userId = getUserId() ?: throw Exception("User is not logged in.")
        val collection = firestore.collection("users").document(userId).collection("notes")
        val currentTime = Date()

        if (noteId == null) {
            val newNoteDocument = collection.document()
            val newNote = Notes(
                id = newNoteDocument.id,
                userId = userId,
                title = title,
                content = content,
                isSecret = isSecret,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            newNoteDocument.set(newNote).await()
        } else {
            collection.document(noteId).update(
                mapOf(
                    "title" to title,
                    "content" to content,
                    "isSecret" to isSecret,
                    "updatedAt" to currentTime
                )
            ).await()
        }
    }


    // --- FUNGSI AKSI NOTE LAINNYA ---
    // ... (Semua fungsi aksi notes Anda tidak berubah) ...
    suspend fun toggleSecretStatus(noteId: String, isSecret: Boolean) {
        getUserNotesCollection()?.document(noteId)?.update(
            mapOf(
                "isSecret" to isSecret,
                "updatedAt" to Date()
            )
        )?.await()
    }
    suspend fun toggleBookmarkStatus(noteId: String, isBookmarked: Boolean) {
        getUserNotesCollection()?.document(noteId)?.update(
            mapOf(
                "isBookmarked" to isBookmarked,
                "updatedAt" to Date()
            )
        )?.await()
    }
    suspend fun trashNote(noteId: String) {
        val collection = getUserNotesCollection() ?: return
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val expireTime = calendar.time

        collection.document(noteId).update(
            mapOf(
                "isTrashed" to true,
                "expireAt" to expireTime,
                "updatedAt" to Date()
            )
        ).await()
    }
    suspend fun restoreNoteFromTrash(noteId: String) {
        val collection = getUserNotesCollection() ?: return
        collection.document(noteId).update(
            mapOf(
                "isTrashed" to false,
                "expireAt" to null,
                "updatedAt" to Date()
            )
        ).await()
    }
    suspend fun deleteNotePermanently(noteId: String) {
        getUserNotesCollection()?.document(noteId)?.delete()?.await()
    }


    // --- FUNGSI UNTUK PROFIL PENGGUNA ---
    // Dengan adanya import User, semua fungsi ini sekarang menjadi valid

    suspend fun getUserProfile(userId: String): User? {
        return try {
            firestore.collection("users").document(userId)
                .get()
                .await()
                .toObject(User::class.java) // Sekarang compiler tahu apa itu 'User'
        } catch (e: Exception) {
            println("Error getting user profile: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfile(userId: String, newName: String) {
        firestore.collection("users").document(userId)
            .update("fullName", newName)
            .await()
    }

    suspend fun updateUserPasswordHash(userId: String, newHash: String) {
        firestore.collection("users").document(userId)
            .update("passwordHash", newHash)
            .await()
    }
}