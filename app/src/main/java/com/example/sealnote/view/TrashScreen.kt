package com.example.sealnote.view

import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.sealnote.model.Notes
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.viewmodel.TrashViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Composable "Cerdas" yang menjadi pintu masuk untuk layar Sampah.
 * Menghubungkan ViewModel dengan UI.
 */
@Composable
fun TrashRoute(
    navController: NavHostController,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val trashedNotes by viewModel.trashedNotes.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efek untuk menampilkan pesan dari ViewModel (misal: "Catatan dipulihkan")
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    TrashScreen(
        trashedNotes = trashedNotes,
        snackbarHostState = snackbarHostState,
        onRestoreNote = { noteId -> viewModel.restoreNote(noteId) },
        onPermanentlyDeleteNote = { noteId -> viewModel.deletePermanently(noteId) },
        onNavigateTo = { route -> navController.navigate(route) }
    )
}

/**
 * Composable "Bodoh" yang hanya bertugas menampilkan UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    trashedNotes: List<Notes>,
    snackbarHostState: SnackbarHostState,
    onRestoreNote: (String) -> Unit,
    onPermanentlyDeleteNote: (String) -> Unit,
    onNavigateTo: (String) -> Unit
) {
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
                    onClick = { onNavigateTo("homepage"); scope.launch { drawerState.close() } },
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                sortOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
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
            val filteredNotes = remember(searchQuery, trashedNotes) {
                if (searchQuery.isBlank()) trashedNotes
                else trashedNotes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true)
                }
            }

            val sortedNotes = remember(selectedSortOption, filteredNotes) {
                when (selectedSortOption) {
                    "Sort by Deletion Date" -> filteredNotes.sortedByDescending { it.updatedAt }
                    "Sort by Title" -> filteredNotes.sortedBy { it.title }
                    else -> filteredNotes
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                DockedSearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery },
                    onSearch = { newQuery ->
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { newActiveState -> isSearchActive = newActiveState },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search in trash...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    }
                ) {
                    // Konten untuk menampilkan hasil/saran pencarian saat search bar aktif
                }

                if (!isSearchActive) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Items in trash are automatically deleted after 30 days.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sortedNotes, key = { it.id }) { note ->
                                TrashNoteCard(
                                    note = note,
                                    onRestore = { onRestoreNote(note.id) },
                                    onPermanentlyDelete = { onPermanentlyDeleteNote(note.id) }
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
    note: Notes,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, "More Options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Restore", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { onRestore(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.Restore, "Restore", tint = MaterialTheme.colorScheme.onSurface) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete forever", color = MaterialTheme.colorScheme.error) },
                            onClick = { onPermanentlyDelete(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.DeleteForever, "Delete Forever", tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
            Text(
                text = note.content,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = "Deleted: ${note.updatedAt.toRelativeTimeString()}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun Date?.toRelativeTimeString(): String {
    if (this == null) return "a moment ago"
    val now = System.currentTimeMillis()
    val diff = now - this.time

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    if (seconds < 60) return "just now"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    if (minutes < 60) return "$minutes min ago"
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return "$hours hours ago"
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return "$days days ago"
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrashScreenPreview() {
    val sampleNotes = List(5) { index ->
        Notes(
            id = "note_$index",
            title = "Judul Catatan Dihapus $index",
            content = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus...",
            updatedAt = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(index.toLong()))
        )
    }
    SealnoteTheme(darkTheme = true) {
        TrashScreen(
            trashedNotes = sampleNotes,
            snackbarHostState = remember { SnackbarHostState() },
            onRestoreNote = {},
            onPermanentlyDeleteNote = {},
            onNavigateTo = {}
        )
    }
}