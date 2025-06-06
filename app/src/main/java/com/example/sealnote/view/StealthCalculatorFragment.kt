package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // Diubah dari CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace // Ikon untuk backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.sealnote.R // Jika warna dari resource

// Definisi Warna (disesuaikan agar lebih cocok dengan gambar)
val CalcScreenBackground = Color(0xFF152332)
val CalcDisplayCardBackground = Color(0xFF152332)
val CalcDisplayTextColor = Color.White
val CalcDisplayDividerColor = Color(0x33FFFFFF) // Alpha 20% untuk #33FFFFFF

// Warna Tombol Disederhanakan (berdasarkan gambar)
val CalcNonOperatorButtonBg = Color(0xFF2A2D57) // Warna ungu gelap solid untuk angka dan fungsi
val CalcOperatorGradient = Brush.horizontalGradient(
    // Gradien dari gambar tampak sedikit berbeda, sesuaikan jika perlu
    listOf(Color(0xFF5C33A1), Color(0xFF3E71D6)) // Contoh ungu ke biru
)
val CalcButtonTextColor = Color.White
val CalcButtonIconColor = Color.White

// Data class untuk merepresentasikan setiap tombol kalkulator
private data class CalcButtonInfo(
    val symbol: String,
    val type: ButtonType,
    val textSize: TextUnit,
    val description: String,
    val icon: ImageVector? = null // Tambahkan field untuk ikon opsional
)

// Enum untuk tipe tombol
private enum class ButtonType {
    NUMBER, OPERATOR, FUNCTION, CLEAR, DECIMAL, BACKSPACE
}

@Composable
fun CalculatorScreenModified() {
    var displayText by remember { mutableStateOf("5.000") } // Contoh dari gambar

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CalcScreenBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CalculatorDisplay( // Tidak banyak berubah, mungkin padding
                displayText = displayText,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Sesuaikan tinggi display jika perlu
                    .padding(horizontal = 16.dp, vertical = 24.dp) // Padding display
            )

            CalculatorButtonsGridModified(
                onButtonClick = { buttonSymbol, isBackspace ->
                    when {
                        isBackspace -> {
                            if (displayText.length > 1) {
                                displayText = displayText.dropLast(1)
                            } else if (displayText != "0") {
                                displayText = "0"
                            }
                        }
                        buttonSymbol == "C" -> displayText = "0"
                        else -> {
                            if (displayText == "0" && buttonSymbol != ",") { // Menggunakan koma dari gambar
                                displayText = buttonSymbol
                            } else if (displayText.length < 12) { // Batas panjang tampilan (sesuaikan)
                                displayText += buttonSymbol
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp) // Padding horizontal untuk grid agar lebih ke tengah
            )
        }
    }
}

@Composable
private fun CalculatorDisplay(displayText: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp), // Sedikit rounded corner untuk display card
        colors = CardDefaults.cardColors(containerColor = CalcDisplayCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Di gambar, display tidak terlalu menonjol
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = displayText,
                color = CalcDisplayTextColor,
                fontSize = 56.sp, // Ukuran font display bisa lebih besar sesuai gambar
                fontWeight = FontWeight.Normal, // Di gambar tidak terlihat tebal sekali
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Divider(color = CalcDisplayDividerColor, thickness = 1.dp)
        }
    }
}

