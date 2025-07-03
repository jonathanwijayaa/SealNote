package com.example.sealnote.view

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sealnote.di.ImageSaver
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.di.MediaStoreHelper
import com.example.sealnote.di.MediaStoreHelper.createImageUri
import com.example.sealnote.viewmodel.AddEditNoteViewModel
import com.example.sealnote.viewmodel.UiEvent
import java.io.File

@Composable
fun AddEditNoteRoute(
    onBack: () -> Unit,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val title                by viewModel.title.collectAsStateWithLifecycle()
    val content              by viewModel.content.collectAsStateWithLifecycle()
    val isBookmarked         by viewModel.isBookmarked.collectAsStateWithLifecycle()
    val isSecret             by viewModel.isSecret.collectAsStateWithLifecycle()
    val currentImageUri      by viewModel.currentImageUri.collectAsStateWithLifecycle()
    val imageUrl by viewModel.imageUrl.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context            = LocalContext.current

    var tempImageFileUri by remember { mutableStateOf<Uri?>(null) }

    val storagePermission = remember {
        if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
    fun makeTempImageFile(): File {
        val dir = context.getExternalFilesDir("Pictures") ?: context.filesDir
        return File(dir, "IMG_${System.currentTimeMillis()}.jpg")
    }

    /* FileProvider URI */
    fun tempFileUri(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    var tempTempFile by remember { mutableStateOf<File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageFileUri != null && tempTempFile != null) {
            // salin ke galeri
            val galleryUri = ImageSaver.copyTempToGallery(context, tempTempFile!!)
            tempTempFile!!.delete()                       // hapus file sementara
            viewModel.onImageSelected(galleryUri)
        } else {
            Toast.makeText(context, "Failed to capture image.", Toast.LENGTH_SHORT).show()
        }
    }


    fun launchCamera() {
        val file = makeTempImageFile()
        val uri  = tempFileUri(file)
        tempTempFile     = file
        tempImageFileUri = uri
        takePictureLauncher.launch(uri)
    }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(context, "Izin penyimpanan diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            /* setelah kamera diizinkan, minta storage */
            storagePermissionLauncher.launch(storagePermission)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.onImageSelected(uri) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.NoteSaved    -> onBack()
            }
        }
    }

    AddEditNoteScreen(
        title                 = title,
        content               = content,
        isBookmarked          = isBookmarked,
        isSecret              = isSecret,
        imageUri = currentImageUri,
        imageUrl = imageUrl,
        onTitleChange         = { viewModel.title.value = it },
        onContentChange       = { viewModel.content.value = it },
        snackbarHostState     = snackbarHostState,
        onBack                = onBack,
        onSaveClick           = viewModel::onSaveNoteClick,
        onCameraClick         = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        onGalleryClick        = { imagePickerLauncher.launch("image/*") },
        onToggleBookmark      = viewModel::toggleBookmarkStatus,
        onToggleSecret        = viewModel::toggleSecretStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    title: String,
    content: String,
    isBookmarked: Boolean,
    isSecret: Boolean,
    imageUri: Uri?,
    imageUrl: String?,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleSecret: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (title.isEmpty()) "Add Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = onToggleSecret) {
                        Icon(
                            imageVector = if (isSecret) Icons.Filled.Lock else Icons.Filled.LockOpen,
                            contentDescription = if (isSecret) "Make Public" else "Make Secret",
                            tint = if (isSecret) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = onSaveClick) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                },
                modifier = Modifier.shadow(2.dp)
            )
        },
        bottomBar = {
            BottomAppBar {
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = title,
                onValueChange = onTitleChange,
                hint = "Title",
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Menampilkan gambar jika ada
            val imageToDisplay = imageUri ?: Uri.parse(imageUrl ?: "") // Memastikan Uri tidak null
            if (imageToDisplay.toString().isNotEmpty()) { // Hanya tampilkan jika URI/URL tidak kosong
                AsyncImage(
                    model = imageToDisplay,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            CustomTextField(
                value = content,
                onValueChange = onContentChange,
                hint = "Start writing your note here...",
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                minLines = 15
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
        placeholder = { Text(hint, style = textStyle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = modifier,
        minLines = minLines,
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddEditNoteScreenPreview() {
    SealnoteTheme {
        AddEditNoteScreen(
            title               = "My Awesome Note",
            content             = "This is the content of the note.",
            isBookmarked        = true,
            isSecret            = false,
            imageUri            = null,
            imageUrl = null,
            onTitleChange       = {},
            onContentChange     = {},
            snackbarHostState   = SnackbarHostState(),
            onBack              = {},
            onSaveClick         = {},
            onCameraClick       = {},
            onGalleryClick      = {},
            onToggleBookmark    = {},
            onToggleSecret      = {}
        )
    }
}