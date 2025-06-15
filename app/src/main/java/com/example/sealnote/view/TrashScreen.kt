package com.example.sealnote.view

import androidx.compose.foundation.background // <-- ADD THIS IMPORT for Modifier.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.* // Make sure this is Material3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan tema Anda diimpor
import kotlinx.coroutines.launch

// Data class untuk item catatan di tempat sampah
data class DeletedNote(
    val id: String,
    val title: String,
    val contentSnippet: String,
    val deletionDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    deletedNotes: List<DeletedNote> = emptyList(),
    onRestoreNote: (DeletedNote) -> Unit = {},
    onPermanentlyDeleteNote: (DeletedNote) -> Unit = {},
    onNavigateTo: (String) -> Unit = {}
) {
    // --- State Management ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Sort by Deletion Date", "Sort by Title")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

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
                    label = { Text("Trash") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Delete, contentDescription = "Trash") }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Trash") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Trash")
                        }
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Notes")
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme color or custom DropdownMenuBackground
                            ) {
                                sortOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme color or custom DropdownMenuItemTextColor
                                        onClick = {
                                            selectedSortOption = option
                                            isSortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            val filteredNotes = remember(searchQuery, deletedNotes) {
                if (searchQuery.isBlank()) deletedNotes
                else deletedNotes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.contentSnippet.contains(searchQuery, ignoreCase = true)
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // FIX: Corrected DockedSearchBar usage
                DockedSearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery },
                    onSearch = { newQuery ->
                        println("Search submitted: $newQuery")
                        isSearchActive = false // Close the search bar after search
                    },
                    active = isSearchActive,
                    onActiveChange = { newActiveState -> isSearchActive = newActiveState },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Adjust padding as needed
                    placeholder = { Text("Search in trash...", color = MaterialTheme.colorScheme.onSurfaceVariant) }, // Use MaterialTheme color or SearchBarHintColor
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
                    // Konten untuk menampilkan hasil/saran pencarian
                    // This is the content that appears *inside* the expanded DockedSearchBar
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Hasil untuk '$searchQuery' akan ditampilkan di sini.",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Conditionally display the main content (Text and Grid) based on search active state
                if (!isSearchActive) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Items in trash are automatically deleted after 30 days.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Add color for consistency
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredNotes, key = { it.id }) { note ->
                                TrashNoteCard(
                                    note = note,
                                    onRestore = { onRestoreNote(note) },
                                    onPermanentlyDelete = { onPermanentlyDeleteNote(note) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrashNoteCard(
    note: DeletedNote,
    onRestore: () -> Unit,
    onPermanentlyDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Add color for consistency
                )
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Add color for consistency
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme color or custom background
                    ) {
                        DropdownMenuItem(
                            text = { Text("Restore", color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme color or custom text color
                            onClick = { onRestore(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.Restore, contentDescription = "Restore", tint = MaterialTheme.colorScheme.onSurface) } // Use MaterialTheme color or custom tint
                        )
                        DropdownMenuItem(
                            text = { Text("Delete forever", color = MaterialTheme.colorScheme.error) }, // Use MaterialTheme color (error for delete)
                            onClick = { onPermanentlyDelete(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.DeleteForever, contentDescription = "Delete Forever", tint = MaterialTheme.colorScheme.error) } // Use MaterialTheme color (error for delete)
                        )
                    }
                }
            }
            Text(
                text = note.contentSnippet,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = "Deleted: ${note.deletionDate}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}


@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrashScreenPreview() {
    val sampleNotes = List(5) { index ->
        DeletedNote(
            id = "note_$index",
            title = "Judul Catatan Dihapus $index",
            contentSnippet = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus...",
            deletionDate = "2 hari lalu"
        )
    }
    SealnoteTheme(darkTheme = true) {
        TrashScreen(deletedNotes = sampleNotes)
    }
}