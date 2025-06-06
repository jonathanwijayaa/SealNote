package com.example.sealnote.view // Atau package UI Anda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email // Contoh, jika ic_email adalah ikon standar
import androidx.compose.material.icons.filled.Lock   // Contoh, jika ic_lock adalah ikon standar
// Jika ic_email, ic_lock, ic_google adalah custom drawables, gunakan painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R // Pastikan path ini benar

// Definisikan Warna (sesuaikan dengan @color/background dan nilai hex dari XML)
val LoginScreenBackground = Color(0xFF152332) // Asumsi dari @color/background atau tema sebelumnya
val LoginWelcomeTextColor = Color(0xFFFDFDFD)
val LoginInfoTextColor = Color(0xFFEDEDED)
val LoginInputFieldBackground = Color(0xFF2A2E45) // Mirip dengan ProfileInputBackgroundColor
val LoginInputTextHintColor = Color(0xFFBBBBBB) // Warna hint yang lebih lembut
val LoginInputTextColor = Color.White
val LoginButtonGradientStart = Color(0xFF8000FF) // Gradien dari Profile Page
val LoginButtonGradientEnd = Color(0xFF00D1FF)
val LoginButtonTextColor = Color.White
val ForgotPasswordTextColor = Color(0xFF2493D7)
val NoAccountTextColor = Color(0xFFF8F8F8)
val SignUpLinkColor = Color(0xFFEDEDED) // Warna untuk link "Sign Up"
val GoogleButtonBackground = Color(0xFF3E5166)
val GoogleButtonTextColor = Color.White

@Composable
fun LoginScreenComposable(
    onLoginClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LoginScreenBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp), // Padding horizontal umum
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_sealnote),
                contentDescription = "App Logo",
                modifier = Modifier.size(173.dp),
                contentScale = ContentScale.Fit // XML menggunakan fitXY, Fit lebih umum untuk logo
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome Back!",
                color = LoginWelcomeTextColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start) // Sesuai bias kiri di XML
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter your login information to continue",
                color = LoginInfoTextColor,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start) // Sesuai bias kiri di XML
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email", color = LoginInputTextHintColor) },
                placeholder = { Text("youremail@gmail.com", color = LoginInputTextHintColor) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email), // Ganti dengan resource Anda
                        contentDescription = "Email Icon",
                        tint = LoginInputTextHintColor // Sesuaikan tint ikon
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(color = LoginInputTextColor, fontSize = 14.sp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LoginInputFieldBackground,
                    unfocusedContainerColor = LoginInputFieldBackground,
                    disabledContainerColor = LoginInputFieldBackground,
                    cursorColor = LoginInputTextColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(16.dp)) // Jarak antar input field

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password", color = LoginInputTextHintColor) },
                placeholder = { Text("password", color = LoginInputTextHintColor) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock), // Ganti dengan resource Anda
                        contentDescription = "Password Icon",
                        tint = LoginInputTextHintColor // Sesuaikan tint ikon
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
                        onLoginClick(email, password)
                    }
                ),
                textStyle = TextStyle(color = LoginInputTextColor, fontSize = 14.sp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LoginInputFieldBackground,
                    unfocusedContainerColor = LoginInputFieldBackground,
                    disabledContainerColor = LoginInputFieldBackground,
                    cursorColor = LoginInputTextColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forgot Password?",
                color = ForgotPasswordTextColor,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.End) // Sesuai bias kanan di XML
                    .clickable { onForgotPasswordClick() }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLoginClick(email, password)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(143.dp) // Sesuai XML
                    .height(39.dp)  // Sesuai XML
                    .align(Alignment.End), // Sesuai bias kanan di XML
                contentPadding = PaddingValues(), // Hapus padding default
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
                        color = LoginButtonTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Google Sign-In Button
            Button(
                onClick = onGoogleSignInClick,
                shape = RoundedCornerShape(8.dp), // Sesuai rounded_button_background
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoogleButtonBackground,
                    contentColor = GoogleButtonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f) // XML 243dp, ini membuatnya responsif
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google), // Ganti dengan resource Anda
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        color = GoogleButtonTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp)) // Jarak sebelum sign up text, bisa disesuaikan (XML 80dp)

            ClickableSignUpText(onSignUpClick)

            Spacer(modifier = Modifier.height(32.dp)) // Padding bawah
        }
    }
}

@Composable
private fun ClickableSignUpText(onSignUpClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = NoAccountTextColor, fontSize = 13.sp)) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "SIGNUP", annotation = "signup")
        withStyle(
            style = SpanStyle(
                color = SignUpLinkColor, // Dari XML textColor="#EDEDED"
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline // Opsional: untuk menandakan link
            )
        ) {
            append("Sign Up")
        }
        pop()
    }

    androidx.compose.foundation.text.ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGNUP", start = offset, end = offset)
                .firstOrNull()?.let {
                    onSignUpClick()
                }
        },
        modifier = Modifier.padding(top = 16.dp) // Sesuaikan dengan margin XML
    )
}


@Preview(showBackground = true, backgroundColor = 0xFF152332)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenComposable(
            onLoginClick = { email, password ->
                // Tidak perlu melakukan apa-apa di preview,
                // atau Anda bisa menambahkan log jika mau.
                // Contoh: println("Preview Login Clicked: $email, $password")
            },
            onGoogleSignInClick = {},
            onForgotPasswordClick = {},
            onSignUpClick = {}
        )
    }
}