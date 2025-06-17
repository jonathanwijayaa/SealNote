package com.example.sealnote.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme
import kotlinx.coroutines.launch

// Definisi Warna sesuai gambar
val ScreenBackground = Color(0xFF1A1C2E)
val CardBackgroundColor = Color(0xFF2C2F48)
val FabColor = Color(0xFF7B5DFF)
val PrimaryTextColor = Color.White
val SecondaryTextColor = Color(0xFFD1D1D1)
val TertiaryTextColor = Color(0xFF9E9E9E)
val IconColor = Color.White

// Data class Anda (tetap sama)
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddNote: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSecretNotes: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val notes = remember {
        List(8) { index ->
            Note(
                id = index,
                title = "Title",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id.",
                date = "Mar 22, 2025",
                tag = "Example"
            )
        }
    }

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
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
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
        }
    ) {
        Scaffold(
            containerColor = ScreenBackground,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "All notes",
                            color = PrimaryTextColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = PrimaryTextColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // TODO: Action to show/hide search TextField or navigate to a separate search screen
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                                tint = IconColor
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { /* TODO: Aksi untuk mengurutkan catatan */ }
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Sorted by",
                                color = PrimaryTextColor,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Sort notes",
                                tint = PrimaryTextColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ScreenBackground,
                        titleContentColor = PrimaryTextColor,
                        navigationIconContentColor = PrimaryTextColor,
                        actionIconContentColor = PrimaryTextColor
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAddNote,
                    containerColor = FabColor,
                    contentColor = IconColor,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add new note"
                    )
                }
            },
            modifier = modifier
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(note)
                    }
                }
            }
        }
    }
}


@Composable
fun NoteCard(note: Note) { // Tetap di file ini atau pindah ke file terpisah jika NoteCard.kt tidak ada
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontSize = 13.sp,
                color = SecondaryTextColor,
                lineHeight = 18.sp,
                maxLines = 4
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
                    color = TertiaryTextColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = "Tag",
                        tint = TertiaryTextColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = note.tag,
                        fontSize = 11.sp,
                        color = TertiaryTextColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen Preview Dark")
@Composable
fun HomeScreenPreview() {
    SealnoteTheme {
        HomepageScreen(
            onNavigateToAddNote = {},
            onNavigateToProfile = {},
            onNavigateToBookmarks = {},
            onNavigateToSecretNotes = {},
            onNavigateToTrash = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(showBackground = true, name = "Note Card Preview Dark", backgroundColor = 0xFF1A1C2E)
@Composable
fun NoteCardPreview() {
    SealnoteTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NoteCard(
                Note(
                    id = 0,
                    title = "Title",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id.",
                    date = "Mar 22, 2025",
                    tag = "Example"
                )
            )
        }
    }
}
