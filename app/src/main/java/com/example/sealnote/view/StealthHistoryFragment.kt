package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider // Menggunakan HorizontalDivider untuk mengganti Divider yang deprecated
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- START: Impor warna kustom dari Color.kt ---
import com.example.sealnote.ui.theme.HistoryScreenBackground
import com.example.sealnote.ui.theme.HistoryItemExpressionColor
import com.example.sealnote.ui.theme.HistoryItemResultColor
import com.example.sealnote.ui.theme.HistoryItemDividerColor
// --- END: Impor warna kustom ---

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
        color = HistoryScreenBackground // Menggunakan warna dari Color.kt
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column( // Ini adalah Composable yang meniru ScrollView
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 159.dp)
                    .width(416.dp)
                    .height(732.dp)
                    .background(HistoryScreenBackground) // Menggunakan warna dari Color.kt
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    historyEntries.forEach { entry ->
                        HistoryItemView(entry = entry)
                    }
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
            .background(HistoryScreenBackground) // Menggunakan warna dari Color.kt
            .padding(vertical = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp - 1.dp) // Kurangi 1dp untuk ruang divider di bawahnya
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = entry.expression,
                    color = HistoryItemExpressionColor, // Menggunakan warna dari Color.kt
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
                Text(
                    text = entry.result,
                    color = HistoryItemResultColor, // Menggunakan warna dari Color.kt
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        HorizontalDivider( // Menggunakan HorizontalDivider
            color = HistoryItemDividerColor, // Menggunakan warna dari Color.kt
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 416, heightDp = 891)
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
    MaterialTheme {
        CalculationHistoryScreen(historyEntries = sampleHistory)
    }
}