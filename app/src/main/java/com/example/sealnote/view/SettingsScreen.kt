package com.example.sealnote.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch // Pastikan ini ada
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // <--- Pastikan ini ada
import androidx.compose.runtime.mutableStateOf // <--- Pastikan ini ada
import androidx.compose.runtime.remember // <--- Pastikan ini ada
import androidx.compose.runtime.setValue // <--- Pastikan ini ada
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme

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
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    var isSystemThemeChecked by remember { mutableStateOf(true)}

    Scaffold(
        containerColor = containerColor,
        topBar = {
            CenterAlignedTopAppBar(
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
        // Mengubah Box menjadi Column untuk menata item secara vertikal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Penting: Terapkan padding dari Scaffold di sini
        ) {
            // Item "Theme" dengan sub-teks dan switch
            SettingsThemeItem(
                title = "Theme",
                subtitle = "Using system theme",
                showSwitch = true,
                switchChecked = isSystemThemeChecked,
                onSwitchChange = { newValue ->
                    isSystemThemeChecked = newValue
                    // Logika untuk menyimpan preferensi tema di sini
                },
                onContainerColor = onContainerColor, // Teruskan warna
                secondaryTextColor = secondaryTextColor // Teruskan warna
            )
        }
    }
}
@Composable
fun SettingsThemeItem(
    title: String,
    subtitle: String? = null,
    showSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onContainerColor: androidx.compose.ui.graphics.Color, // Menerima warna dari luar
    secondaryTextColor: androidx.compose.ui.graphics.Color // Menerima warna dari luar
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding untuk seluruh baris item
        verticalAlignment = Alignment.CenterVertically // Pusatkan item secara vertikal
    ) {
        Column(
            modifier = Modifier.weight(1f) // Membuat kolom ini mengambil sebagian besar ruang yang tersedia
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(600),
                    color = onContainerColor
                )
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 13.sp, // Ukuran font lebih kecil untuk subtitle
                        fontWeight = FontWeight(400),
                        color = secondaryTextColor // Warna abu-abu untuk subtitle
                    )
                )
            }
        }

        if (showSwitch && onSwitchChange != null) {
            Switch(
                checked = switchChecked,
                onCheckedChange = onSwitchChange
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
