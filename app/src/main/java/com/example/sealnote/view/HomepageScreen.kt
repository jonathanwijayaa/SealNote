// path: app/src/main/java/com/example/sealnote/view/HomepageScreen.kt

package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.sealnote.model.Notes
import com.example.sealnote.util.SortOption
import com.example.sealnote.viewmodel.HomepageViewModel
import kotlinx.coroutines.launch

// Pastikan import NoteCard dari filenya sendiri
import com.example.sealnote.view.NoteCard

@Composable
fun HomepageRoute(
    navController: NavHostController,
    viewModel: HomepageViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    HomepageScreen(
        notes = notes,
        searchQuery = searchQuery,
        sortOption = sortOption,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortOptionChange = viewModel::onSortOptionChange,
        onNoteClick = { noteId ->
            navController.navigate("add_edit_note_screen/$noteId")
        },
        onDeleteNoteClick = { noteId ->
            viewModel.trashNote(noteId)
        },
        onNavigateToAddNote = {
            navController.navigate("add_edit_note_screen/null")
        },
        onToggleSecretClick = { noteId, currentStatus ->
            viewModel.toggleSecretStatus(noteId, currentStatus)
        },
        onNavigateToBookmarks = { navController.navigate("bookmarks") },
        onNavigateToProfile = { navController.navigate("profile") },
        onNavigateToSecretNotes = { navController.navigate("secretNotesLocked") },
        onNavigateToTrash = { navController.navigate("trash") },
        onNavigateToSettings = { navController.navigate("settings") },
        onLogoutClick = {
            // Panggil fungsi logout dari ViewModel untuk menghapus sesi user
            viewModel.logout()
            // Kemudian navigasikan ke halaman login
            navController.navigate("login") {
                // Hapus semua halaman sebelumnya dari back stack agar user tidak bisa kembali
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                // Pastikan hanya ada satu instance halaman login
                launchSingleTop = true
            }
        },
        onNavigateToCalculator = {
            navController.navigate("stealthCalculator") { popUpTo("homepage") { inclusive = true } }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    notes: List<Notes>,
    searchQuery: String,
    sortOption: SortOption,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onNoteClick: (String) -> Unit,
    onDeleteNoteClick: (String) -> Unit,
    onNavigateToAddNote: () -> Unit,
    onToggleSecretClick: (String, Boolean) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSecretNotes: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.AddCircleOutline, "New Note") },
                    label = { Text("New Note") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAddNote()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.BookmarkBorder, "Bookmarks") },
                    label = { Text("Bookmarks") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBookmarks()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Lock, "Secret Notes") },
                    label = { Text("Secret Notes") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSecretNotes()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Delete, "Trash") },
                    label = { Text("Trash") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToTrash()
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Settings, "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Calculate, "Back to Calculator") },
                    label = { Text("Back to Calculator") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToCalculator()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Logout, "Logout") },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, "Sort Notes")
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false }
                            ) {
                                SortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.displayName) },
                                        onClick = {
                                            onSortOptionChange(option)
                                            isSortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.Add, "Add Note")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, "Clear search")
                            }
                        }
                    }
                ) {}

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEditClick = { onNoteClick(note.id) },
                            onDeleteClick = { onDeleteNoteClick(note.id) },
                            onToggleSecretClick = { onToggleSecretClick(note.id, note.isSecret) }
                        )
                    }
                }
            }
        }
    }
}