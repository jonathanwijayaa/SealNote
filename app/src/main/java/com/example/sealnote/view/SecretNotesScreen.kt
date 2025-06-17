package com.example.sealnote.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Hanya untuk Color.Transparent atau custom color tags
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Digunakan untuk lineHeight yang spesifik
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme // Pastikan SealnoteTheme diimpor
import kotlinx.coroutines.launch


// Data class placeholder untuk item catatan utama
data class MainNote(
    val id: String,
    val title: String,
    val contentPreview: String,
    val date: String,
    val colorTag: Color? = null // Biarkan Color? di sini untuk custom card colors
)

@Composable
fun NoteCardItem(note: MainNote, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Gunakan secondaryContainer dari MaterialTheme sebagai default background kartu
            // Dan onSecondaryContainer untuk teks di atasnya
            containerColor = note.colorTag ?: MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .defaultMinSize(minHeight = 120.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer, // Teks pada kartu
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.contentPreview,
                style = MaterialTheme.typography.bodyMedium,
                // Gunakan warna yang kontras dengan secondaryContainer, dan sesuaikan alpha
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = note.date,
                style = MaterialTheme.typography.labelSmall,
                // Gunakan warna yang kontras dengan secondaryContainer, dan sesuaikan alpha
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End),
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretNotesScreen(
    notes: List<MainNote> = emptyList(),
    onNoteClick: (MainNote) -> Unit = {},
    onSortClick: () -> Unit = {},
    onFabClick: () -> Unit = {},
    onNavigateHomepage: () -> Unit,
    onNavigateToAddNote: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSecretNotes: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    selected = true,
                    onClick = { onNavigateToSecretNotes(); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Lock, contentDescription = "Secret Notes", tint = MaterialTheme.colorScheme.onSurface) } // <-- ADDED ICON
                )
                NavigationDrawerItem(
                    label = { Text("Trash", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    selected = false,
                    onClick = { onNavigateToTrash(); scope.launch { drawerState.close() } },
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
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background, // Menggunakan background dari tema
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "Secret Notes",
                                color = MaterialTheme.colorScheme.onSurface, // Teks judul di TopAppBar
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface) // Ikon menu
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface, // Background TopAppBar
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onFabClick,
                        containerColor = MaterialTheme.colorScheme.primary, // Menggunakan warna primary dari tema
                        contentColor = MaterialTheme.colorScheme.onPrimary, // Warna ikon di FAB
                        shape = FloatingActionButtonDefaults.shape
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add New Note"
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "All Secret Notes",
                            color = MaterialTheme.colorScheme.onBackground, // Teks di atas background utama
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(onClick = onSortClick)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sorted by",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), // Warna teks sekunder
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sort),
                                contentDescription = "Sort Notes",
                                tint = MaterialTheme.colorScheme.onBackground, // Warna ikon
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 16.dp + 56.dp + 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                onClick = { onNoteClick(note) }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SecretNotesScreenPreview() {
    val sampleNotes = List(7) { index ->
        MainNote(
            id = "note_$index",
            title = "Judul Catatan Penting $index",
            contentPreview = "Ini adalah isi dari catatan rahasia nomor $index yang sangat penting dan perlu diingat baik-baik. Konten bisa beberapa baris.",
            date = "Mei ${20 + index}, 2025",
            colorTag = if (index % 3 == 0) Color(0xFF4A4E69) else if (index % 3 == 1) Color(0xFF2C3E50) else null
        )
    }
    SealnoteTheme {
        SecretNotesScreen(
            notes = sampleNotes,
            onNavigateHomepage = {},
            onNavigateToAddNote = {},
            onNavigateToProfile = {},
            onNavigateToBookmarks = {},
            onNavigateToSecretNotes = {},
            onNavigateToTrash = {},
            onNavigateToSettings = {}
        )
    }
}