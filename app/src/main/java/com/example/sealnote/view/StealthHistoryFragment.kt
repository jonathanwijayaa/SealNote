package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.sealnote.R // Jika menggunakan resource @color/background

// Definisi Warna dari XML
val HistoryScreenBackground = Color(0xFF152332)
val HistoryItemExpressionColor = Color(0xFF8090A6)
val HistoryItemResultColor = Color.White
val HistoryItemDividerColor = Color(0xFF2A2F3A)

// Data class untuk setiap entri riwayat
data class CalculationHistoryEntry(
    val id: String, // Untuk key jika menggunakan LazyColumn
    val expression: String,
    val result: String
)

@Composable
fun CalculationHistoryScreen(
    historyEntries: List<CalculationHistoryEntry> = emptyList()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = HistoryScreenBackground // Background utama dari root ConstraintLayout
    ) {
        // Box ini bertindak sebagai parent ConstraintLayout untuk memposisikan ScrollView
        Box(modifier = Modifier.fillMaxSize()) {
            Column( // Ini adalah Composable yang meniru ScrollView
                modifier = Modifier
                    .align(Alignment.TopCenter) // Memusatkan ScrollView secara horizontal, dan menempel ke atas sebelum offset
                    .offset(y = 159.dp)         // Menerapkan layout_marginTop="159dp"
                    .width(416.dp)              // Menerapkan layout_width="416dp"
                    .height(732.dp)             // Menerapkan layout_height="732dp"
                    .background(HistoryScreenBackground) // Background ScrollView dari XML
                    .verticalScroll(rememberScrollState()) // Membuat Column ini bisa di-scroll
            ) {
                // Column ini adalah LinearLayout vertikal di dalam ScrollView
                // fillMaxHeight() untuk meniru android:fillViewport="true"
                Column(modifier = Modifier.fillMaxHeight()) {
                    historyEntries.forEach { entry ->
                        HistoryItemView(entry = entry)
                    }
                    // Jika tidak ada item, fillMaxHeight akan membuat Column ini setinggi viewport
                    // Jika ada item dan lebih pendek, juga akan setinggi viewport
                    // Jika item lebih panjang, akan bisa di-scroll.
                }
            }
        }
    }
}

@Composable
private fun HistoryItemView(entry: CalculationHistoryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HistoryScreenBackground) // background="#152332" pada RelativeLayout
            .padding(vertical = 15.dp)    // paddingVertical="15dp" pada RelativeLayout
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp - 1.dp) // Kurangi 1dp untuk ruang divider di bawahnya
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Menempatkan teks di kanan atas dalam Box 60dp ini
                    .padding(end = 20.dp),    // layout_marginEnd="20dp" untuk kedua TextView
                horizontalAlignment = Alignment.End // Teks di dalam Column ini rata kanan
            ) {
                Text(
                    text = entry.expression,
                    color = HistoryItemExpressionColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
                // Menggunakan padding atas dengan nilai dp tetap
                Text(
                    text = entry.result,
                    color = HistoryItemResultColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    // Ganti dengan nilai dp yang sesuai secara visual, misal 4.dp atau 8.dp
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Divider(
            color = HistoryItemDividerColor,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth() // android:layout_width="match_parent"
        )
    }

}

@Preview(showBackground = true, widthDp = 416, heightDp = 891) // Mensimulasikan ukuran layar jika diperlukan
@Composable
fun CalculationHistoryScreenPreview() {
    val sampleHistory = listOf(
        CalculationHistoryEntry("id1", "25.000 - 5.000", "5.000"),
        CalculationHistoryEntry("id2", "50.000 - 15.000", "15.000"),
        CalculationHistoryEntry("id3", "100.000 - 32.500", "32.500"),
        CalculationHistoryEntry("id4", "75.000 - 10.000", "10.000"),
        CalculationHistoryEntry("id5", "150.000 - 45.000", "45.000"),
        CalculationHistoryEntry("id6", "30.000 - 7.500", "7.500"),
    )
    MaterialTheme { // Atau tema kustom aplikasi Anda
        CalculationHistoryScreen(historyEntries = sampleHistory)
    }
}