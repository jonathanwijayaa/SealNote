package com.example.sealnote.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sealnote.R
import com.example.sealnote.model.Notes
import com.example.sealnote.viewmodel.BookmarksViewModel
import kotlinx.coroutines.launch

/**
 * Entry point cerdas untuk layar bookmark. Menghubungkan ViewModel ke UI.
 */
@Composable
fun BookmarksRoute(
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarkedNotes by viewModel.bookmarkedNotes.collectAsStateWithLifecycle()

    BookmarksScreen(
        bookmarkedNotes = bookmarkedNotes,
        onNavigateToAddNote = onNavigateToAddNote,
        onNavigateTo = onNavigateTo
    )
}

/**
 * Composable yang hanya bertugas menampilkan UI berdasarkan data yang diterima.
 */





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
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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
            ModalDrawerSheet {
                // Letakkan semua NavigationDrawerItem Anda di sini
                // Contoh:
                Text("SealNote Menu", modifier = Modifier.padding(16.dp), style = typography.titleLarge)
                Divider()
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = false,
                    onClick = { onNavigateTo("all_notes") }
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                // ... item lainnya
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        // Judul layar
                        Text("Bookmarks", color = colorScheme.onSurface)
                    },

                TopAppBar(
                    title = { Text("Bookmarks") },

                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Search Action */ }) {
                            Icon(painterResource(id = R.drawable.ic_search), "Search")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.Add, "Add Note")
                }
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(bookmarkedNotes, key = { it.id }) { note ->
                    // Ganti dengan Composable NoteCard Anda yang sebenarnya
                    Card(modifier = Modifier.padding(4.dp)) {
                        Text(note.title, modifier = Modifier.padding(16.dp))
                    }
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