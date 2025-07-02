// path: app/src/main/java/com/example/sealnote/data/UserPreferencesRepository.kt

package com.example.sealnote.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Definisikan enum ThemeOption jika belum ada di `data` package
enum class ThemeOption {
    LIGHT, DARK, SYSTEM
}

// DataStore instance
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context // Gunakan @ApplicationContext untuk injeksi context
) {
    private object PreferencesKeys {
        val THEME_OPTION = stringPreferencesKey("theme_option")
    }

    // Aliran pilihan tema dari DataStore
    val themeOption: Flow<ThemeOption> = context.userPreferencesDataStore.data
        .map { preferences ->
            // Baca string dari DataStore, default ke SYSTEM jika tidak ada atau tidak valid
            preferences[PreferencesKeys.THEME_OPTION]?.let { themeString ->
                try {
                    ThemeOption.valueOf(themeString)
                } catch (e: IllegalArgumentException) {
                    ThemeOption.SYSTEM // Fallback jika string tidak valid
                }
            } ?: ThemeOption.SYSTEM
        }

    // Fungsi untuk menyimpan pilihan tema
    suspend fun saveThemeOption(themeOption: ThemeOption) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_OPTION] = themeOption.name
        }
    }
}