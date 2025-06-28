package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* // Memastikan semua komponen Material 3 diimpor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sealnote.ui.theme.ItemNoteCardBackground
import com.example.sealnote.ui.theme.ItemNoteContentColor
import com.example.sealnote.ui.theme.ItemNoteDateColor
import com.example.sealnote.ui.theme.ItemNoteRestoreButtonBackground
import com.example.sealnote.ui.theme.ItemNoteRestoreButtonTextColor
import com.example.sealnote.ui.theme.ItemNoteTitleColor
import com.example.sealnote.ui.theme.TrashScreenBackground
import com.example.sealnote.model.DeletedNote
import com.example.sealnote.ui.theme.SealnoteTheme

@Composable
fun TrashNoteItem(
    note: DeletedNote,
    onRestoreClick: (noteId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(), // Mengisi lebar sel grid
        shape = RoundedCornerShape(8.dp), // app:cardCornerRadius="8dp"
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // app:cardElevation="4dp"
        colors = CardDefaults.cardColors(containerColor = ItemNoteCardBackground) // Menggunakan warna kustom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // padding="12dp" pada LinearLayout horizontal
            verticalAlignment = Alignment.CenterVertically // Untuk menengahkan tombol Restore secara vertikal
        ) {
            // Bagian Kiri: Info Catatan
            Column(
                modifier = Modifier.weight(1f) // Mengambil sisa ruang, mendorong tombol ke kanan
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Menggunakan tipografi titleMedium
                    color = ItemNoteTitleColor, // Menggunakan warna kustom
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = note.contentSnippet,
                    style = MaterialTheme.typography.bodyMedium, // Menggunakan tipografi bodyMedium
                    color = ItemNoteContentColor, // Menggunakan warna kustom
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = note.deletionDate,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), // Menggunakan tipografi labelSmall
                    color = ItemNoteDateColor // Menggunakan warna kustom
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Bagian Kanan: Tombol Restore Note
            Box(
                modifier = Modifier
                    .background(ItemNoteRestoreButtonBackground, shape = RoundedCornerShape(4.dp)) // Menggunakan warna kustom
                    .clickable { onRestoreClick(note.id) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Restore Note",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), // Menggunakan tipografi labelSmall
                    color = ItemNoteRestoreButtonTextColor, // Menggunakan warna kustom
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrashNoteItemPreviewDark() {
    val sampleNote = DeletedNote(
        id = "1",
        title = "Contoh Judul Catatan yang Panjang Sekali Sehingga Mungkin Perlu Beberapa Baris",
        contentSnippet = "Ini adalah cuplikan konten catatan yang telah dihapus. Cuplikan ini bisa cukup panjang hingga mencapai tiga baris maksimum sebelum akhirnya terpotong.",
        deletionDate = "Deleted date : 2 Juni 2025"
    )
    SealnoteTheme { // Gunakan tema kustom aplikasi Anda
        Surface(color = TrashScreenBackground) { // Untuk mensimulasikan latar belakang screen menggunakan warna kustom
            Box(modifier = Modifier.padding(8.dp)) {
                TrashNoteItem(note = sampleNote, onRestoreClick = {})
            }
        }
    }
}