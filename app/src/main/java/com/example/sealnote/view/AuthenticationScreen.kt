package com.example.sealnote.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
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
import com.example.sealnote.R
import com.example.sealnote.ui.theme.AppTheme // Asumsi AppTheme Anda

// Definisi Warna dari XML
val AuthScreenBackground = Color(0xFF0A0F1E)
val AuthCardBackgroundColor = Color(0xFF10182C)
val AuthTextColor = Color.White

// Warna untuk Tab (berdasarkan asumsi dari @drawable/tab_selector, @drawable/tab_selected)
val AuthTabLayoutBackgroundColor = Color(0xFF0D1326)
val AuthSelectedTabBrush = Brush.horizontalGradient(listOf(Color(0xFF7B5DFF), Color(0xFF5D7FFF)))
val AuthUnselectedTabColor = Color.Transparent


@Composable
fun AuthenticationScreen(
    onUsePinClick: () -> Unit,
    onAuthSuccess: () -> Unit // Callback untuk berhasil autentikasi
) {
    // State untuk tab yang terpilih (0: Fingerprint, 1: Face)
    var selectedTabIndex by remember { mutableStateOf(1) } // Default ke Face

    val currentAuthMethod = if (selectedTabIndex == 0) AuthMethod.Fingerprint else AuthMethod.Face

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthScreenBackground
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .width(332.dp)
                    .height(305.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AuthCardBackgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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

                    Spacer(modifier = Modifier.height(32.dp))


                    Image(
                        painter = painterResource(id = currentAuthMethod.iconRes),
                        contentDescription = currentAuthMethod.contentDescription,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                // Contoh: Trigger autentikasi saat ikon diklik (untuk demo)
                                onAuthSuccess()
                            }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = currentAuthMethod.authText,
                        color = AuthTextColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "Use PIN",
                        color = AuthTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onUsePinClick() }
                            .padding(8.dp)
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
            .background(AuthTabLayoutBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            val tabBackgroundModifier = if (isSelected) {
                Modifier.background(AuthSelectedTabBrush, shape = RoundedCornerShape(6.dp))
            } else {
                Modifier.background(AuthUnselectedTabColor, shape = RoundedCornerShape(6.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(tabBackgroundModifier)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = AuthTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private enum class AuthMethod(
    val iconRes: Int,
    val authText: String,
    val contentDescription: String
) {
    Fingerprint(
        iconRes = R.drawable.ic_finger,
        authText = "Place your finger on the sensor.",
        contentDescription = "Fingerprint Icon"
    ),
    Face(
        iconRes = R.drawable.ic_face_id,
        authText = "We need to detect your face.",
        contentDescription = "Face Icon"
    )
}

// Placeholder untuk R.drawable.ic_fingerprint_placeholder jika belum ada
// Ini hanya contoh, ganti dengan ikon fingerprint Anda
private val R.drawable.ic_fingerprint_placeholder: Int
    get() = android.R.drawable.ic_partial_secure


@Preview(showBackground = true, backgroundColor = 0xFF0A0F1E)
@Composable
fun AuthenticationScreenPreview() {
    AppTheme {
        // Berikan lambda kosong untuk callbacks di preview
        AuthenticationScreen(onUsePinClick = {}, onAuthSuccess = {})
    }
}

@Preview
@Composable
fun AuthCustomTabLayoutPreview() {
    var selected by remember { mutableStateOf(0) }
    AuthCustomTabLayout(selectedTabIndex = selected, onTabSelected = { selected = it })
}
