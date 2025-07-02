// path: app/src/main/java/com/example/sealnote/MainActivity.kt

package com.example.sealnote

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sealnote.data.UserPreferencesRepository // Import UserPreferencesRepository
import com.example.sealnote.data.ThemeOption // Pastikan ThemeOption diimpor
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.view.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject // <--- PASTIKAN INI DIIMPOR

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    // INJEKSI UserPreferencesRepository langsung di sini
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository // <--- PERBAIKAN DI SINI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // uncomment jika Anda ingin tampilan edge-to-edge

        setContent {
            // Gunakan instance yang sudah diinjeksi
            val themeOption by userPreferencesRepository.themeOption.collectAsStateWithLifecycle(
                initialValue = ThemeOption.SYSTEM // Berikan nilai awal yang valid
                // lifecycleOwner dan minActiveState sudah memiliki default yang baik
            )

            SealnoteTheme(themeOption = themeOption) {
                AppNavigation()
            }
        }
    }
}