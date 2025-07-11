// path: app/src/main/java/com/example/sealnote/view/ProfileScreen.kt

package com.example.sealnote.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sealnote.R
import com.example.sealnote.model.User
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.viewmodel.ProfileUiState
import com.example.sealnote.viewmodel.ProfileViewModel
import com.example.sealnote.viewmodel.ProfileEvent // <--- Import ProfileEvent
import kotlinx.coroutines.launch
import android.util.Log // <--- Import Log untuk logging
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat

// --- Warna ---
val ProfilePageBackgroundColor = Color(0xFF152332)
val ProfileNameTextColor = Color.White
val ProfileLabelTextColor = Color.White
val ProfileInputBackgroundColor = Color(0xFF2A2E45)
val ProfileInputTextColor = Color(0xFFFFF3DB)
val ProfileButtonTextColor = Color.White
val ProfileButtonGradientStart = Color(0xFF8000FF)
val ProfileButtonGradientEnd = Color(0xFF00D1FF)

@Composable
fun ProfileRoute(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profileEvent by viewModel.profileEvent.collectAsState(initial = ProfileEvent.Idle)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val context = LocalContext.current
    val activity = LocalActivity.current as FragmentActivity

    // Menangani pesan error/info dari uiState
    LaunchedEffect(uiState.errorMessage, uiState.infoMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        uiState.infoMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        viewModel.clearMessages()
    }

    // Menangani event satu kali dari ProfileViewModel
    LaunchedEffect(profileEvent) {
        when (val event = profileEvent) {
            is ProfileEvent.ShowToast -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                Log.d("ProfileRoute", "ProfileEvent.ShowToast: ${event.message}")
            }
            is ProfileEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                Log.e("ProfileRoute", "ProfileEvent.Error: ${event.message}")
            }
            ProfileEvent.TogglePasswordVisibilitySuccess -> {
                Log.d("ProfileRoute", "ProfileEvent.TogglePasswordVisibilitySuccess detected.")
                // Panggil fungsi di ViewModel yang akan memicu toggle password visibility di UI state
                viewModel.onTogglePasswordVisibilityRequest(activity, context)
            }
            ProfileEvent.SignOutSuccess -> {
                Log.d("ProfileRoute", "ProfileEvent.SignOutSuccess detected. Navigating to login.")
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
                viewModel.onSignOutNavigated() // Reset status isSignedOut di ViewModel
            }
            else -> {
                // Tangani Idle atau Loading jika diperlukan
                Log.d("ProfileRoute", "Unhandled ProfileEvent: $event")
            }
        }
    }

    // VVVVVV KODE INI DIHAPUS UNTUK MENGHINDARI DUPLIKASI VVVVVV
    /*
    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            viewModel.onSignOutNavigated()
        }
    }
    */
    // ^^^^^^ KODE INI DIHAPUS UNTUK MENGHINDARI DUPLIKASI ^^^^^^

    ProfileScreen(
        uiState = uiState,
        currentRoute = currentRoute,
        onNameChange = viewModel::onNameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onEditToggle = viewModel::onEditToggle,
        onSaveChanges = viewModel::onSaveChanges,
        onTogglePasswordVisibility = {
            viewModel.onTogglePasswordVisibilityRequest(activity, context)
        },
        onSignOutClick = viewModel::signOut,
        onNavigate = { route ->
            if (currentRoute != route) {
                navController.navigate(route) { launchSingleTop = true }
            }
        },
        onNavigateToCalculator = {
            navController.navigate("stealthCalculator") {
                popUpTo("homepage") { inclusive = true }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    currentRoute: String?,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEditToggle: () -> Unit,
    onSaveChanges: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSignOutClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        "homepage" to ("All Notes" to Icons.Default.Home),
        "bookmarks" to ("Bookmarks" to Icons.Default.BookmarkBorder),
        "secretNotes" to ("Secret Notes" to Icons.Default.Lock),
        "trash" to ("Trash" to Icons.Default.Delete),
        "settings" to ("Settings" to Icons.Default.Settings),
        "profile" to ("Profile" to Icons.Default.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("SealNote Menu", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))
                menuItems.forEach { (route, details) ->
                    val (label, icon) = details
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = { scope.launch { drawerState.close() }; onNavigate(route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Calculate, "Back to Calculator") },
                    label = { Text("Back to Calculator") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onNavigateToCalculator() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Profile") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Open Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.user != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.logo_sealnote), contentDescription = "Profile Image", modifier = Modifier.size(100.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.user.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // --- Fields ---
                        ProfileTextFieldItem(label = "Full Name", value = uiState.editedName, onValueChange = onNameChange, readOnly = !uiState.isEditing)
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileTextFieldItem(label = "Email", value = uiState.user.email, onValueChange = {}, readOnly = true)
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileTextFieldItem(
                            label = if (uiState.isEditing) "New Password" else "Password",
                            value = uiState.editedPassword,
                            onValueChange = onPasswordChange,
                            readOnly = !uiState.isEditing,
                            isPassword = !uiState.isPasswordVisible,
                            onVisibilityToggle = if(uiState.isEditing && !uiState.isPasswordVisible) onTogglePasswordVisibility else null,
                            placeholder = if (!uiState.isEditing) "••••••••" else "Enter new password (optional)"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- Tombol ---
                        if (uiState.isEditing) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                            ) {
                                // Cancel Button
                                OutlinedButton(onClick = onEditToggle, modifier = Modifier.weight(1f).height(48.dp)) { Text("Cancel") }
                                // Save Button
                                GradientButton(text = "Save Changes", onClick = onSaveChanges, modifier = Modifier.weight(1f))
                            }
                        } else {
                            // Edit Profile Button (Outlined, Pipih, Lebar)
                            OutlinedButton(
                                onClick = onEditToggle,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.Gray)
                            ) {
                                Text("Edit Profile", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign Out Button (Gradient, Besar, Lebar)
                        GradientButton(
                            text = "Sign Out",
                            onClick = onSignOutClick,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    }
                }
            }
        }
    }
}

// Overload baru untuk ProfileTextFieldItem
@Composable
private fun ProfileTextFieldItem(
    label: String, value: String, onValueChange: (String) -> Unit, readOnly: Boolean,
    isPassword: Boolean = false, onVisibilityToggle: (() -> Unit)? = null, placeholder: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            placeholder = {
                if (placeholder != null) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (onVisibilityToggle != null) {
                    val icon = if(isPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                    }
                }
            }
        )
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(brush = Brush.horizontalGradient(listOf(ProfileButtonGradientStart, ProfileButtonGradientEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun showBiometricPrompt(activity: FragmentActivity, onSuccess: () -> Unit, onFailure: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                Toast.makeText(activity, errString, Toast.LENGTH_SHORT).show()
            }
            onFailure()
        }
    })
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authentication Required")
        .setSubtitle("Authenticate to show password")
        .setNegativeButtonText("Cancel")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .build()
    biometricPrompt.authenticate(promptInfo)
}
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SealnoteTheme {
        // Create a dummy ProfileUiState for preview purposes
        val previewUiState = ProfileUiState(
            user = User(
                fullName = "John Doe",
                email = "john.doe@example.com",
                passwordHash = "somehash",
            ),
            editedName = "John Doe",
            editedPassword = "", // Empty for preview
            isEditing = false,
            isPasswordVisible = false,
            isLoading = false,
            triggerBiometric = false,
            errorMessage = null,
            infoMessage = null,
            isSignedOut = false
        )
        ProfileScreen(
            uiState = previewUiState,
            currentRoute = "profile", // Set current route for selected drawer item
            onNameChange = {},
            onPasswordChange = {},
            onEditToggle = {},
            onSaveChanges = {},
            onTogglePasswordVisibility = {},
            onSignOutClick = {},
            onNavigate = {},
            onNavigateToCalculator = {}
        )
    }
}