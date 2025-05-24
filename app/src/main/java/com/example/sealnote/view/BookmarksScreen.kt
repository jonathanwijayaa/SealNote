package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sealnote.viewmodel.BookmarksViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.example.sealnote.model.Notes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreenLayout(
    bookmarkedNotes: List<Notes>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Konten untuk Navigation Drawer
            ModalDrawerSheet {
                Text(
                    "SealNote Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = false,
                    onClick = {
                        onNavigateTo("all_notes")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks") },
                    selected = true, // Item ini aktif
                    onClick = { scope.launch { drawerState.close() } } // Hanya menutup drawer
                )
                NavigationDrawerItem(
                    label = { Text("Secret Notes") },
                    selected = false,
                    onClick = {
                        onNavigateTo("secret_notes")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Trash") },
                    selected = false,
                    onClick = {
                        onNavigateTo("trash")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        onNavigateTo("settings")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // Konten utama layar menggunakan Scaffold
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search your bookmarked notes...") },
                            modifier = Modifier.fillMaxWidth(),
//                            colors = TextFieldDefaults.textFieldColors(
//                                // Contoh: Membuat garis bawah merah saat fokus
//                                focusedIndicatorColor = Color.Red,
//                                // Membuat garis bawah transparan saat tidak fokus
//                                unfocusedIndicatorColor = Color.Transparent,
//                                // Membuat latar belakang transparan
//                                containerColor = Color.Transparent,
//                                // Warna kursor merah
//                                cursorColor = Color.Red
//                            ),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        // Tombol untuk membuka drawer
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                // Tombol Aksi Mengambang untuk menambah catatan
                FloatingActionButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { paddingValues -> // Menggunakan paddingValues dari Scaffold
            // Memfilter catatan berdasarkan query pencarian
            val filteredNotes = if (searchQuery.isBlank()) {
                bookmarkedNotes
            } else {
                bookmarkedNotes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true) // Opsional: cari di konten juga
                }
            }

            // Menampilkan catatan dalam grid vertikal
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Menampilkan 2 kolom
                modifier = Modifier
                    .padding(paddingValues) // Menerapkan padding dari Scaffold
                    .fillMaxSize()
                    .padding(horizontal = 8.dp), // Menambah padding horizontal tambahan
                contentPadding = PaddingValues(vertical = 8.dp) // Menambah padding vertikal
            ) {
                // Menggunakan 'items' dengan key untuk performa yang lebih baik
                items(filteredNotes, key = { it.id }) { note ->
                    NoteCard(note)
                }
            }
        }
    }
}

// --- Composable untuk Pratinjau ---

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun BookmarksScreenPreview() {
    // Data dummy untuk pratinjau
    val dummyNotes = listOf(
        Notes(1, "Resep Nasi Goreng", "Bawang merah, bawang putih, nasi, kecap manis, telur..."),
        Notes(2, "Ide Proyek Kotlin", "Aplikasi catatan sederhana SealNote dengan Jetpack Compose."),
        Notes(3, "Link Penting", "developer.android.com/jetpack/compose"),
        Notes(4, "Catatan Rapat", "Bahasan mengenai UI/UX dan alur navigasi."),
        Notes(5, "Daftar Belanja", "Susu, Roti, Telur, Kopi."),
        Notes(6, "Quote Favorit", "Stay hungry, stay foolish.")
    )

    // Menggunakan MaterialTheme untuk pratinjau
    MaterialTheme {
        BookmarksScreenLayout(
            bookmarkedNotes = dummyNotes,
            searchQuery = "", // Query pencarian awal kosong
            onSearchQueryChange = {}, // Callback dummy
            onNavigateToAddNote = {}, // Callback dummy
            onNavigateTo = {} // Callback dummy
        )
    }
}