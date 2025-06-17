package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.* // Import remember and mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme // Asumsi AppTheme Anda diimpor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(
    onBack: () -> Unit,
    onSave: (title: String, notes: String) -> Unit // Ini adalah signature yang diharapkan
) {
    var title by remember { mutableStateOf("") }
    var notesContent by remember { mutableStateOf("") }

    val createdDate = "Created: May 24, 2025"
    val lastChangedDate = "Last Edited: May 24, 2025"

    val containerColor = MaterialTheme.colorScheme.surface
    val onContainerColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = containerColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Note", color = onContainerColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = onContainerColor
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(title, notesContent) }) { // Panggilan yang benar
                        Text("Save", color = onContainerColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = containerColor,
                contentColor = onContainerColor,
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
                            tint = onContainerColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { /* TODO: Camera action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Take Photo",
                            tint = onContainerColor
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = createdDate,
                        color = secondaryTextColor,
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
                onValueChange = { title = it },
                hint = "Title",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = notesContent,
                onValueChange = { notesContent = it },
                hint = "Notes...",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lastChangedDate,
                color = secondaryTextColor,
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
    val textFieldBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = placeholderColor) },
        textStyle = TextStyle(color = textColor, fontSize = 16.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = textFieldBackgroundColor,
            unfocusedContainerColor = textFieldBackgroundColor,
            disabledContainerColor = textFieldBackgroundColor,
            cursorColor = textColor,
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
    com.example.sealnote.ui.theme.SealnoteTheme(darkTheme = true) {
        AddNotesScreen(
            onBack = {},
            onSave = { title, notes ->
                println("Preview Save: Title=$title, Notes=$notes")
            }
        )
    }
}
