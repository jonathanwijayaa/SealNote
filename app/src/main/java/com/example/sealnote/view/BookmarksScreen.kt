package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.* // Impor semua yang diperlukan dari Material3
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
import com.example.sealnote.ui.theme.AppTheme // Pastikan AppTheme diimpor
import androidx.compose.ui.graphics.Color // Tetap impor jika Anda ingin menggunakan Color.Transparent
import androidx.compose.ui.res.painterResource
import com.example.sealnote.R

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

    // Mengambil ColorScheme dari MaterialTheme
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography // Tambahkan ini untuk akses ke typography

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = colorScheme.surface // Menggunakan warna tema untuk drawer
            ) {
                Text(
                    "SealNote Menu",
                    modifier = Modifier.padding(16.dp),
                    style = typography.titleLarge, // Menggunakan typography tema
                    color = colorScheme.onSurface // Warna teks menu
                )
                Divider(color = colorScheme.outlineVariant) // Warna divider
                NavigationDrawerItem(
                    label = { Text("All Notes", color = colorScheme.onSurface) }, // Warna teks
                    selected = false,
                    onClick = {
                        onNavigateTo("all_notes")
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        // Warna item saat tidak terpilih
                        unselectedContainerColor = Color.Transparent, // Biarkan transparan
                        unselectedTextColor = colorScheme.onSurface,
                        unselectedIconColor = colorScheme.onSurface
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks", color = colorScheme.onSecondaryContainer) }, // Warna teks item terpilih
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        // Warna item saat terpilih
                        selectedContainerColor = colorScheme.secondaryContainer,
                        selectedTextColor = colorScheme.onSecondaryContainer,
                        selectedIconColor = colorScheme.onSecondaryContainer
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Secret Notes", color = colorScheme.onSurface) },
                    selected = false,
                    onClick = {
                        onNavigateTo("secret_notes")
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = colorScheme.onSurface,
                        unselectedIconColor = colorScheme.onSurface
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Trash", color = colorScheme.onSurface) },
                    selected = false,
                    onClick = {
                        onNavigateTo("trash")
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = colorScheme.onSurface,
                        unselectedIconColor = colorScheme.onSurface
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Settings", color = colorScheme.onSurface) },
                    selected = false,
                    onClick = {
                        onNavigateTo("settings")
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = colorScheme.onSurface,
                        unselectedIconColor = colorScheme.onSurface
                    )
                )
            }
        }
    ) {
        Scaffold(
            containerColor = colorScheme.background, // Menggunakan warna tema untuk latar belakang Scaffold
            topBar = {
                TopAppBar(
                    title = {
                        // Judul layar
                        Text("Bookmarks", color = colorScheme.onSurface)
                    },
                    navigationIcon = {
                        // Tombol untuk membuka drawer (ikon Menu)
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = colorScheme.onSurface)
                        }
                    },
                    actions = {
                        // Ikon pencarian di sisi kanan
                        IconButton(onClick = {
                            // TODO: Aksi untuk menampilkan/menyembunyikan TextField pencarian
                            // atau menavigasi ke layar pencarian terpisah
                            // Untuk contoh ini, kita bisa menampilkan TextField sebagai overlay atau dialog,
                            // atau langsung menempatkan SearchBar jika itu yang diinginkan
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search), // Anda perlu memiliki drawable ic_search
                                contentDescription = "Search",
                                tint = colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surface,
                        titleContentColor = colorScheme.onSurface,
                        navigationIconContentColor = colorScheme.onSurface,
                        actionIconContentColor = colorScheme.onSurface // Menentukan warna ikon aksi
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAddNote,
                    containerColor = colorScheme.primary, // Warna FAB dari tema
                    contentColor = colorScheme.onPrimary // Warna ikon FAB dari tema
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { paddingValues ->
            val filteredNotes = if (searchQuery.isBlank()) {
                bookmarkedNotes
            } else {
                bookmarkedNotes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    NoteCard(note) // Pastikan NoteCard juga menggunakan tema Material3
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun BookmarksScreenPreview() {
    val dummyNotes = listOf(
        Notes(1, "Resep Nasi Goreng", "Bawang merah, bawang putih, nasi, kecap manis, telur..."),
        Notes(2, "Ide Proyek Kotlin", "Aplikasi catatan sederhana SealNote dengan Jetpack Compose."),
        Notes(3, "Link Penting", "developer.android.com/jetpack/compose"),
        Notes(4, "Catatan Rapat", "Bahasan mengenai UI/UX dan alur navigasi."),
        Notes(5, "Daftar Belanja", "Susu, Roti, Telur, Kopi."),
        Notes(6, "Quote Favorit", "Stay hungry, stay foolish.")
    )

    // Wrap preview dengan AppTheme agar warna tema diterapkan
    AppTheme(darkTheme = true) { // Anda bisa mengatur darkTheme = true atau false untuk melihat perbedaannya
        BookmarksScreenLayout(
            bookmarkedNotes = dummyNotes,
            searchQuery = "",
            onSearchQueryChange = {},
            onNavigateToAddNote = {},
            onNavigateTo = {}
        )
    }
}