package com.example.sealnote.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.sealnote.R // Pastikan ini adalah path yang benar ke R class Anda

// --- START: Impor warna kustom dari Color.kt ---
import com.example.sealnote.ui.theme.ProfilePageBackgroundColor
import com.example.sealnote.ui.theme.ProfileNameTextColor
import com.example.sealnote.ui.theme.ProfileUsernameTextColor
import com.example.sealnote.ui.theme.ProfileLabelTextColor
import com.example.sealnote.ui.theme.ProfileInputBackgroundColor
import com.example.sealnote.ui.theme.ProfileInputTextColor
import com.example.sealnote.ui.theme.ProfileButtonTextColor
import com.example.sealnote.ui.theme.ProfileButtonGradientStart
import com.example.sealnote.ui.theme.ProfileButtonGradientEnd
// --- END: Impor warna kustom ---

@Composable
fun ProfileScreenComposable(
    onSignOutClick: () -> Unit
) {
    // State untuk TextField, bisa di-hoist jika diperlukan
    // Nilai awal diambil dari XML/gambar
    var email by remember { mutableStateOf("mingyusayang@gmail.com") }
    var password by remember { mutableStateOf("**********") }
    var nomor by remember { mutableStateOf("081234567890") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProfilePageBackgroundColor // Menggunakan warna dari Color.kt
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agar konten bisa di-scroll
                .padding(bottom = 32.dp), // Padding bawah agar tidak terlalu mepet
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_sealnote), // Ganti dengan drawable Anda
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape), // Bentuk lingkaran untuk gambar profil
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kim Mingyu",
                color = ProfileNameTextColor, // Menggunakan warna dari Color.kt
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "@mingyusyng",
                color = ProfileUsernameTextColor, // Menggunakan warna dari Color.kt
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileTextFieldItem(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email,
                readOnly = true // Berdasarkan gambar, field ini hanya untuk display
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileTextFieldItem(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                isPassword = true,
                readOnly = true // Berdasarkan gambar, field ini hanya untuk display
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileTextFieldItem(
                label = "Nomor",
                value = nomor,
                onValueChange = { nomor = it },
                keyboardType = KeyboardType.Phone,
                readOnly = true // Berdasarkan gambar, field ini hanya untuk display
            )

            Spacer(modifier = Modifier.height(70.dp))

            Button(
                onClick = onSignOutClick,
                shape = RoundedCornerShape(12.dp), // Sudut tombol
                modifier = Modifier
                    .width(154.dp)
                    .height(49.dp),
                contentPadding = PaddingValues(), // Hapus padding default agar gradien memenuhi tombol
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent) // Kontainer transparan untuk gradien
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ProfileButtonGradientStart, ProfileButtonGradientEnd) // Menggunakan warna dari Color.kt
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Out",
                        color = ProfileButtonTextColor, // Menggunakan warna dari Color.kt
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
    readOnly: Boolean = false // Tambahkan parameter readOnly
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp) // Margin horizontal untuk section ini
    ) {
        Text(
            text = label,
            color = ProfileLabelTextColor, // Menggunakan warna dari Color.kt
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
                .height(50.dp), // Tinggi TextField
            textStyle = TextStyle(color = ProfileInputTextColor, fontSize = 14.sp), // Menggunakan warna dari Color.kt
            shape = RoundedCornerShape(8.dp), // Bentuk field input
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ProfileInputBackgroundColor, // Menggunakan warna dari Color.kt
                unfocusedContainerColor = ProfileInputBackgroundColor, // Menggunakan warna dari Color.kt
                disabledContainerColor = ProfileInputBackgroundColor, // Menggunakan warna dari Color.kt
                cursorColor = ProfileInputTextColor, // Menggunakan warna dari Color.kt
                focusedIndicatorColor = Color.Transparent, // Sembunyikan garis bawah
                unfocusedIndicatorColor = Color.Transparent, // Sembunyikan garis bawah
                disabledIndicatorColor = Color.Transparent,  // Sembunyikan garis bawah jika disabled
                focusedTextColor = ProfileInputTextColor, // Menggunakan warna dari Color.kt
                unfocusedTextColor = ProfileInputTextColor, // Menggunakan warna dari Color.kt
                disabledTextColor = ProfileInputTextColor // Menggunakan warna dari Color.kt
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            readOnly = readOnly // Set TextField menjadi read-only jika true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF152332)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme { // Gunakan MaterialTheme untuk preview yang akurat
        ProfileScreenComposable(onSignOutClick = {})
    }
}