package com.example.sealnote.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sealnote.viewmodel.BookmarksViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.MutableLiveData // Import untuk Preview ViewModel
import com.example.sealnote.viewmodel.StealthCalculatorViewModel // Import ViewModel StealthCalculator

/**
 * Mendefinisikan semua rute dan Composables untuk navigasi aplikasi.
 *
 * @param navController NavHostController untuk mengelola tumpukan belakang navigasi.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "stealthCalculator" // Ubah ini sesuai mode default Anda
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Stealth Mode ---
        composable("stealthCalculator") {
            // Dapatkan instance ViewModel untuk StealthCalculatorScreen
            val stealthCalculatorViewModel: StealthCalculatorViewModel = viewModel()
            StealthCalculatorScreen(
                // Meneruskan lambda untuk navigasi ke LoginScreen
                onNavigateToLogin = {
                    navController.navigate("login") {
                        // Opsi popUpTo untuk membersihkan back stack dan mencegah kembali ke kalkulator
                        popUpTo("stealthCalculator") { inclusive = true }
                    }
                },
                viewModel = stealthCalculatorViewModel // Meneruskan ViewModel ke Composable
            )
        }
        composable("stealthHistory") {
            StealthHistoryScreen()
        }
        composable("stealthScientific") {
            StealthScientificScreen()
        }

        // --- SealNote Login & Signup ---
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    // TODO: Implementasi logika login
                    navController.navigate("homepage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleSignInClick = {
                    // TODO: Implementasi login Google
                    navController.navigate("homepage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    // TODO: Implementasi lupa password
                    // Anda mungkin ingin menavigasi ke layar khusus Lupa Password
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignUpClick = { email, password ->
                    // TODO: Implementasi logika sign up
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // --- Note Mode ---
        composable("homepage") {
            HomepageScreen(
                onNavigateToAddNote = { navController.navigate("addNotes") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToBookmarks = { navController.navigate("bookmarks") },
                onNavigateToSecretNotes = { navController.navigate("secretNotesLocked") },
                onNavigateToTrash = { navController.navigate("trash") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("profile") {
            ProfileScreen(
                onSignOutClick = {
                    // TODO: Implementasi logika sign out
                    navController.navigate("login") {
                        popUpTo("homepage") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("bookmarks") {
            val bookmarksViewModel: BookmarksViewModel = viewModel()
            BookmarksScreen(
                bookmarksViewModel = bookmarksViewModel,
                onNavigateToAddNote = { navController.navigate("addNotes") },
                onNavigateTo = { destination ->
                    when (destination) {
                        "all_notes" -> navController.navigate("homepage") {
                            popUpTo("homepage") {
                                inclusive = true
                            }
                        }

                        "secret_notes" -> navController.navigate("secretNotesLocked") {
                            popUpTo("homepage") {
                                inclusive = true
                            }
                        }

                        "trash" -> navController.navigate("trash") {
                            popUpTo("homepage") {
                                inclusive = true
                            }
                        }

                        "settings" -> navController.navigate("settings") {
                            popUpTo("homepage") {
                                inclusive = true
                            }
                        }

                        "profile" -> navController.navigate("profile") {
                            popUpTo("homepage") {
                                inclusive = true
                            }
                        }

                        else -> { /* Do nothing or handle unknown destination */
                        }
                    }
                },
                bookmarkedNotes = emptyList(), // FIX: Sesuaikan dengan data asli Anda, ini hanya placeholder
                searchQuery = "",            // FIX: Sesuaikan dengan data asli Anda, ini hanya placeholder
                onSearchQueryChange = {}     // FIX: Sesuaikan dengan data asli Anda, ini hanya placeholder
            )
        }
        composable("secretNotes") {
            SecretNotesScreen(
                onFabClick = { navController.navigate("addNotes") },
                onNoteClick = { /* Handle note click */ },
                onSortClick = { /* Handle sort click */ }
            )
        }
        composable("secretNotesLocked") {
            SecretNotesLockedScreen(
                onAuthenticate = {
                    // Jika autentikasi berhasil, navigasi ke authentication
                    navController.navigate("authentication")
                }
            )
        }
        composable("authentication") {
            AuthenticationScreen(
                onUsePinClick = {
                    // Navigasi ke login jika pengguna memilih PIN
                    navController.navigate("login") {
                        popUpTo("authentication") { inclusive = true }
                    }
                },
                onAuthSuccess = {
                    // Setelah autentikasi berhasil, kembali ke secretNotes
                    navController.navigate("secretNotes") {
                        popUpTo("authentication") { inclusive = true }
                    }
                }
            )
        }
        composable("trash") {
            TrashScreen()
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("addNotes") {
            AddNotesScreen(
                onBack = { navController.popBackStack() },
                onSave = { title, notes ->
                    // TODO: Implementasi logika simpan catatan (misalnya memanggil ViewModel)
                    navController.popBackStack() // Kembali setelah menyimpan
                }
            )
        }
    }
}