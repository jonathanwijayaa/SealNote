package com.example.sealnote.view // Tambahkan di setiap file
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Pastikan import ini ada
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FloatingActionButtonDefaults
import com.example.sealnote.R // Pastikan ini adalah path yang benar ke R class Anda

// Asumsi warna dari resources Anda
val ScreenNotesBackground = Color(0xFF1A1C2E) // Contoh, sesuaikan dengan @color/background
val ScreenNotesFabColor = Color(0xFF7377E8)
val ScreenNotesTextColor = Color.White
val ScreenNotesIconColor = Color.White
val NoteCardDefaultBackgroundColor = Color(0xFF2C2F48) // Contoh warna kartu catatan

// Data class placeholder untuk item catatan utama
data class MainNote(
    val id: String,
    val title: String,
    val contentPreview: String,
    val date: String,
    val colorTag: Color? = null // Opsional: warna untuk kartu catatan
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretNotesScreen(
    notes: List<MainNote> = emptyList(),
    onNoteClick: (MainNote) -> Unit = {},
    onSortClick: () -> Unit = {},
    onFabClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenNotesBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = ScreenNotesFabColor,
                contentColor = ScreenNotesIconColor, // Untuk tint ikon di dalam FAB
                shape = FloatingActionButtonDefaults.shape // Bentuk default (biasanya lingkaran)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add), // Pastikan drawable ini ada
                    contentDescription = "Add New Note"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Padding dari Scaffold (untuk status bar, FAB, dll.)
                .fillMaxSize()
        ) {
            // Header Layout
            NotesHeader(
                onSortClick = onSortClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp) // Margin dari XML
            )

            // Grid untuk Catatan
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // app:spanCount="2"
                modifier = Modifier
                    .fillMaxSize()
                    // layout_marginStart/End="8dp" untuk RecyclerView diterapkan sebagai padding horizontal pada LazyVerticalGrid
                    .padding(horizontal = 8.dp),
                // paddingHorizontal="8dp" dan paddingBottom="16dp" dari RecyclerView menjadi contentPadding
                contentPadding = PaddingValues(
                    start = 8.dp, // dari paddingHorizontal
                    end = 8.dp,   // dari paddingHorizontal
                    top = 8.dp,   // Beri jarak dari header
                    bottom = 16.dp + 56.dp + 16.dp // paddingBottom + FAB height + FAB margin (agar item terakhir tidak tertutup FAB)
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),   // Jarak antar item
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar item
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCardItem(
                        note = note,
                        onClick = { onNoteClick(note) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesHeader(
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "All Secret Notes",
            color = ScreenNotesTextColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f) // Mengisi ruang yang tersedia
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onSortClick) // Membuat grup "Sorted by" bisa diklik
                .padding(vertical = 4.dp) // Padding agar area klik lebih nyaman
        ) {
            Text(
                text = "Sorted by",
                color = ScreenNotesTextColor,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp)) // layout_marginStart="4dp" untuk ikon
            Icon(
                painter = painterResource(id = R.drawable.ic_sort), // Pastikan drawable ini ada
                contentDescription = "Sort Notes",
                tint = ScreenNotesIconColor, // android:tint="#FFFFFF"
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NoteCardItem( // Berbeda dari TrashNoteItem
    note: MainNote,
    onClick: () -> Unit
) {
    // IMPLEMENTASI INI ADALAH PLACEHOLDER.
    // Anda HARUS menyesuaikan Composable ini agar sesuai dengan desain
    // file item_note.xml Anda untuk daftar catatan utama.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp), // Contoh, bisa disesuaikan
        colors = CardDefaults.cardColors(
            containerColor = note.colorTag ?: NoteCardDefaultBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Contoh elevasi
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .defaultMinSize(minHeight = 120.dp) // Agar kartu tidak terlalu pendek
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp, // Sedikit lebih besar untuk judul catatan utama
                color = ScreenNotesTextColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.contentPreview,
                fontSize = 14.sp,
                color = ScreenNotesTextColor.copy(alpha = 0.8f),
                maxLines = 4, // Mungkin lebih banyak baris untuk preview konten
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f)) // Mendorong tanggal ke bawah
            Text(
                text = note.date,
                fontSize = 12.sp,
                color = ScreenNotesTextColor.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainNotesScreenPreview() {
    val sampleNotes = List(7) { index ->
        MainNote(
            id = "note_$index",
            title = "Judul Catatan Penting $index",
            contentPreview = "Ini adalah isi dari catatan rahasia nomor $index yang sangat penting dan perlu diingat baik-baik. Konten bisa beberapa baris.",
            date = "Mei ${20 + index}, 2025",
            colorTag = if (index % 3 == 0) Color(0xFF4A4E69) else if (index % 3 == 1) Color(0xFF2C3E50) else null
        )
    }
    MaterialTheme { // Atau tema kustom aplikasi Anda
        SecretNotesScreen(notes = sampleNotes)
    }
}