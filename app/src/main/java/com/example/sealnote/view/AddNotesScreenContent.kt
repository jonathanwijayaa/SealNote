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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.sealnote.R // Pastikan R diimpor dengan benar

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
    val darkBlue = Color(0xFF1E2A3A)
    val lightGray = Color(0x80FFFFFF)
    val white = Color.White

    Scaffold(
        containerColor = darkBlue,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Note", color = white) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = white
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkBlue
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = darkBlue,
                contentColor = white,
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
                            tint = white
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { /* TODO: Camera action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Take Photo",
                            tint = white
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = createdDate,
                        color = lightGray,
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
                color = lightGray,
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
    val textFieldBackground = Color(0xFF2A3B4F) // Slightly lighter than dark blue
    val white = Color.White
    val lightGray = Color(0x80FFFFFF)

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = lightGray) },
        textStyle = TextStyle(color = white, fontSize = 16.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = textFieldBackground,
            unfocusedContainerColor = textFieldBackground,
            disabledContainerColor = textFieldBackground,
            cursorColor = white,
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