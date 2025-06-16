package com.example.sealnote.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R
import com.example.sealnote.ui.theme.SealnoteTheme

// Warna berdasarkan UI yang diberikan
val ProfilePageBackgroundColor = Color(0xFF152332)
val ProfileNameTextColor = Color.White
val ProfileUsernameTextColor = Color(0xFFDBDBDB)
val ProfileLabelTextColor = Color.White
val ProfileInputBackgroundColor = Color(0xFF2A2E45)
val ProfileInputTextColor = Color(0xFFFFF3DB)
val ProfileButtonTextColor = Color.White
val ProfileButtonGradientStart = Color(0xFF8000FF)
val ProfileButtonGradientEnd = Color(0xFF00D1FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen( // Nama Composable: ProfileScreen
    onSignOutClick: () -> Unit,
    onBack: () -> Unit // Callback untuk tombol kembali
) {
    // State untuk TextField, bisa di-hoist jika diperlukan
    var email by remember { mutableStateOf("mingyusayang@gmail.com") }
    var password by remember { mutableStateOf("**********") }
    var nomor by remember { mutableStateOf("081234567890") }

    Scaffold(
        containerColor = ProfilePageBackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = ProfileNameTextColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ProfileNameTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ProfilePageBackgroundColor,
                    titleContentColor = ProfileNameTextColor,
                    navigationIconContentColor = ProfileNameTextColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_sealnote),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kim Mingyu",
                color = ProfileNameTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "@mingyusyng",
                color = ProfileUsernameTextColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileTextFieldItem(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileTextFieldItem(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                isPassword = true,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileTextFieldItem(
                label = "Nomor",
                value = nomor,
                onValueChange = { nomor = it },
                keyboardType = KeyboardType.Phone,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(70.dp))

            Button(
                onClick = onSignOutClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(154.dp)
                    .height(49.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ProfileButtonGradientStart, ProfileButtonGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Out",
                        color = ProfileButtonTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTextFieldItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    readOnly: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = label,
            color = ProfileLabelTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            textStyle = TextStyle(color = ProfileInputTextColor, fontSize = 14.sp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ProfileInputBackgroundColor,
                unfocusedContainerColor = ProfileInputBackgroundColor,
                disabledContainerColor = ProfileInputBackgroundColor,
                cursorColor = ProfileInputTextColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = ProfileInputTextColor,
                unfocusedTextColor = ProfileInputTextColor,
                disabledTextColor = ProfileInputTextColor
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            readOnly = readOnly
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF152332)
@Composable
fun ProfileScreenPreview() {
    SealnoteTheme {
        ProfileScreen(onSignOutClick = {}, onBack = {}) // Memberikan lambda kosong untuk preview
    }
}
