package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever // Contoh ikon
import androidx.compose.material.icons.filled.Restore // Contoh ikon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.sealnote.R // Jika @color/background dan @color/white dari R

// Asumsi warna dari resources Anda (ganti dengan Color(0xFF....) jika spesifik)
// atau pastikan R.color.background dan R.color.white terdefinisi dengan benar.
// Untuk contoh ini, saya akan gunakan warna placeholder seperti sebelumnya.
val TrashScreenBackground = Color(0xFF1A1C2E) // Contoh, sesuaikan dengan @color/background Anda
val TrashScreenTextColor = Color.White      // Contoh, sesuaikan dengan @color/white Anda
val TrashNoteItemBackgroundColor = Color(0xFF2C2F48) // Contoh warna kartu catatan

// Data class placeholder untuk item catatan di tempat sampah
data class DeletedNote(
    val id: String,
    val title: String,
    val contentSnippet: String,
    val deletionDate: String // Atau informasi lain yang relevan
)

@Composable
fun TrashScreen(
    // Anda mungkin ingin meneruskan daftar catatan yang dihapus sebagai parameter
    deletedNotes: List<DeletedNote> = emptyList(),
    onRestoreNote: (String) -> Unit = {},
    onPermanentlyDeleteNote: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TrashScreenBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
            // Padding horizontal 8dp dari XML untuk RecyclerView bisa diterapkan di sini
            // atau pada LazyVerticalGrid jika hanya grid yang memerlukannya.
            // XML: recyclerView android:layout_marginStart="8dp", android:layout_marginEnd="8dp"
        ) {
            // Teks Notifikasi
            Text(
                text = "Your note here will be automatically deleted in 30 days.",
                color = TrashScreenTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 16.dp) // Sesuai margin XML
            )

            // Grid untuk Catatan yang Dihapus
            // XML: recyclerView android:layout_marginTop="8dp"
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // app:spanCount="2"
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp), // Margin atas dari teks notifikasi
                contentPadding = PaddingValues(8.dp), // padding="8dp" dari RecyclerView
                verticalArrangement = Arrangement.spacedBy(8.dp), // Jarak antar item secara vertikal
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar item secara horizontal
            ) {
                items(deletedNotes, key = { it.id }) { note ->
                    TrashNoteItem(
                        note = note,
                        onRestore = { onRestoreNote(note.id) },
                        onPermanentlyDelete = { onPermanentlyDeleteNote(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TrashNoteItem(
    note: DeletedNote,
    onRestore: () -> Unit,
    onPermanentlyDelete: () -> Unit
) {
    // IMPLEMENTASI INI ADALAH PLACEHOLDER.
    // Anda HARUS menyesuaikan Composable ini agar sesuai dengan desain
    // file item_note.xml Anda.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // .aspectRatio(1f / 1.2f) // Atur aspek rasio atau tinggi sesuai kebutuhan
            .heightIn(min = 120.dp), // Contoh tinggi minimal
        shape = RoundedCornerShape(12.dp), // Contoh corner radius
        colors = CardDefaults.cardColors(containerColor = TrashNoteItemBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(), // Mengisi kartu
            verticalArrangement = Arrangement.SpaceBetween // Menyebarkan konten dan tombol aksi
        ) {
            Column { // Untuk konten teks
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TrashScreenTextColor,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.contentSnippet,
                    fontSize = 13.sp,
                    color = TrashScreenTextColor.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Deleted: ${note.deletionDate}", // Contoh informasi tambahan
                    fontSize = 11.sp,
                    color = TrashScreenTextColor.copy(alpha = 0.5f)
                )
            }

            // Tombol Aksi (Restore & Delete Permanently)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End, // Tombol di sisi kanan
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRestore) {
                    Icon(
                        imageVector = Icons.Filled.Restore,
                        contentDescription = "Restore Note",
                        tint = TrashScreenTextColor
                    )
                }
                IconButton(onClick = onPermanentlyDelete) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "Delete Permanently",
                        tint = TrashScreenTextColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrashScreenPreview() {
    // Contoh data untuk preview
    val sampleNotes = List(5) { index ->
        DeletedNote(
            id = "note_$index",
            title = "Judul Catatan Dihapus $index",
            contentSnippet = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus beberapa waktu lalu...",
            deletionDate = "2 hari lalu"
        )
    }
    MaterialTheme { // Atau tema kustom aplikasi Anda
        TrashScreen(deletedNotes = sampleNotes)
    }
}

@Preview(showBackground = true)
@Composable
fun TrashNoteItemPreview() {
    MaterialTheme {
        TrashNoteItem(
            note = DeletedNote("1", "Contoh Judul", "Ini adalah isi catatan yang dihapus.", "Kemarin"),
            onRestore = {},
            onPermanentlyDelete = {}
        )
    }
}
