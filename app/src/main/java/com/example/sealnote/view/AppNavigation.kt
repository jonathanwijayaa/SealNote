package com.example.sealnote.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sealnote.viewmodel.BookmarksViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.MutableLiveData
import com.example.sealnote.viewmodel.StealthCalculatorViewModel
import com.example.sealnote.viewmodel.StealthScientificViewModel
import com.example.sealnote.viewmodel.CalculatorHistoryViewModel

/**
 * Mendefinisikan semua rute dan Composables untuk navigasi aplikasi.
 *
 * @param navController NavHostController untuk mengelola tumpukan belakang navigasi.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "stealthCalculator"
) {
    val calculatorHistoryViewModel: CalculatorHistoryViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Stealth Mode ---
        composable("stealthCalculator") {
            val stealthCalculatorViewModel: StealthCalculatorViewModel = viewModel()
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
            val stealthScientificViewModel: StealthScientificViewModel = viewModel()
            StealthScientificScreen(
                navController = navController,
                onNavigateToLogin = { // Teruskan callback onNavigateToLogin
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
                onLoginClick = { email, password ->
                    navController.navigate("homepage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleSignInClick = {
                    navController.navigate("homepage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignUpClick = { email, password ->
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
                bookmarkedNotes = emptyList(),
                searchQuery = "",
                onSearchQueryChange = {}
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
                    navController.navigate("authentication")
                }
            )
        }
        composable("authentication") {
            AuthenticationScreen(
                onUsePinClick = {
                    navController.navigate("login") {
                        popUpTo("authentication") { inclusive = true }
                    }
                },
                onAuthSuccess = {
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
                    navController.popBackStack()
                }
            )
        }
    }
}