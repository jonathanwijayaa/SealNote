package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.* // Impor semua yang diperlukan dari Material3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sealnote.viewmodel.BookmarksViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.sealnote.model.Notes
import kotlinx.coroutines.launch
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan AppTheme diimpor
import androidx.compose.ui.graphics.Color // Tetap impor jika Anda ingin menggunakan Color.Transparent
import androidx.compose.ui.res.painterResource
import com.example.sealnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    bookmarkedNotes: List<Notes>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateHomepage: () -> Unit,
    onNavigateToAddNote: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSecretNotes: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit,
    bookmarksViewModel: BookmarksViewModel
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
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "SealNote Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                // Navigation Drawer Items
                NavigationDrawerItem(
                    label = { Text("All Notes", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onNavigateHomepage(); scope.launch { drawerState.close() } },

                    icon = { Icon(Icons.Outlined.Home, contentDescription = "All Notes", tint = MaterialTheme.colorScheme.onSurface) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks", color = MaterialTheme.colorScheme.onSurface) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmarks", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
                NavigationDrawerItem(
                    label = { Text("Secret Notes", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onNavigateToSecretNotes(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Lock, contentDescription = "Secret Notes", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
                NavigationDrawerItem(
                    label = { Text("Trash", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    selected = false,
                    onClick = { onNavigateToTrash(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Delete, contentDescription = "Trash", tint = MaterialTheme.colorScheme.onSecondaryContainer) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Settings", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onNavigateToSettings(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
                NavigationDrawerItem(
                    label = { Text("Profile", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onNavigateToProfile(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
            }
        }
    ) {
        Scaffold(
            containerColor = colorScheme.background, // Menggunakan warna tema untuk latar belakang Scaffold
            topBar = {
                CenterAlignedTopAppBar(
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
    SealnoteTheme(darkTheme = true) { // Anda bisa mengatur darkTheme = true atau false untuk melihat perbedaannya
        BookmarksScreen(
            bookmarkedNotes = dummyNotes,
            onNavigateHomepage = {},
            onNavigateToAddNote = {},
            onNavigateToProfile = {},
            onNavigateToBookmarks = {},
            onNavigateToSecretNotes = {},
            onNavigateToTrash = {},
            onNavigateToSettings = {},
            bookmarksViewModel = TODO(),
            searchQuery = TODO(),
            onSearchQueryChange = TODO()
        )
    }
}