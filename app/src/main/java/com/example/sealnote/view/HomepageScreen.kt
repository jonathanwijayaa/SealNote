package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.sealnote.model.Notes
import com.example.sealnote.viewmodel.HomepageViewModel
import kotlinx.coroutines.launch
import com.example.sealnote.view.NoteCard

@Composable
fun HomepageRoute(
    navController: NavHostController,
    viewModel: HomepageViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    HomepageScreen(
        notes = notes,
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
        // Navigasi untuk drawer
        onNavigateToBookmarks = { navController.navigate("bookmarks") },
        onNavigateToProfile = { navController.navigate("profile") },
        onNavigateToSecretNotes = { navController.navigate("secretNotesLocked") },
        onNavigateToTrash = { navController.navigate("trash") },
        onNavigateToSettings = { navController.navigate("settings") },
        // Aksi baru dari drawer
        onLogoutClick = {
            navController.navigate("login") {
                popUpTo("homepage") { inclusive = true }
            }
        },
        onNavigateToCalculator = {
            navController.navigate("stealthCalculator") {
                popUpTo("homepage") { inclusive = true }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    notes: List<Notes>,
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                // --- Isi dari Navigation Drawer ---
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmarks") },
                    label = { Text("Bookmarks") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBookmarks()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Lock, contentDescription = "Secret Notes") },
                    label = { Text("Secret Notes") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSecretNotes()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Delete, contentDescription = "Trash") },
                    label = { Text("Trash") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToTrash()
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Calculate, contentDescription = "Back to Calculator") },
                    label = { Text("Back to Calculator") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToCalculator()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Logout, contentDescription = "Logout") },
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
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note"
                    )
                }
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
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

