// path: app/src/main/java/com/example/sealnote/MainActivity.kt

package com.example.sealnote

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sealnote.data.UserPreferencesRepository
import com.example.sealnote.data.ThemeOption
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.view.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth // Inject FirebaseAuth untuk memeriksa status login

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tentukan startDestination secara dinamis
        val startDestination = if (firebaseAuth.currentUser != null) {
            "homepage" // Jika sudah login, langsung ke homepage
        } else {
            "stealthCalculator"    // Jika belum login, ke halaman login
        }

        setContent {
            val themeOption by userPreferencesRepository.themeOption.collectAsStateWithLifecycle(
                initialValue = ThemeOption.SYSTEM
            )

            SealnoteTheme(themeOption = themeOption) {
                AppNavigation(startDestination = startDestination) // Teruskan startDestination
            }
        }
    }
}