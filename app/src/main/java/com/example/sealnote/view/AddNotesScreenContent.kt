package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color // TETAPKAN INI jika Anda masih menggunakan Color.White atau Color(0xFF...) untuk debugging
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.sealnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreenContent(
    title: String,
    notes: String,
    createdDate: String,
    lastChangedDate: String,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onBack: () -> Unit
) {

    val containerColor = MaterialTheme.colorScheme.surface
    val onContainerColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val TextFieldBackground = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        containerColor = containerColor, // Menggunakan warna tema
        topBar = {
            TopAppBar(
                title = { Text("Add Note", color = onContainerColor) }, // Menggunakan warna tema
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = onContainerColor // Menggunakan warna tema
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor // Menggunakan warna tema
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = containerColor, // Menggunakan warna tema
                contentColor = onContainerColor, // Menggunakan warna tema
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { /* TODO: Image action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "Add Image",
                            tint = onContainerColor // Menggunakan warna tema
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { /* TODO: Camera action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Take Photo",
                            tint = onContainerColor // Menggunakan warna tema
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = createdDate,
                        color = secondaryTextColor, // Menggunakan warna tema
                        fontSize = 14.sp
                    )
                }
            }
        }
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
                value = notes,
                onValueChange = onNotesChange,
                hint = "Notes...",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lastChangedDate,
                color = secondaryTextColor, // Menggunakan warna tema
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End)
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
    minLines: Int = 1
) {
    // --- Ganti hardcoded colors dengan MaterialTheme.colorScheme ---
    val textFieldBackgroundColor = MaterialTheme.colorScheme.surfaceVariant // Menggunakan warna tema
    val textColor = MaterialTheme.colorScheme.onSurface // Menggunakan warna tema
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant // Menggunakan warna tema

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = placeholderColor) }, // Menggunakan warna tema
        textStyle = TextStyle(color = textColor, fontSize = 16.sp), // Menggunakan warna tema
        colors = TextFieldDefaults.colors(
            focusedContainerColor = textFieldBackgroundColor, // Menggunakan warna tema
            unfocusedContainerColor = textFieldBackgroundColor, // Menggunakan warna tema
            disabledContainerColor = textFieldBackgroundColor, // Menggunakan warna tema
            cursorColor = textColor, // Menggunakan warna tema
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.shadow(4.dp, RoundedCornerShape(8.dp)),
        minLines = minLines
    )
}

@Preview(showBackground = true)
@Composable
fun AddNotesScreenPreview() {
    // Wrap preview dengan AppTheme agar warna tema diterapkan
    com.example.sealnote.ui.theme.AppTheme(darkTheme = true) { // Paksa tema gelap untuk pratinjau
        AddNotesScreenContent(
            title = "Meeting Notes",
            notes = "Discuss Q2 goals, client feedback, and action items.",
            createdDate = "Created: May 24, 2025",
            lastChangedDate = "Last Edited: May 24, 2025",
            onTitleChange = {},
            onNotesChange = {},
            onBack = {}
        )
    }
}