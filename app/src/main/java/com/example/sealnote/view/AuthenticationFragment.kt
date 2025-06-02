package com.example.sealnote.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face // Placeholder jika R.drawable.ic_face_id tidak ada
import androidx.compose.material.icons.filled.Fingerprint // Placeholder
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

// Definisi Warna dari XML
val AuthScreenBackground = Color(0xFF0A0F1E)
val AuthCardBackgroundColor = Color(0xFF10182C)
val AuthTextColor = Color.White

// Warna untuk Tab (berdasarkan asumsi dari @drawable/tab_selector, @drawable/tab_selected)
val AuthTabLayoutBackgroundColor = Color(0xFF0D1326) // Perkiraan untuk @drawable/tab_selector
val AuthSelectedTabBrush = Brush.horizontalGradient(listOf(Color(0xFF7B5DFF), Color(0xFF5D7FFF))) // Perkiraan untuk @drawable/tab_selected
val AuthUnselectedTabColor = Color.Transparent // Asumsi @drawable/tab_unselected adalah transparan atau bagian dari selector


@Composable
fun AuthenticationScreen(
    onUsePinClick: () -> Unit = {}
) {
    // State untuk tab yang terpilih (0: Fingerprint, 1: Face)
    // Default ke Face seperti di XML (tab_face memiliki background @drawable/tab_selected)
    var selectedTabIndex by remember { mutableStateOf(1) }

    val currentAuthMethod = if (selectedTabIndex == 0) AuthMethod.Fingerprint else AuthMethod.Face

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthScreenBackground
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
                colors = CardDefaults.cardColors(containerColor = AuthCardBackgroundColor)
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
                        onTabSelected = { index -> selectedTabIndex = index }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Unlock to open secret notes",
                        color = AuthTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Spacer dinamis untuk menempatkan ikon dengan benar
                    // Jarak dari atas tab_layout ke ikon adalah 68dp
                    // Jarak dari atas tab_layout ke title adalah 16dp
                    // Perkiraan tinggi Title Text + Spacer atasnya sekitar 16dp + 20sp ~ 36-40dp
                    // Jadi sisa jarak ke ikon adalah 68dp - (16dp + ~20dp [tinggi title]) ~ 32dp
                    Spacer(modifier = Modifier.height(32.dp))


                    Image(
                        painter = painterResource(id = currentAuthMethod.iconRes),
                        contentDescription = currentAuthMethod.contentDescription,
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp)) // Sesuai marginTop text_auth dari icon_auth

                    Text(
                        text = currentAuthMethod.authText,
                        color = AuthTextColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp)) // Sesuai marginTop "Use PIN" dari text_auth

                    Text(
                        text = "Use PIN",
                        color = AuthTextColor,
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
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Fingerprint", "Face")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(AuthTabLayoutBackgroundColor, shape = RoundedCornerShape(8.dp)) // Latar belakang dari @drawable/tab_selector
            .padding(4.dp) // Padding internal dari LinearLayout
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            val tabBackgroundModifier = if (isSelected) {
                Modifier.background(AuthSelectedTabBrush, shape = RoundedCornerShape(6.dp)) // Latar belakang @drawable/tab_selected
            } else {
                Modifier.background(AuthUnselectedTabColor, shape = RoundedCornerShape(6.dp)) // Latar belakang @drawable/tab_unselected
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
                    color = AuthTextColor, // textColor="@color/white" untuk kedua tab
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
        // Ganti dengan R.drawable.ic_fingerprint jika ada
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
private val R.drawable.ic_fingerprint_placeholder: Int
    get() = android.R.drawable.ic_partial_secure // Ini hanya contoh, ganti dengan ikon fingerprint Anda


@Preview(showBackground = true, backgroundColor = 0xFF0A0F1E)
@Composable
fun AuthenticationScreenPreview() {
    // Anda mungkin perlu menyediakan Theme Aplikasi Anda di sini jika Composable menggunakannya secara implisit
    // MaterialTheme { // Jika menggunakan komponen Material 3 secara ekstensif
    AuthenticationScreen()
    // }
}

@Preview
@Composable
fun AuthCustomTabLayoutPreview() {
    var selected by remember { mutableStateOf(0) }
    AuthCustomTabLayout(selectedTabIndex = selected, onTabSelected = { selected = it })
}