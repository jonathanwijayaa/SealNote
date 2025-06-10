package com.example.sealnote.view // Sesuaikan dengan package Anda

import androidx.compose.foundation.background // <-- ADD THIS IMPORT for Modifier.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.* // Make sure this is Material3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan tema Anda diimpor
import kotlinx.coroutines.launch

// Import your custom colors if you want to use them instead of MaterialTheme.colorScheme
// import com.example.sealnote.ui.theme.DarkGreyBackground
// import com.example.sealnote.ui.theme.NoteCardBackgroundColor
// import com.example.sealnote.ui.theme.TopAppBarBackgroundColor
// import com.example.sealnote.ui.theme.TopAppBarIconColor
// import com.example.sealnote.ui.theme.TopAppBarTitleColor
// import com.example.sealnote.ui.theme.SearchBarBackgroundColor
// import com.example.sealnote.ui.theme.SearchBarBorderColor
// import com.example.sealnote.ui.theme.SearchBarIconColor
// import com.example.sealnote.ui.theme.SearchBarHintColor
// import com.example.sealnote.ui.theme.SearchBarTextColor
// import com.example.sealnote.ui.theme.DropdownMenuBackground
// import com.example.sealnote.ui.theme.DropdownMenuItemTextColor


// Data class yang lebih sesuai untuk konteks ini
data class SecretNote(
    val id: String,
    val title: String,
    val contentPreview: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretNotesScreen(
    // Parameter untuk data dan aksi/event
    secretNotes: List<SecretNote> = emptyList(),
    onNoteClick: (SecretNote) -> Unit = {},
    onDeleteNote: (SecretNote) -> Unit = {},
    onNavigateTo: (String) -> Unit = {},
    onFabClick: () -> Unit = {}
) {
    // --- State Management ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Sort by Date", "Sort by Title")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

    // REVISI 4: Navigation Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                // Contoh item di drawer, tandai "Secret Notes" sebagai yang aktif
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = false,
                    onClick = { onNavigateTo("all_notes"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "All Notes") }
                )
                NavigationDrawerItem(
                    label = { Text("Secret Notes") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Lock, contentDescription = "Secret Notes") }
                )
                // ... tambahkan item lain seperti Bookmarks, Trash, Settings ...
            }
        }
    ) {
        Scaffold(
            // REVISI 2 & 3: TopAppBar dengan Search dan Sorting
            topBar = {
                TopAppBar(
                    title = { Text("Secret Notes") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Secret Notes")
                        }
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Notes")
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme color or your custom DropdownMenuBackground
                            ) {
                                sortOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme color or your custom DropdownMenuItemTextColor
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
                FloatingActionButton(onClick = onFabClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Secret Note")
                }
            }
        ) { innerPadding ->
            // Filter catatan berdasarkan query pencarian
            val filteredNotes = remember(searchQuery, secretNotes) {
                if (searchQuery.isBlank()) {
                    secretNotes
                } else {
                    secretNotes.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.contentPreview.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding) // Terapkan padding dari Scaffold di sini
                    .fillMaxSize()
            ) {
                // FIX: Corrected DockedSearchBar usage
                DockedSearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery },
                    onSearch = { newQuery ->
                        println("Search submitted: $newQuery")
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { newActiveState -> isSearchActive = newActiveState },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Search secret notes...", color = MaterialTheme.colorScheme.onSurfaceVariant) }, // Use MaterialTheme color or SearchBarHintColor
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) }, // Use MaterialTheme color or SearchBarIconColor
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Use MaterialTheme color or SearchBarIconColor
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant, // Use MaterialTheme color or SearchBarBackgroundColor
                        inputFieldColors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface, // Use MaterialTheme color or SearchBarTextColor
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // Use MaterialTheme color or SearchBarTextColor
                            cursorColor = MaterialTheme.colorScheme.onSurface, // Use MaterialTheme color or SearchBarTextColor
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Use MaterialTheme color or SearchBarBorderColor
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline, // Use MaterialTheme color or SearchBarBorderColor
                        )
                    )
                ) {
                    // Konten yang ditampilkan saat search bar aktif (daftar saran, dll.)
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hasil untuk '$searchQuery' akan ditampilkan di sini.", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Conditionally display the grid based on search active state
                if (!isSearchActive) {
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
                            SecretNoteCard(
                                note = note,
                                onClick = { onNoteClick(note) },
                                onDeleteClick = { onDeleteNote(note) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecretNoteCard(
    note: SecretNote,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // REVISI 1: Menggunakan ElevatedCard
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Menggunakan warna dari tema
        )
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
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme color or custom background
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme color or custom text color
                            onClick = {
                                onDeleteClick()
                                isMenuExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface) } // Use MaterialTheme color or custom tint
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = note.contentPreview,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(Modifier.weight(1f)) // Mendorong tanggal ke bawah
            Text(
                text = note.date,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SecretNotesScreenPreview() {
    val sampleNotes = List(5) { index ->
        SecretNote(
            id = "secret_$index",
            title = "Catatan Rahasia #$index",
            contentPreview = "Ini adalah isi dari catatan rahasia yang sangat penting...",
            date = "Jun ${15 + index}, 2025"
        )
    }
    SealnoteTheme(darkTheme = true) {
        SecretNotesScreen(secretNotes = sampleNotes)
    }
}