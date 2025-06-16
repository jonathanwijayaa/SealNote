package com.example.sealnote.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            TrashRoute(navController = navController)
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
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