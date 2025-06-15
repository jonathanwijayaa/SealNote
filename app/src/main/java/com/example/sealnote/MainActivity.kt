package com.example.sealnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sealnote.ui.theme.AppTheme
import com.example.sealnote.view.AppNavigation // Impor navigasi utama Compose Anda

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Setel tema aplikasi Anda
            AppTheme {
                // Panggil Composable Navigasi utama Anda
                AppNavigation()
            }
        }
    }
}
