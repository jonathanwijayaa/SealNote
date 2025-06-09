package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort // Menggunakan ikon AutoMirrored
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark // Ikon yang lebih cocok untuk Bookmarks
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.model.Notes // Pastikan model Notes diimpor
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan tema Anda diimpor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    bookmarkedNotes: List<Notes>,
    onNoteClick: (Notes) -> Unit,
    onDeleteBookmark: (Notes) -> Unit, // Callback untuk menghapus bookmark/catatan
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    // --- State Management ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Sort by Date", "Sort by Title")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

    // REVISI 4: Navigation Drawer (disederhanakan)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = false,
                    onClick = { onNavigateTo("all_notes"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "All Notes") }
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks") },
                    selected = true, // Halaman ini adalah Bookmarks
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Bookmark, contentDescription = "Bookmarks") }
                )
                // ... tambahkan item drawer lain jika perlu
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { onNavigateTo("settings"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // REVISI 2 & 3: TopAppBar dengan Search dan Sorting
                TopAppBar(
                    title = { Text("Bookmarks") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Bookmarks")
                        }
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Bookmarks")
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false }
                            ) {
                                sortOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedSortOption = option
                                            isSortMenuExpanded = false
                                            // TODO: Terapkan logika sorting
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { innerPadding ->
            // Filter catatan berdasarkan query pencarian
            val filteredNotes = remember(searchQuery, bookmarkedNotes) {
                if (searchQuery.isBlank()) {
                    bookmarkedNotes
                } else {
                    bookmarkedNotes.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.content.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding) // Terapkan innerPadding di sini
                    .fillMaxSize()
            ) {
                if (isSearchActive) {
                    DockedSearchBar(
                        // Parameter untuk mengatur WADAH search bar
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },

                        // Parameter 'inputField' untuk mendefinisikan KOLOM INPUT di dalamnya
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onSearch = {
                                    isSearchActive = false
                                    // TODO: Handle search action
                                },
                                expanded = isSearchActive,
                                onExpandedChange = { isSearchActive = it },
                                placeholder = { Text("Search bookmarked notes...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        // Konten yang ditampilkan DI BAWAH search bar saat aktif.
                        // Biasanya untuk menampilkan saran atau hasil pencarian.
                    }
                } else {
                    // Konten utama (grid catatan) saat search bar tidak aktif
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 8.dp + 80.dp // Padding bawah agar tidak tertutup FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredNotes, key = { it.id }) { note ->
                            // REVISI 1 & 5: Memanggil Note Card yang sudah direvisi
                            BookmarkNoteCard(
                                note = note,
                                onClick = { onNoteClick(note) },
                                onDeleteClick = { onDeleteBookmark(note) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkNoteCard(
    note: Notes,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // REVISI 1: Menggunakan ElevatedCard
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                // REVISI 5: Aksi pada Card (Delete)
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Note Options")
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Remove Bookmark") },
                            onClick = {
                                onDeleteClick()
                                isMenuExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = "Remove") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = note.content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarksScreenPreview() {
    val dummyNotes = listOf(
        Notes(1, "Resep Nasi Goreng", "Bawang merah, bawang putih, nasi, kecap manis, telur..."),
        Notes(2, "Ide Proyek Kotlin", "Aplikasi catatan sederhana SealNote dengan Jetpack Compose."),
        Notes(3, "Link Penting", "developer.android.com/jetpack/compose"),
        Notes(4, "Catatan Rapat", "Bahasan mengenai UI/UX dan alur navigasi."),
    )

    SealnoteTheme(darkTheme = true) {
        BookmarksScreen(
            bookmarkedNotes = dummyNotes,
            onNoteClick = {},
            onDeleteBookmark = {},
            onNavigateToAddNote = {},
            onNavigateTo = {}
        )
    }
}