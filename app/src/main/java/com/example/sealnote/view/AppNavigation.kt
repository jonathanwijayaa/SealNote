package com.example.sealnote.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
// import androidx.lifecycle.MutableLiveData // Tidak digunakan di sini, bisa dihapus

import com.example.sealnote.viewmodel.BookmarksViewModel
import androidx.navigation.navArgument
import com.example.sealnote.viewmodel.CalculatorHistoryViewModel
import com.example.sealnote.viewmodel.StealthCalculatorViewModel
import com.example.sealnote.viewmodel.StealthScientificViewModel

/**
 * Mendefinisikan semua rute dan Composables untuk navigasi aplikasi.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "stealthCalculator"
) {
    val calculatorHistoryViewModel: CalculatorHistoryViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Stealth Mode ---
        composable("stealthCalculator") {
            val stealthCalculatorViewModel: StealthCalculatorViewModel = hiltViewModel()
            StealthCalculatorScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("stealthCalculator") { inclusive = true }
                    }
                },
                navController = navController,
                viewModel = stealthCalculatorViewModel,
                historyViewModel = calculatorHistoryViewModel
            )
        }
        composable("stealthScientific") {
            val stealthScientificViewModel: StealthScientificViewModel = hiltViewModel()
            StealthScientificScreen(
                navController = navController,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("stealthScientific") { inclusive = true }
                    }
                },
                viewModel = stealthScientificViewModel,
                historyViewModel = calculatorHistoryViewModel
            )
        }
        composable("stealthHistory") {
            StealthHistoryScreen(
                navController = navController,
                historyViewModel = calculatorHistoryViewModel
            )
        }

        // --- SealNote Login & Signup ---
        composable("login") {
            LoginScreen(
                // Ganti 'onLoginClick' menjadi 'onLoginSuccess'
                onLoginSuccess = {
                    // Logika navigasi setelah login berhasil tetap sama
                    navController.navigate("homepage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleSignInClick = {
                    navController.navigate("homepage") { popUpTo("login") { inclusive = true } }
                },
                onForgotPasswordClick = {},
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            // Memanggil SignUpScreen versi BARU yang sudah terhubung ke ViewModel
            SignUpScreen(
                // Parameter 'onSignUpClick' sudah tidak ada lagi.
                // Sebagai gantinya, 'onSignUpSuccess' akan dipanggil secara otomatis
                // dari dalam SignUpScreen setelah ViewModel melaporkan proses berhasil.
                onSignUpSuccess = {
                    // Logika navigasi tetap sama: arahkan ke halaman login.
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },

                // Parameter ini tidak berubah, logikanya tetap sama.
                onGoogleSignInClick = {
                    // Setelah sign-in dengan Google, mungkin langsung ke homepage
                    navController.navigate("homepage") { popUpTo("signup") { inclusive = true } }
                },

                // Parameter ini juga tidak berubah.
                onLoginClick = {
                    // Jika pengguna sudah punya akun, kembali ke halaman login
                    navController.navigate("login") { popUpTo("signup") { inclusive = true } }
                }
            )
        }

        // --- Note Mode ---
        composable("homepage") {
            // Perbarui panggilan HomepageScreen untuk menyertakan parameter drawer
            HomepageScreen(
                onNavigateToAddNote = { navController.navigate("addNotes") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToBookmarks = {
                    navController.navigate("bookmarks") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSecretNotes = {
                    navController.navigate("secretNotesLocked") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToTrash = {
                    navController.navigate("trash") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
            )
            HomepageRoute(navController = navController)
        }

        composable("profile") {
            ProfileScreen(
                onSignOutClick = {
                    navController.navigate("login") { popUpTo("homepage") { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("bookmarks") {
            val bookmarksViewModel: BookmarksViewModel = viewModel()
            // Perbarui panggilan BookmarksScreen untuk menyertakan parameter drawer
            BookmarksScreen(
                bookmarksViewModel = bookmarksViewModel,
                onNavigateToAddNote = { navController.navigate("addNotes") },
                // Hapus `onNavigateTo` dan ganti dengan parameter spesifik
                onNavigateHomepage = {
                    navController.navigate("homepage") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSecretNotes = {
                    navController.navigate("secretNotesLocked") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToTrash = {
                    navController.navigate("trash") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToBookmarks = { /* Sudah di Bookmarks, tidak perlu navigasi ulang */ },
                bookmarkedNotes = emptyList(), // Sediakan data yang sebenarnya
                searchQuery = "", // Sediakan data yang sebenarnya
                onSearchQueryChange = {} // Sediakan fungsi yang sebenarnya
            // Panggil BookmarksRoute dengan parameter yang benar sesuai definisinya
            BookmarksRoute(
                onNavigateToAddNote = {
                    // Arahkan ke layar tambah/edit dengan ID null untuk catatan baru
                    navController.navigate("add_edit_note_screen/null")
                },
                onNavigateTo = { destination ->
                    // Logika untuk navigasi dari drawer
                    val route = when (destination) {
                        "all_notes" -> "homepage"
                        "secret_notes" -> "secretNotesLocked"
                        else -> destination
                    }
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("secretNotes") {
            SecretNotesScreen(
                onFabClick = { navController.navigate("addNotes") },
                onNoteClick = { /* Handle note click */ },
                onSortClick = { /* Handle sort click */ },
                onNavigateHomepage = {
                    navController.navigate("homepage") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToAddNote = { navController.navigate("addNotes") },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToBookmarks = {
                    navController.navigate("bookmarks") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSecretNotes = { /* Sudah di secretNotes, tidak perlu navigasi ulang */ },
                onNavigateToTrash = {
                    navController.navigate("trash") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                notes = emptyList() // Pastikan parameter ini juga disediakan
                onFabClick = { navController.navigate("add_edit_note_screen/null") },
                onNoteClick = { noteId ->
                    navController.navigate("add_edit_note_screen/$noteId")
                },
                onSortClick = { /* Handle sort click */ }
            )
        }
        composable("secretNotesLocked") {
            SecretNotesLockedScreen(
                onAuthenticate = { navController.navigate("authentication") }
            )
        }
        composable("authentication") {
            AuthenticationScreen(
                onUsePinClick = {
                    navController.navigate("login") { popUpTo("authentication") { inclusive = true } }
                },
                onAuthSuccess = {
                    navController.navigate("secretNotes") { popUpTo("authentication") { inclusive = true } }
                }
            )
        }

        // HANYA ADA SATU BLOK INI
        composable("trash") {
            TrashScreen(
                deletedNotes = emptyList(), // Pastikan parameter ini disediakan
                onRestoreNote = { /* TODO */ },
                onPermanentlyDeleteNote = { /* TODO */ },
                onNavigateHomepage = {
                    navController.navigate("homepage") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToAddNote = { navController.navigate("addNotes") },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToBookmarks = {
                    navController.navigate("bookmarks") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToSecretNotes = {
                    navController.navigate("secretNotesLocked") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onNavigateToTrash = { /* Sudah di Trash, tidak perlu navigasi ulang */ },
                onNavigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("homepage") { inclusive = true }
                    }
                }
            )
            TrashRoute(navController = navController)
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
                // Jika SettingsScreen juga menggunakan drawer, tambahkan parameter navigasi di sini
                // onNavigateHomepage = { /* ... */ },
                // ... dan lainnya
            )
        }

        composable(
            route = "add_edit_note_screen/{noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            AddEditNoteRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}