package com.example.sealnote.view // Sesuaikan dengan package Anda

import androidx.compose.foundation.background // <-- ADD THIS IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort // REVISI: Menggunakan ikon AutoMirrored
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close // <-- ADD THIS IMPORT for Icons.Default.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan import ini sesuai dengan nama tema Anda
import kotlinx.coroutines.launch

// Import your custom colors if you plan to use them instead of MaterialTheme.colorScheme
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


// Data class Note (tidak berubah)
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Parameter untuk event navigasi dan aksi
    onNoteClick: (Note) -> Unit = {},
    onAddNoteClick: () -> Unit = {},
    onDeleteNote: (Note) -> Unit = {}
) {
    // --- State Management ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Sort by Date", "Sort by Title A-Z", "Sort by Title Z-A")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }

    // --- Data Contoh ---
    val notes = remember {
        List(8) { index ->
            Note(
                id = index,
                title = "Judul Catatan $index",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id.",
                date = "Jun ${10 + index}, 2025",
                tag = "Contoh"
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("SealNote Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                HorizontalDivider() // REVISI: Menggunakan HorizontalDivider
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("All Notes") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Trash") },
                    label = { Text("Trash") },
                    selected = false,
                    onClick = { /* TODO: Navigasi ke halaman Sampah */ }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("All Notes") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Navigation Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Notes")
                        }
                        Box {
                            IconButton(onClick = { isSortMenuExpanded = true }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort Notes")
                            }
                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                // Using Modifier.background for the dropdown menu background
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme colors or your custom DropdownMenuBackground
                            ) {
                                sortOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme colors or your custom DropdownMenuItemTextColor
                                        onClick = {
                                            selectedSortOption = option
                                            isSortMenuExpanded = false
                                            // TODO: Terapkan logika sorting
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddNoteClick,
                    shape = FloatingActionButtonDefaults.shape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new note")
                }
            }
        ) { innerPadding ->
            // --- KONTEN UTAMA DENGAN PERBAIKAN PADDING ---
            // Column terluar ini menerapkan innerPadding dari Scaffold
            Column(
                modifier = Modifier
                    .padding(innerPadding) // ✅ innerPadding DITERAPKAN DI SINI
                    .fillMaxSize()
            ) {
                // Logika if/else untuk menampilkan SearchBar atau Grid Catatan
                // FIX: Updated DockedSearchBar usage
                if (isSearchActive) {
                    DockedSearchBar(
                        query = searchQuery,
                        onQueryChange = { newQuery -> searchQuery = newQuery },
                        onSearch = { newQuery ->
                            isSearchActive = false
                            // TODO: Handle search action
                        },
                        active = isSearchActive,
                        onActiveChange = { newActiveState -> isSearchActive = newActiveState },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        placeholder = { Text("Search your notes...", color = MaterialTheme.colorScheme.onSurfaceVariant) }, // Use MaterialTheme color or SearchBarHintColor
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.onSurfaceVariant) }, // Use MaterialTheme color or SearchBarIconColor
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search query", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Use MaterialTheme color or SearchBarIconColor
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Hasil pencarian untuk '$searchQuery' akan muncul di sini.",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    // LazyVerticalGrid sekarang tidak perlu innerPadding lagi karena parent-nya sudah punya
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 16.dp + 80.dp // Padding bawah agar item terakhir tidak tertutup FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCard(
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
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isCardMenuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Box {
                    IconButton(
                        onClick = { isCardMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Note options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = isCardMenuExpanded,
                        onDismissRequest = { isCardMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Use MaterialTheme color or DropdownMenuBackground
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.onSurface) }, // Use MaterialTheme color or DropdownMenuItemTextColor
                            onClick = {
                                onDeleteClick()
                                isCardMenuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface) // Use MaterialTheme color or DropdownMenuItemTextColor
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(min = 60.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.date,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = "Tag",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = note.tag,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

// Pratinjau untuk HomeScreen yang sudah direvisi
@Preview(showBackground = true, name = "Home Screen Revised")
@Composable
fun HomeScreenRevisedPreview() {
    SealnoteTheme(darkTheme = true) {
        HomeScreen()
    }
}