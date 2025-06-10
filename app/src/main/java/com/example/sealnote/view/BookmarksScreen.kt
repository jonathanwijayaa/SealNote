package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.* // Make sure this is Material3
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

// --- START: ADD THESE IMPORTS FOR COLORS ---
import com.example.sealnote.ui.theme.DarkGreyBackground
import com.example.sealnote.ui.theme.NoteCardBackgroundColor
import com.example.sealnote.ui.theme.TopAppBarBackgroundColor
import com.example.sealnote.ui.theme.TopAppBarIconColor
import com.example.sealnote.ui.theme.TopAppBarTitleColor
import com.example.sealnote.ui.theme.SearchBarBackgroundColor
import com.example.sealnote.ui.theme.SearchBarBorderColor
import com.example.sealnote.ui.theme.SearchBarIconColor
import com.example.sealnote.ui.theme.SearchBarHintColor
import com.example.sealnote.ui.theme.SearchBarTextColor
import com.example.sealnote.ui.theme.DropdownMenuBackground
import com.example.sealnote.ui.theme.DropdownMenuItemTextColor
import androidx.compose.foundation.background // Add this line
// --- END: ADD THESE IMPORTS FOR COLORS ---

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // Required for DockedSearchBar
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
    var isSearchActive by remember { mutableStateOf(false) } // Renamed to isSearchActive for clarity with search bar state
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
                    title = { Text("Bookmarks", color = TopAppBarTitleColor) }, // Added color to title
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TopAppBarIconColor) // Added color to icon
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TopAppBarBackgroundColor), // Added background color
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Bookmarks", tint = TopAppBarIconColor) // Added color to icon
                        }
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Bookmarks", tint = TopAppBarIconColor) // Added color to icon
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                // FIX: DropdownMenu no longer has a 'colors' parameter.
                                // Its background is set via Modifier.background().
                                // Item colors are set within DropdownMenuItem itself.
                                modifier = Modifier.background(DropdownMenuBackground)
                            ) {
                                sortOptions.forEach { option ->
                                    // FIX: DropdownMenuItem takes its text and icon as Composable lambdas.
                                    // Ensure the 'text' lambda explicitly defines Text()
                                    DropdownMenuItem(
                                        text = { Text(option, color = DropdownMenuItemTextColor) }, // Correct
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
            },
            containerColor = DarkGreyBackground // Set background color for the screen
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
                // --- Corrected DockedSearchBar usage ---
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
                    placeholder = { Text("Search bookmarked notes...", color = SearchBarHintColor) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SearchBarIconColor) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = SearchBarIconColor)
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = SearchBarBackgroundColor,
                        inputFieldColors = TextFieldDefaults.colors( // Use inputFieldColors for text field related colors
                            focusedTextColor = SearchBarTextColor,
                            unfocusedTextColor = SearchBarTextColor,
                            cursorColor = SearchBarTextColor,
                            focusedIndicatorColor = SearchBarBorderColor,
                            unfocusedIndicatorColor = SearchBarBorderColor // Set unfocused indicator color
                        )
                    )
                ) {
                    // This is the content that appears *inside* the expanded DockedSearchBar
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "Searching for: \"$searchQuery\"",
                            modifier = Modifier.padding(16.dp),
                            color = SearchBarTextColor
                        )
                    } else {
                        Text(
                            text = "Start typing to search...",
                            modifier = Modifier.padding(16.dp),
                            color = SearchBarHintColor
                        )
                    }
                }

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

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = NoteCardBackgroundColor)
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
                    color = TopAppBarTitleColor
                )
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Note Options", tint = TopAppBarIconColor)
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(DropdownMenuBackground) // Correct way to set background
                    ) {
                        DropdownMenuItem(
                            text = { Text("Remove Bookmark", color = DropdownMenuItemTextColor) }, // Correct
                            onClick = {
                                onDeleteClick()
                                isMenuExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = DropdownMenuItemTextColor) } // Correct
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