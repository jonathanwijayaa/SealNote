package com.example.sealnote.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// Tidak perlu import Face dan Fingerprint jika menggunakan drawable khusus
// import androidx.compose.material.icons.filled.Face
// import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.R // Pastikan ini adalah path yang benar ke R class Anda

// --- START: Impor warna kustom dari Color.kt ---
import com.example.sealnote.ui.theme.AuthScreenBackground
import com.example.sealnote.ui.theme.AuthCardBackgroundColor
import com.example.sealnote.ui.theme.AuthTextColor
import com.example.sealnote.ui.theme.AuthTabLayoutBackgroundColor
import com.example.sealnote.ui.theme.AuthSelectedTabBrushStart
import com.example.sealnote.ui.theme.AuthSelectedTabBrushEnd
import com.example.sealnote.ui.theme.AuthUnselectedTabColor
// --- END: Impor warna kustom ---

@Composable
fun AuthenticationScreen(
    onUsePinClick: () -> Unit = {}
) {
    // State untuk tab yang terpilih (0: Fingerprint, 1: Face)
    var selectedTabIndex by remember { mutableStateOf(1) }

    val currentAuthMethod = if (selectedTabIndex == 0) AuthMethod.Fingerprint else AuthMethod.Face

    // Buat Brush di sini menggunakan warna dari Color.kt
    val AuthSelectedTabBrush = Brush.horizontalGradient(
        listOf(AuthSelectedTabBrushStart, AuthSelectedTabBrushEnd)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthScreenBackground // Menggunakan warna dari Color.kt
    ) {
        Box(
            contentAlignment = Alignment.Center, // Memusatkan CardView
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .width(332.dp)
                    .height(305.dp), // Tinggi tetap seperti di XML
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuthCardBackgroundColor) // Menggunakan warna dari Color.kt
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize() // Mengisi Card
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Custom Tab Layout
                    AuthCustomTabLayout(
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { index -> selectedTabIndex = index },
                        authSelectedTabBrush = AuthSelectedTabBrush // Meneruskan Brush yang dibuat
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Unlock to open secret notes",
                        color = AuthTextColor, // Menggunakan warna dari Color.kt
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp)) // Jarak yang dihitung sebelumnya

                    Image(
                        painter = painterResource(id = currentAuthMethod.iconRes),
                        contentDescription = currentAuthMethod.contentDescription,
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp)) // Sesuai marginTop text_auth dari icon_auth

                    Text(
                        text = currentAuthMethod.authText,
                        color = AuthTextColor, // Menggunakan warna dari Color.kt
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp)) // Sesuai marginTop "Use PIN" dari text_auth

                    Text(
                        text = "Use PIN",
                        color = AuthTextColor, // Menggunakan warna dari Color.kt
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp)) // Memberikan bentuk pada area klik
                            .clickable { onUsePinClick() }
                            .padding(8.dp) // Padding seperti di XML
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthCustomTabLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    authSelectedTabBrush: Brush // Menerima Brush sebagai parameter
) {
    val tabs = listOf("Fingerprint", "Face")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(AuthTabLayoutBackgroundColor, shape = RoundedCornerShape(8.dp)) // Menggunakan warna dari Color.kt
            .padding(4.dp) // Padding internal dari LinearLayout
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            val tabBackgroundModifier = if (isSelected) {
                Modifier.background(authSelectedTabBrush, shape = RoundedCornerShape(6.dp)) // Menggunakan Brush yang diterima
            } else {
                Modifier.background(AuthUnselectedTabColor, shape = RoundedCornerShape(6.dp)) // Menggunakan warna dari Color.kt
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(tabBackgroundModifier)
                    .clip(RoundedCornerShape(6.dp)) // Untuk efek klik pada area yang benar
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = AuthTextColor, // Menggunakan warna dari Color.kt
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Enum untuk merepresentasikan metode autentikasi dan data terkaitnya
private enum class AuthMethod(
    val iconRes: Int,
    val authText: String,
    val contentDescription: String
) {
    Fingerprint(
        iconRes = R.drawable.ic_finger, // Anda perlu drawable ini
        authText = "Place your finger on the sensor.",
        contentDescription = "Fingerprint Icon"
    ),
    Face(
        iconRes = R.drawable.ic_face_id, // Pastikan drawable ini ada
        authText = "We need to detect your face.",
        contentDescription = "Face Icon"
    )
}

// Placeholder untuk R.drawable.ic_fingerprint_placeholder jika belum ada
// Anda harus membuat drawable ini atau menggantinya dengan yang valid.
// Contoh menggunakan Material Icon sebagai fallback, tapi idealnya gunakan drawable dari proyek Anda.
// private val R.drawable.ic_fingerprint_placeholder: Int // Baris ini tidak perlu jika ic_finger sudah didefinisikan
//    get() = android.R.drawable.ic_partial_secure // Ini hanya contoh, ganti dengan ikon fingerprint Anda


@Preview(showBackground = true, backgroundColor = 0xFF0A0F1E)
@Composable
fun AuthenticationScreenPreview() {
    MaterialTheme { // Menggunakan MaterialTheme agar preview bekerja dengan baik
        AuthenticationScreen()
    }
}

@Preview
@Composable
fun AuthCustomTabLayoutPreview() {
    var selected by remember { mutableStateOf(0) }
    // Membuat Brush untuk preview, sama dengan di AuthenticationScreen
    val previewAuthSelectedTabBrush = Brush.horizontalGradient(
        listOf(AuthSelectedTabBrushStart, AuthSelectedTabBrushEnd)
    )
    AuthCustomTabLayout(selectedTabIndex = selected, onTabSelected = { selected = it }, authSelectedTabBrush = previewAuthSelectedTabBrush)
}