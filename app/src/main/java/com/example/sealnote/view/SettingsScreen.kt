package com.example.sealnote.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sealnote.ui.theme.SealnoteTheme // Menggunakan SealnoteTheme

/**
 * Layar Pengaturan yang diimplementasikan dengan Jetpack Compose.
 * Menyediakan bilah atas dengan tombol kembali dan placeholder konten.
 *
 * @param onBack Callback yang akan dipanggil saat tombol kembali di bilah atas diklik.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    // Menggunakan skema warna dari MaterialTheme.colorScheme yang disediakan oleh SealnoteTheme
    val containerColor = MaterialTheme.colorScheme.surface
    val onContainerColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        containerColor = containerColor,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = onContainerColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = onContainerColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings Screen (Ini adalah kontennya)",
                color = onContainerColor // Menggunakan warna teks onContainerColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SealnoteTheme(darkTheme = true) { // Menggunakan SealnoteTheme untuk pratinjau
        SettingsScreen(onBack = {})
    }
}
