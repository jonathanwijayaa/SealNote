// path: app/src/main/java/com/example/sealnote/view/TrashScreen.kt

package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sealnote.data.ThemeOption // <-- TAMBAHKAN IMPORT INI
import com.example.sealnote.model.Notes
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.util.SortOption
import com.example.sealnote.viewmodel.TrashViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun TrashRoute(
    navController: NavHostController,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val trashedNotes by viewModel.trashedNotes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    TrashScreen(
        currentRoute = currentRoute,
        trashedNotes = trashedNotes,
        searchQuery = searchQuery,
        sortOption = sortOption,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortOptionChange = viewModel::onSortOptionChange,
        onRestoreNote = viewModel::restoreNote,
        onPermanentlyDeleteNote = viewModel::deletePermanently,
        onNavigate = { route ->
            if (currentRoute != route) {
                navController.navigate(route) { launchSingleTop = true }
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
fun TrashScreen(
    currentRoute: String?,
    trashedNotes: List<Notes>,
    searchQuery: String,
    sortOption: SortOption,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onRestoreNote: (String) -> Unit,
    onPermanentlyDeleteNote: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val menuItems = listOf(
        "homepage" to ("All Notes" to Icons.Default.Home),
        "bookmarks" to ("Bookmarks" to Icons.Default.BookmarkBorder),
        "secretNotes" to ("Secret Notes" to Icons.Default.Lock),
        "trash" to ("Trash" to Icons.Default.Delete),
        "settings" to ("Settings" to Icons.Default.Settings),
        "profile" to ("Profile" to Icons.Default.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    "SealNote Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                menuItems.forEach { (route, details) ->
                    val (label, icon) = details
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
                        selected = currentRoute == route,
                        onClick = { scope.launch { drawerState.close() }; onNavigate(route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Calculate, "Back to Calculator") },
                    label = { Text("Back to Calculator", style = MaterialTheme.typography.bodyLarge) }, // Tipografi dari tema
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onNavigateToCalculator() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (isSearchActive) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                "Search in trash...",
                                                style = MaterialTheme.typography.bodyLarge, // Tipografi dari tema
                                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) // Warna placeholder dari tema
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            LaunchedEffect(Unit) { focusRequester.requestFocus() }
                        } else {
                            Text(
                                "Trash",
                                style = MaterialTheme.typography.headlineSmall, // Tipografi judul dari tema
                                color = MaterialTheme.colorScheme.onPrimary // Warna judul dari tema
                            )
                        }
                    },
                    navigationIcon = {
                        if (isSearchActive) {
                            IconButton(onClick = { isSearchActive = false; onSearchQueryChange("") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Open Menu", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    },
                    actions = {
                        if (isSearchActive) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, "Clear Search", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        } else {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, "Search Trash", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        TextButton(
                            onClick = { isSortMenuExpanded = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant) // Warna teks tombol dari tema
                        ) {
                            Text(sortOption.displayName, style = MaterialTheme.typography.labelLarge) // Tipografi dari tema
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort Options", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Warna ikon dari tema
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                    onSortOptionChange(option)
                                    isSortMenuExpanded = false
                                })
                            }
                        }
                    }
                }
                HorizontalDivider()
                Text(
                    text = "Items in trash are automatically deleted after 30 days.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trashedNotes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (searchQuery.isNotEmpty()) "No results found" else "Trash is empty.",
                            style = MaterialTheme.typography.bodyLarge, // Tipografi dari tema
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Warna teks dari tema
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trashedNotes, key = { it.id }) { note ->
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
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Warna container dari tema
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Tipografi dari tema
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface // Warna teks dari tema
                )
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, "More Options", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Warna ikon dari tema
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Latar belakang menu dari tema
                    ) {
                        DropdownMenuItem(
                            text = { Text("Restore", style = MaterialTheme.typography.bodyLarge) }, // Tipografi dari tema
                            onClick = { onRestore(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.Restore, "Restore", tint = MaterialTheme.colorScheme.onSurface) }, // Warna ikon dari tema
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface // Warna teks dari tema
                            )
                        )
                        DropdownMenuItem(
                            text = { Text("Delete forever", style = MaterialTheme.typography.bodyLarge) }, // Tipografi dari tema
                            onClick = { onPermanentlyDelete(); isMenuExpanded = false },
                            leadingIcon = { Icon(Icons.Outlined.DeleteForever, "Delete Forever", tint = MaterialTheme.colorScheme.error) }, // Warna ikon dari tema
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error // Warna teks error dari tema
                            )
                        )
                    }
                }
            }
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium, // Tipografi dari tema
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Warna teks dari tema dengan alpha
            )
            Text(
                text = "Deleted: ${note.updatedAt.toRelativeTimeString()}",
                style = MaterialTheme.typography.labelSmall, // Tipografi dari tema
                color = MaterialTheme.colorScheme.outline // Warna teks dari tema
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

// --- FUNGSI PREVIEW YANG DIPERBAIKI ---
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrashScreenPreview() {
    // Data sampel dibuat di sini
    val sampleNotes = List(5) { index ->
        Notes(
            id = "note_$index",
            title = "Judul Catatan Dihapus $index",
            content = "Ini adalah cuplikan singkat dari konten catatan yang telah dihapus...",
            updatedAt = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(index.toLong()))
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    SealnoteTheme(themeOption = ThemeOption.DARK) {
        TrashScreen(
            currentRoute = "trash",
            trashedNotes = sampleNotes,
            searchQuery = "",
            sortOption = SortOption.BY_DATE_DESC,
            snackbarHostState = snackbarHostState,
            onSearchQueryChange = {},
            onSortOptionChange = {},
            onRestoreNote = {},
            onPermanentlyDeleteNote = {},
            onNavigate = {},
            onNavigateToCalculator = {}
        )
    }
}