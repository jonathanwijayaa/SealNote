package com.example.sealnote.view // Atau package UI Anda, pastikan R.drawable dapat diakses

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

// Warna berdasarkan UI yang diberikan
val ProfilePageBackgroundColor = Color(0xFF152332)
val ProfileNameTextColor = Color.White
val ProfileUsernameTextColor = Color(0xFFDBDBDB)
val ProfileLabelTextColor = Color.White
val ProfileInputBackgroundColor = Color(0xFF2A2E45) // Perkiraan dari gambar field input
val ProfileInputTextColor = Color(0xFFFFF3DB)
val ProfileButtonTextColor = Color.White
val ProfileButtonGradientStart = Color(0xFF8000FF) // Ungu pekat untuk gradien tombol
val ProfileButtonGradientEnd = Color(0xFF00D1FF)   // Cyan/Biru untuk gradien tombol

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
        color = ProfilePageBackgroundColor
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
    readOnly: Boolean = false // Tambahkan parameter readOnly
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp) // Margin horizontal untuk section ini
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
                .height(50.dp), // Tinggi TextField
            textStyle = TextStyle(color = ProfileInputTextColor, fontSize = 14.sp),
            shape = RoundedCornerShape(8.dp), // Bentuk field input
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ProfileInputBackgroundColor,
                unfocusedContainerColor = ProfileInputBackgroundColor,
                disabledContainerColor = ProfileInputBackgroundColor, // Warna jika disabled
                cursorColor = ProfileInputTextColor,
                focusedIndicatorColor = Color.Transparent, // Sembunyikan garis bawah
                unfocusedIndicatorColor = Color.Transparent, // Sembunyikan garis bawah
                disabledIndicatorColor = Color.Transparent,  // Sembunyikan garis bawah jika disabled
                focusedTextColor = ProfileInputTextColor,
                unfocusedTextColor = ProfileInputTextColor,
                disabledTextColor = ProfileInputTextColor // Warna teks jika disabled
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            readOnly = readOnly // Set TextField menjadi read-only jika true
            // Padding internal untuk teks di dalam TextField (paddingHorizontal="16dp" di XML)
            // biasanya sudah diatur oleh default Material 3 TextField.
            // Jika teks terlalu mepet, bisa gunakan parameter `contentPadding` pada `decorationBox`
            // atau `BasicTextField` untuk kontrol penuh.
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