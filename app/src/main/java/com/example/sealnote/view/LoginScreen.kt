package com.example.sealnote.view

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import android.util.Log // <--- PASTIKAN INI DIIMPOR UNTUK LOGGING

// Definisi Warna

val LoginButtonGradientStart = Color(0xFF8000FF)
val LoginButtonGradientEnd = Color(0xFF00D1FF)
val GoogleButtonBackground = Color(0xFF3E5166)
val GoogleButtonTextColor = Color.White

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()

    // Deklarasi variabel isLoading di sini
    val isLoading = remember { mutableStateOf(false) }

    val loginResult by viewModel.loginResult.observeAsState()
    val isLoggedIn by viewModel.isLoggedIn.observeAsState()

    // Objek GoogleSignInClient
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher untuk Google Sign-In Activity
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading.value = false // Sembunyikan loading setelah hasil diterima dari Google Activity
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)

                // VVVVVV KODE YANG ANDA TANYAKAN DIMULAI DI SINI VVVVVV
                isLoading.value = true // Tampilkan loading lagi saat autentikasi Firebase dimulai
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { taskResult ->
                        isLoading.value = false // Sembunyikan loading setelah Firebase Auth selesai
                        if (taskResult.isSuccessful) {
                            Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                            Log.d("GoogleSignIn", "Firebase Auth with Google SUCCESS. User: ${firebaseAuth.currentUser?.email}") // <--- Pesan Log Sukses
                        } else {
                            // TANGKAP ERROR DENGAN LOG DI SINI
                            val errorMessage = taskResult.exception?.message ?: "Unknown Firebase authentication error."
                            Toast.makeText(context, "Firebase authentication with Google failed: $errorMessage", Toast.LENGTH_LONG).show()
                            Log.e("GoogleSignIn", "Firebase Auth with Google FAILED. Error: $errorMessage", taskResult.exception) // <--- Pesan Log Error
                        }
                    }
                // ^^^^^^ KODE YANG ANDA TANYAKAN BERAKHIR DI SINI ^^^^^^
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
                Log.e("GoogleSignIn", "Google sign in task failed (ApiException): ${e.statusCode}", e) // Log error ApiException
            } catch (e: Exception) {
                Toast.makeText(context, "An unexpected error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("GoogleSignIn", "An unexpected error occurred during Google sign in: ${e.message}", e) // Log error umum
            }
        } else {
            Toast.makeText(context, "Google Sign-In cancelled or failed", Toast.LENGTH_SHORT).show()
            Log.d("GoogleSignIn", "Google sign in activity result not OK. Result code: ${result.resultCode}")
        }
    }

    // Menangani hasil dari proses login email/kata sandi
    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is LoginViewModel.LoginResult.Success -> {
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
                isLoading.value = false // Sembunyikan loading
            }
            is LoginViewModel.LoginResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                isLoading.value = false // Sembunyikan loading
            }
            LoginViewModel.LoginResult.Loading -> {
                isLoading.value = true // Tampilkan loading
            }
            else -> {
                isLoading.value = false // Default ke false jika Idle atau state lain
            }
        }
    }

    // Menangani kasus jika pengguna sudah login (auto-login Firebase)
    LaunchedEffect(isLoggedIn, firebaseAuth.currentUser) {
        // Jika isLoggedIn dari ViewModel true, atau ada pengguna Firebase yang aktif
        if (isLoggedIn == true || firebaseAuth.currentUser != null) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo_sealnote),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(173.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Welcome Back!",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enter your login information to continue",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = {
                        Text(
                            "youremail@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "Email Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Password",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    placeholder = {
                        Text(
                            "password",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Password Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login(email, password)
                        }
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onForgotPasswordClick() }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login(email, password)
                    },
                    enabled = !isLoading.value, // Gunakan isLoading global
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(143.dp)
                        .height(39.dp)
                        .align(Alignment.End),
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(LoginButtonGradientStart, LoginButtonGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Google Sign-In Button
                Button(
                    onClick = {
                        isLoading.value = true // Tampilkan loading saat memulai Google Sign-In
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    enabled = !isLoading.value, // Gunakan isLoading global
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoogleButtonBackground,
                        contentColor = GoogleButtonTextColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoogleButtonTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                ClickableSignUpText(onSignUpClick)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (isLoading.value) { // Tampilkan indikator loading global
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ClickableSignUpText(onSignUpClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onBackground
        ).toSpanStyle()) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "SIGNUP", annotation = "signup")
        withStyle(
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            ).toSpanStyle()
        ) {
            append("Sign Up")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGNUP", start = offset, end = offset)
                .firstOrNull()?.let {
                    onSignUpClick()
                }
        },
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SealnoteTheme {
        LoginScreen(
            onLoginSuccess = {},
            onGoogleSignInClick = {},
            onForgotPasswordClick = {},
            onSignUpClick = {}
        )
    }
}