package com.example.sealnote.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sealnote.R
import com.example.sealnote.model.Notes
import com.example.sealnote.viewmodel.BookmarksViewModel
import kotlinx.coroutines.launch

/**
 * Entry point cerdas untuk layar bookmark. Menghubungkan ViewModel ke UI.
 */
@Composable
fun BookmarksRoute(
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarkedNotes by viewModel.bookmarkedNotes.collectAsStateWithLifecycle()

    BookmarksScreen(
        bookmarkedNotes = bookmarkedNotes,
        onNavigateToAddNote = onNavigateToAddNote,
        onNavigateTo = onNavigateTo
    )
}

/**
 * Composable yang hanya bertugas menampilkan UI berdasarkan data yang diterima.
 */





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    bookmarkedNotes: List<Notes>,
    onNavigateToAddNote: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Letakkan semua NavigationDrawerItem Anda di sini
                // Contoh:
                Text("SealNote Menu", modifier = Modifier.padding(16.dp), style = typography.titleLarge)
                Divider()
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = false,
                    onClick = { onNavigateTo("all_notes") }
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                // ... item lainnya
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bookmarks") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Search Action */ }) {
                            Icon(painterResource(id = R.drawable.ic_search), "Search")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.Add, "Add Note")
                }
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(bookmarkedNotes, key = { it.id }) { note ->
                    // Ganti dengan Composable NoteCard Anda yang sebenarnya
                    Card(modifier = Modifier.padding(4.dp)) {
                        Text(note.title, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}