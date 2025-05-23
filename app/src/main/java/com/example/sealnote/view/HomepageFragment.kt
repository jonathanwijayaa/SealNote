package com.example.sealnote.view // Pastikan package ini sesuai dengan lokasi file Anda

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // Untuk FAB
import androidx.compose.material.icons.filled.Menu // Untuk ikon sort (hamburger)
import androidx.compose.material.icons.outlined.LocalOffer // Untuk ikon tag
import androidx.compose.material3.*
import androidx.compose.runtime.* // Tidak perlu import Composable lagi jika sudah ada di sini
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definisikan Warna sesuai gambar
val ScreenBackground = Color(0xFF1A1C2E) // Dark blue/gray
val CardBackgroundColor = Color(0xFF2C2F48) // Slightly lighter purplish dark blue
val FabColor = Color(0xFF7B5DFF) // Purple/Blue untuk FAB (sesuai perkiraan dari gambar)
val PrimaryTextColor = Color.White
val SecondaryTextColor = Color(0xFFD1D1D1) // LightGray yang sedikit lebih spesifik
val TertiaryTextColor = Color(0xFF9E9E9E) // Gray untuk tanggal dan tag
val IconColor = Color.White


// Data class Anda (tetap sama)
data class Note(
    val id: Int, // Tambahkan ID untuk key di LazyVerticalGrid jika diperlukan
    val title: String,
    val content: String,
    val date: String,
    val tag: String
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Data contoh, sesuaikan jumlah dan isinya
    val notes = remember {
        List(8) { index ->
            Note(
                id = index,
                title = "Title",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id.",
                date = "Mar 22, 2025",
                tag = "Example" // Tag akan ditampilkan dengan ikon
            )
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Aksi untuk menambah catatan baru */ },
                containerColor = FabColor,
                contentColor = IconColor,
                shape = RoundedCornerShape(16.dp) // Sedikit rounded square seperti di beberapa desain modern FAB
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add new note"
                )
            }
        },
        modifier = modifier // Modifier dari parameter bisa diterapkan ke Scaffold
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Padding dari Scaffold (untuk status bar, nav bar, FAB)
                .padding(horizontal = 16.dp) // Padding horizontal keseluruhan untuk konten
        ) {
            // Header: "All notes" dan "Sorted by"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp), // Padding vertikal untuk header
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "All notes",
                    color = PrimaryTextColor,
                    fontSize = 22.sp, // Ukuran font lebih besar untuk "All notes"
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sorted by",
                        color = PrimaryTextColor,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Menu, // Ikon hamburger menu
                        contentDescription = "Sort notes",
                        tint = PrimaryTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Grid untuk daftar catatan
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp), // Padding bawah untuk grid jika ada item yang dekat FAB
                verticalArrangement = Arrangement.spacedBy(16.dp), // Jarak vertikal antar kartu
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak horizontal antar kartu
            ) {
                items(notes, key = { it.id }) { note -> // Gunakan ID sebagai key
                    NoteCard(note)
                }
            }
        }
    }
}


@Composable
fun NoteCard(note: Note) {
    Card(
        shape = RoundedCornerShape(12.dp), // Sudut kartu yang lebih rounded
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        modifier = Modifier
            .fillMaxWidth()
        // .height(180.dp) // Anda bisa mengatur tinggi tetap atau membiarkannya dinamis (wrap content)
        // Jika konten bisa bervariasi, lebih baik tidak set tinggi tetap atau gunakan intrinsic height.
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding internal kartu
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp, // Ukuran font judul
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontSize = 13.sp, // Ukuran font konten
                color = SecondaryTextColor,
                lineHeight = 18.sp, // Jarak antar baris agar lebih mudah dibaca
                maxLines = 4 // Batasi jumlah baris konten agar kartu tidak terlalu panjang
            )
            Spacer(modifier = Modifier.height(16.dp)) // Beri jarak sebelum baris bawah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.date,
                    fontSize = 11.sp, // Ukuran font tanggal
                    color = TertiaryTextColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer, // Ikon tag
                        contentDescription = "Tag",
                        tint = TertiaryTextColor,
                        modifier = Modifier.size(14.dp) // Ukuran ikon tag
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = note.tag,
                        fontSize = 11.sp, // Ukuran font tag
                        color = TertiaryTextColor
                    )
                }
            }
        }
    }
}

// Pratinjau untuk HomeScreen
@Preview(showBackground = true, name = "Home Screen Preview Dark")
@Composable
fun HomeScreenPreview() {
    MaterialTheme { // Idealnya, Anda memiliki tema aplikasi sendiri yang mengatur warna gelap/terang
        HomeScreen()
    }
}

// Pratinjau untuk NoteCard individual
@Preview(showBackground = true, name = "Note Card Preview Dark", backgroundColor = 0xFF1A1C2E)
@Composable
fun NoteCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NoteCard(
                Note(
                    id = 0,
                    title = "Title",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id.",
                    date = "Mar 22, 2025",
                    tag = "Example"
                )
            )
        }
    }
}