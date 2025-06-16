package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sealnote.R
import com.example.sealnote.viewmodel.AddEditNoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEditNoteRoute(
    onBack: () -> Unit,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val note by viewModel.note.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    onBack() // Kembali ke layar sebelumnya setelah berhasil menyimpan
                }
            }
        }
    }

    AddNotesScreen(
        title = title,
        notesContent = content,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        snackbarHostState = snackbarHostState,
        createdDate = note?.createdAt?.formatToString() ?: "Just now",
        lastChangedDate = note?.updatedAt?.formatToString() ?: "Just now",
        onBack = onBack,
        onSave = { viewModel.saveNote() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(
    title: String,
    notesContent: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    createdDate: String,
    lastChangedDate: String,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val onContainerColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = containerColor,
        topBar = {
            TopAppBar(
                title = { Text(if (createdDate == "Just now") "Add Note" else "Edit Note", color = onContainerColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = onContainerColor)
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Text("Save", color = onContainerColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = { /* ... Kode BottomAppBar Anda tetap sama ... */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CustomTextField(
                value = title,
                onValueChange = onTitleChange,
                hint = "Title",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = notesContent,
                onValueChange = onContentChange,
                hint = "Notes...",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 10
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Last Edited: $lastChangedDate",
                color = secondaryTextColor,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// Composable CustomTextField Anda tetap sama, tidak perlu diubah
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) { /* ... */ }

// Helper function ini juga bisa Anda pindahkan ke file terpisah
private fun Date.formatToString(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(this)
}