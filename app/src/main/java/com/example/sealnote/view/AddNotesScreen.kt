// path: app/src/main/java/com/example/sealnote/view/AddNotesScreen.kt

package com.example.sealnote.view

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Camera permission granted. Feature under development.", Toast.LENGTH_SHORT).show()
            // TODO: Implement camera logic with FileProvider to get a URI
        } else {
            Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    onBack()
                }
            }
        }
    }

    AddNotesScreen(
        title = title,
        notesContent = content,
        imageUri = imageUri,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        snackbarHostState = snackbarHostState,
        createdDate = note?.createdAt?.formatToString() ?: "Just now",
        lastChangedDate = note?.updatedAt?.formatToString() ?: "Just now",
        onBack = onBack,
        onSave = { viewModel.saveNote() },
        onCameraClick = {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        },
        onGalleryClick = {
            imagePickerLauncher.launch("image/*")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(
    title: String,
    notesContent: String,
    imageUri: Uri?,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    createdDate: String,
    lastChangedDate: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val onContainerColor = MaterialTheme.colorScheme.onSurface

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
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = onCameraClick) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = "Open Camera")
                    }
                    IconButton(onClick = onGalleryClick) {
                        Icon(Icons.Outlined.Image, contentDescription = "Open Gallery")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CustomTextField(
                value = title,
                onValueChange = onTitleChange,
                hint = "Title",
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            CustomTextField(
                value = notesContent,
                onValueChange = onContentChange,
                hint = "Start writing your note here...",
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                minLines = 10
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, style = textStyle) },
        modifier = modifier,
        minLines = minLines,
        textStyle = textStyle,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

private fun Date.formatToString(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault())
    return formatter.format(this)
}