@Composable
private fun CalculatorButtonsGridModified(
    onButtonClick: (symbol: String, isBackspace: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonCornerRadius = 16.dp // Radius untuk tombol "sedikit berbentuk kotak"

    // Konfigurasi Tombol berdasarkan gambar
    val buttonRowsConfig = listOf(
        listOf(
            CalcButtonInfo("", ButtonType.BACKSPACE, 24.sp, "Backspace", Icons.Outlined.Backspace),
            CalcButtonInfo("X/Y", ButtonType.FUNCTION, 20.sp, "X divided by Y"), // Ukuran font disesuaikan
            CalcButtonInfo("%", ButtonType.FUNCTION, 24.sp, "Percent"),
            CalcButtonInfo("÷", ButtonType.OPERATOR, 30.sp, "Divide")
        ),
        listOf(
            CalcButtonInfo("7", ButtonType.NUMBER, 26.sp, "Seven"),
            CalcButtonInfo("8", ButtonType.NUMBER, 26.sp, "Eight"),
            CalcButtonInfo("9", ButtonType.NUMBER, 26.sp, "Nine"),
            CalcButtonInfo("×", ButtonType.OPERATOR, 30.sp, "Multiply")
        ),
        listOf(
            CalcButtonInfo("4", ButtonType.NUMBER, 26.sp, "Four"),
            CalcButtonInfo("5", ButtonType.NUMBER, 26.sp, "Five"),
            CalcButtonInfo("6", ButtonType.NUMBER, 26.sp, "Six"),
            CalcButtonInfo("-", ButtonType.OPERATOR, 30.sp, "Subtract")
        ),
        listOf(
            CalcButtonInfo("1", ButtonType.NUMBER, 26.sp, "One"),
            CalcButtonInfo("2", ButtonType.NUMBER, 26.sp, "Two"),
            CalcButtonInfo("3", ButtonType.NUMBER, 26.sp, "Three"),
            CalcButtonInfo("+", ButtonType.OPERATOR, 30.sp, "Add")
        ),
        listOf(
            CalcButtonInfo("C", ButtonType.CLEAR, 26.sp, "Clear"),
            CalcButtonInfo("0", ButtonType.NUMBER, 26.sp, "Zero"),
            CalcButtonInfo(",", ButtonType.DECIMAL, 26.sp, "Comma"), // Mengganti . dengan ,
            CalcButtonInfo("=", ButtonType.OPERATOR, 30.sp, "Equal")
        )
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar baris tombol
    ) {
        buttonRowsConfig.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar tombol dalam satu baris
            ) {
                row.forEach { buttonInfo ->
                    CalculatorButtonModified(
                        info = buttonInfo,
                        onClick = { onButtonClick(buttonInfo.symbol, buttonInfo.type == ButtonType.BACKSPACE) },
                        shape = RoundedCornerShape(buttonCornerRadius),
                        modifier = Modifier.weight(1f) // Membuat tombol mengisi ruang secara merata
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorButtonModified(
    info: CalcButtonInfo,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    val backgroundColor: Color?
    val backgroundBrush: Brush?

    when (info.type) {
        ButtonType.OPERATOR -> {
            backgroundColor = null; backgroundBrush = CalcOperatorGradient
        }
        else -> { // NUMBER, FUNCTION, CLEAR, DECIMAL, BACKSPACE
            backgroundColor = CalcNonOperatorButtonBg; backgroundBrush = null
        }
    }

    val finalBackgroundModifier = when {
        backgroundBrush != null -> Modifier.background(brush = backgroundBrush, shape = shape)
        backgroundColor != null -> Modifier.background(color = backgroundColor, shape = shape)
        else -> Modifier.background(Color.DarkGray, shape = shape) // Fallback
    }

    Box(
        modifier = modifier
            .aspectRatio(1f) // Membuat tombol menjadi persegi (atau mendekati jika tingginya diatur oleh Row)
            .defaultMinSize(minWidth = 70.dp, minHeight = 70.dp) // Ukuran minimal tombol
            .then(finalBackgroundModifier)
            .clip(shape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = info.description },
        contentAlignment = Alignment.Center
    ) {
        if (info.icon != null) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.description, // Deskripsi bisa juga untuk ikon
                tint = CalcButtonIconColor,
                modifier = Modifier.size(28.dp) // Ukuran ikon
            )
        } else {
            Text(
                text = info.symbol,
                color = CalcButtonTextColor,
                fontSize = info.textSize,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CalculatorScreenModifiedPreview() {
    MaterialTheme {
        CalculatorScreenModified()
    }
}