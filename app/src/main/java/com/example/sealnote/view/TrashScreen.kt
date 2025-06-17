package com.example.sealnote.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore

import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.sealnote.R // Pastikan R diimpor untuk painterResource
import com.example.sealnote.ui.theme.SealnoteTheme

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

    deletedNotes: List<DeletedNote> = emptyList(),
    onRestoreNote: (DeletedNote) -> Unit = {},
    onPermanentlyDeleteNote: (DeletedNote) -> Unit = {},
    // Parameter navigasi untuk Navigation Drawer
    onNavigateHomepage: () -> Unit,
    onNavigateToAddNote: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSecretNotes: () -> Unit,
    onNavigateToTrash: () -> Unit, // Ini adalah layar saat ini
    onNavigateToSettings: () -> Unit

    trashedNotes: List<Notes>,
    snackbarHostState: SnackbarHostState,
    onRestoreNote: (String) -> Unit,
    onPermanentlyDeleteNote: (String) -> Unit,
    onNavigateTo: (String) -> Unit

) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) } // State untuk mengontrol aktifnya SearchBar
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Sort by Deletion Date", "Sort by Title")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

    val appBarContainerColor = MaterialTheme.colorScheme.surface
    val appBarContentColor = MaterialTheme.colorScheme.onSurface
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer // Untuk warna DropdownMenuBackground
    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer // Untuk warna teks DropdownMenuItem

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
                    selected = false,
                    onClick = { onNavigateToBookmarks(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmarks", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
                NavigationDrawerItem(
                    label = { Text("Secret Notes", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,

                    onClick = { onNavigateToSecretNotes(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Lock, contentDescription = "Secret Notes", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON

                    onClick = { onNavigateTo("homepage"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "All Notes") }

                )
                NavigationDrawerItem(
                    label = { Text("Trash", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Trash", color = appBarContentColor) }, // Warna teks judul
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = appBarContentColor) // Warna ikon menu
                        }
                    },
                    actions = {
                        // Icon Search yang akan mengaktifkan SearchBar
                        IconButton(onClick = { isSearchActive = !isSearchActive }) { // Toggle search bar
                            Icon(Icons.Default.Search, contentDescription = "Search Trash", tint = appBarContentColor) // Warna ikon search
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = appBarContainerColor, // Background TopAppBar
                        titleContentColor = appBarContentColor,
                        navigationIconContentColor = appBarContentColor,
                        actionIconContentColor = appBarContentColor
                    )
                )
            }
        ) { innerPadding ->
            val filteredNotes = remember(searchQuery, deletedNotes, selectedSortOption) {
                var notesToFilter = deletedNotes
                if (searchQuery.isNotBlank()) {
                    notesToFilter = notesToFilter.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.contentSnippet.contains(searchQuery, ignoreCase = true)
                    }
                }
                when (selectedSortOption) {
                    "Sort by Deletion Date" -> notesToFilter.sortedByDescending { it.deletionDate } // Asumsi format tanggal memungkinkan sorting string
                    "Sort by Title" -> notesToFilter.sortedBy { it.title }
                    else -> notesToFilter
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

                // DockedSearchBar ditampilkan secara kondisional di sini
                // Ketika isSearchActive true, DockedSearchBar akan muncul
                AnimatedVisibility(visible = isSearchActive) { // Membutuhkan AnimatedVisibility atau serupa
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
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search in trash...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            inputFieldColors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary, // Cursor color
                                focusedIndicatorColor = Color.Transparent, // Hapus indikator default
                                unfocusedIndicatorColor = Color.Transparent, // Hapus indikator default
                            )
                        )
                    ) {
                        // Konten untuk menampilkan hasil/saran pencarian di dalam SearchBar yang aktif
                        // Anda dapat menampilkan saran pencarian di sini
                        if (searchQuery.isNotEmpty()) {
                            Text(
                                "Mencari catatan dengan '${searchQuery}'...",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                "Ketik untuk mencari.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }


                // --- Row untuk Sorting di bawah TopAppBar ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // Padding horizontal
                        .padding(top = if (!isSearchActive) 8.dp else 0.dp, bottom = 8.dp), // Padding vertikal, sesuaikan jika search bar aktif
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // Pindahkan "Sorted by" ke kanan
                ) {
                    // Anda bisa menambahkan Text("Trash Items") di sini jika tidak ada di TopAppBar
                    // atau jika Anda ingin judul konten terpisah dari judul TopAppBar.
                    // Text(
                    //     text = "Trash Items",
                    //     style = MaterialTheme.typography.titleMedium,
                    //     color = MaterialTheme.colorScheme.onBackground,
                    //     modifier = Modifier.weight(1f) // Membuatnya mengisi ruang ke kiri
                    // )

                    Box { // Bungkus DropdownMenu untuk positioning
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(onClick = { isSortMenuExpanded = true })
                                .padding(vertical = 4.dp) // Padding untuk area klik
                        ) {
                            Text(
                                text = "Sorted by",
                                color = MaterialTheme.colorScheme.onSurfaceVariant, // Warna teks
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.Sort, // Menggunakan ikon sort yang sama
                                contentDescription = "Sort Notes",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant, // Warna ikon
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant) // Background menu
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        selectedSortOption = option
                                        isSortMenuExpanded = false
                                    }
                                )

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
                // --- Akhir Row untuk Sorting ---

                if (!isSearchActive) {
                    Column(modifier = Modifier.fillMaxSize()) { // Gunakan Column untuk mengatur posisi di dalam innerPadding
                        Text(
                            text = "Items in trash are automatically deleted after 30 days.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()

                                .padding(horizontal = 16.dp, vertical = 8.dp),

                                .padding(16.dp),

                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
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
                    style = MaterialTheme.typography.titleSmall, // Menggunakan typography Material3
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

                        Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = MaterialTheme.colorScheme.onSurfaceVariant)

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

                            leadingIcon = { Icon(Icons.Outlined.Restore, contentDescription = "Restore", tint = MaterialTheme.colorScheme.onSurface) }

                            leadingIcon = { Icon(Icons.Outlined.Restore, "Restore", tint = MaterialTheme.colorScheme.onSurface) }

                        )
                        DropdownMenuItem(
                            text = { Text("Delete forever", color = MaterialTheme.colorScheme.error) },
                            onClick = { onPermanentlyDelete(); isMenuExpanded = false },

                            leadingIcon = { Icon(Icons.Outlined.DeleteForever, contentDescription = "Delete Forever", tint = MaterialTheme.colorScheme.error) }

                            leadingIcon = { Icon(Icons.Outlined.DeleteForever, "Delete Forever", tint = MaterialTheme.colorScheme.error) }

                        )
                    }
                }
            }
            Text(

                text = note.contentSnippet,
                style = MaterialTheme.typography.bodySmall, // Menggunakan typography Material3

                text = note.content,
                fontSize = 13.sp,

                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(

                text = "Deleted: ${note.deletionDate}",
                style = MaterialTheme.typography.labelSmall, // Menggunakan typography Material3

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

            contentSnippet = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus, bisa jadi lebih panjang dari ini.",
            deletionDate = "2 hari lalu"

            content = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus...",
            updatedAt = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(index.toLong()))

        )
    }
    SealnoteTheme(darkTheme = true) {
        TrashScreen(

            deletedNotes = sampleNotes,
            onNavigateHomepage = {},
            onNavigateToAddNote = {},
            onNavigateToProfile = {},
            onNavigateToBookmarks = {},
            onNavigateToSecretNotes = {},
            onNavigateToTrash = {},
            onNavigateToSettings = {}

            trashedNotes = sampleNotes,
            snackbarHostState = remember { SnackbarHostState() },
            onRestoreNote = {},
            onPermanentlyDeleteNote = {},
            onNavigateTo = {}

        )
    }
}