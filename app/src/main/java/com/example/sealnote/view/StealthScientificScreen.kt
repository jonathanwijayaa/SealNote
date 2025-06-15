package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// REVISI: Menggunakan Icons.AutoMirrored.Filled.Backspace yang direkomendasikan
import androidx.compose.material.icons.automirrored.filled.Backspace
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

// --- START: Impor warna kustom dari Color.kt ---
import com.example.sealnote.ui.theme.SciCalcScreenBackground
import com.example.sealnote.ui.theme.SciCalcDisplayCardBackground
import com.example.sealnote.ui.theme.SciCalcDisplayModeColor
import com.example.sealnote.ui.theme.SciCalcDisplayResultColor
import com.example.sealnote.ui.theme.SciCalcDividerColor
import com.example.sealnote.ui.theme.SciCalcScientificButtonBg
import com.example.sealnote.ui.theme.SciCalcScientificButtonTextColor
import com.example.sealnote.ui.theme.SciCalcBasicNumberButtonBg
import com.example.sealnote.ui.theme.SciCalcBasicFunctionButtonBg
import com.example.sealnote.ui.theme.SciCalcBasicOperatorGradientStart
import com.example.sealnote.ui.theme.SciCalcBasicOperatorGradientEnd
import com.example.sealnote.ui.theme.SciCalcBasicButtonTextColor
// --- END: Impor warna kustom ---

// --- Data Class dan Enum untuk Tombol ---
private data class SciCalcButtonUIData(
    val symbol: String,
    val description: String,
    val textSize: TextUnit,
    val type: SciButtonType,
    val icon: ImageVector? = null // Opsional untuk ikon
)

private enum class SciButtonType {
    SCIENTIFIC,
    BASIC_NUMBER,
    BASIC_FUNCTION, // Untuk backspace, sqrt, % di basic grid
    BASIC_OPERATOR,
    BASIC_CLEAR,
    BASIC_DECIMAL
}

// --- Composable Utama ---
@Composable
fun StealthScientificScreen() {
    var displayMode by remember { mutableStateOf("Rad") }
    var mainDisplay by remember { mutableStateOf("78.000") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SciCalcScreenBackground // Menggunakan warna dari Color.kt
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScientificCalculatorDisplay(
                mode = displayMode,
                result = mainDisplay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp) // marginStart/End dari CardView XML
            )

            ScientificFunctionsGrid(
                onButtonClick = { symbol -> /* TODO: Handle scientific button click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp) // Padding untuk keseluruhan grid saintifik
            )

            BasicCalculatorPad(
                onButtonClick = { symbol -> /* TODO: Handle basic button click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp) // Padding untuk keseluruhan grid dasar
            )
        }
    }
}

// --- Composable untuk Area Display ---
@Composable
private fun ScientificCalculatorDisplay(
    mode: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp), // app:cardCornerRadius="0dp"
        colors = CardDefaults.cardColors(containerColor = SciCalcDisplayCardBackground), // Menggunakan warna dari Color.kt
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // app:cardElevation="8dp"
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) { // paddingBottom="8dp" dari LinearLayout dalam
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp), // Tinggi tetap dari LinearLayout horizontal
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mode,
                    color = SciCalcDisplayModeColor, // Menggunakan warna dari Color.kt
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f)) // Mendorong hasil ke kanan
                Text(
                    text = result,
                    color = SciCalcDisplayResultColor, // Menggunakan warna dari Color.kt
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(end = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Atau auto-size text
                )
            }
            // REVISI: Menggunakan HorizontalDivider yang direkomendasikan
            HorizontalDivider(
                color = SciCalcDividerColor, // Menggunakan warna dari Color.kt
                thickness = 1.dp,
                modifier = Modifier.padding(top = 4.dp) // marginTop="4dp"
            )
        }
    }
}

// --- Composable untuk Grid Tombol Saintifik ---
@Composable
private fun ScientificFunctionsGrid(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scientificButtonsConfig = listOf(
        // Row 1 (Dari gambar & XML)
        SciCalcButtonUIData("/", "Divide (fraction part)", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("√", "Square Root", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("∛", "Cube Root", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("∜", "Fourth Root", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("ln", "Natural Log", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("log", "Logarithm base 10", 14.sp, SciButtonType.SCIENTIFIC),
        // Row 2 (Dari gambar & XML)
        SciCalcButtonUIData("x!", "Factorial", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("sin", "Sine", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("cos", "Cosine", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("tan", "Tangent", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("e", "Euler's Number", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("EE", "Exponent", 14.sp, SciButtonType.SCIENTIFIC),
        // Row 3 (Dari XML, disesuaikan dengan format gambar jika bisa)
        SciCalcButtonUIData("Rad", "Radians Mode", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("sinh", "Hyperbolic Sine", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("cosh", "Hyperbolic Cosine", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("tanh", "Hyperbolic Tangent", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("sin⁻¹", "Arc Sine", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("cos⁻¹", "Arc Cosine", 14.sp, SciButtonType.SCIENTIFIC),
        // Row 4 (Dari sisa XML, ini adalah tombol ke 19-24)
        SciCalcButtonUIData("tan⁻¹", "Arc Tangent", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("1/x", "Reciprocal", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("x²", "Square", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("x³", "Cube", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("π", "Pi", 14.sp, SciButtonType.SCIENTIFIC),
        SciCalcButtonUIData("Deg", "Degrees Mode", 14.sp, SciButtonType.SCIENTIFIC)
    )

    Column(modifier = modifier) {
        scientificButtonsConfig.chunked(6).forEach { rowButtons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp) // Jarak antar tombol
            ) {
                rowButtons.forEach { buttonInfo ->
                    SciCalcActualButton(
                        info = buttonInfo,
                        onClick = { onButtonClick(buttonInfo.symbol) },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp) // layout_height="40dp"
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp)) // Jarak antar baris
        }
    }
}

// --- Composable untuk Grid Tombol Kalkulator Dasar ---
@Composable
private fun BasicCalculatorPad(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Konfigurasi tombol dasar dari XML (ID dan teks)
    val basicButtonsConfig = listOf(
        // Row 1
        SciCalcButtonUIData("⌫", "Backspace", 18.sp, SciButtonType.BASIC_FUNCTION, Icons.AutoMirrored.Filled.Backspace), // REVISI: Menggunakan AutoMirrored.Filled.Backspace
        SciCalcButtonUIData("√", "Square Root", 18.sp, SciButtonType.BASIC_FUNCTION),
        SciCalcButtonUIData("%", "Percent", 18.sp, SciButtonType.BASIC_FUNCTION),
        SciCalcButtonUIData("÷", "Divide", 20.sp, SciButtonType.BASIC_OPERATOR),
        // Row 2
        SciCalcButtonUIData("7", "Seven", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("8", "Eight", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("9", "Nine", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("×", "Multiply", 20.sp, SciButtonType.BASIC_OPERATOR),
        // Row 3
        SciCalcButtonUIData("4", "Four", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("5", "Five", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("6", "Six", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("-", "Subtract", 20.sp, SciButtonType.BASIC_OPERATOR),
        // Row 4
        SciCalcButtonUIData("1", "One", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("2", "Two", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("3", "Three", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData("+", "Add", 20.sp, SciButtonType.BASIC_OPERATOR),
        // Row 5
        SciCalcButtonUIData("C", "Clear", 18.sp, SciButtonType.BASIC_CLEAR),
        SciCalcButtonUIData("0", "Zero", 18.sp, SciButtonType.BASIC_NUMBER),
        SciCalcButtonUIData(",", "Decimal", 18.sp, SciButtonType.BASIC_DECIMAL),
        SciCalcButtonUIData("=", "Equals", 20.sp, SciButtonType.BASIC_OPERATOR)
    )

    Column(modifier = modifier) {
        basicButtonsConfig.chunked(4).forEach { rowButtons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                rowButtons.forEach { buttonInfo ->
                    SciCalcActualButton(
                        info = buttonInfo,
                        onClick = { onButtonClick(buttonInfo.symbol) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp) // layout_height="50dp"
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp)) // Jarak antar baris
        }
    }
}

// --- Composable Universal untuk Tombol Kalkulator ---
@Composable
private fun SciCalcActualButton(
    info: SciCalcButtonUIData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Bentuk tombol dari gambar: rounded rectangle
    val buttonShape = RoundedCornerShape(8.dp) // Sesuaikan radiusnya

    val backgroundColor: Color?
    val backgroundBrush: Brush?
    val textColor: Color

    // Membuat Brush di sini menggunakan warna dari Color.kt
    val basicOperatorGradient = Brush.horizontalGradient(
        listOf(SciCalcBasicOperatorGradientStart, SciCalcBasicOperatorGradientEnd)
    )

    when (info.type) {
        SciButtonType.SCIENTIFIC -> {
            backgroundColor = SciCalcScientificButtonBg
            backgroundBrush = null
            textColor = SciCalcScientificButtonTextColor
        }
        SciButtonType.BASIC_NUMBER, SciButtonType.BASIC_CLEAR, SciButtonType.BASIC_DECIMAL -> {
            backgroundColor = SciCalcBasicNumberButtonBg
            backgroundBrush = null
            textColor = SciCalcBasicButtonTextColor
        }
        SciButtonType.BASIC_FUNCTION -> {
            backgroundColor = SciCalcBasicFunctionButtonBg
            backgroundBrush = null
            textColor = SciCalcBasicButtonTextColor
        }
        SciButtonType.BASIC_OPERATOR -> {
            backgroundColor = null // Gunakan gradien
            backgroundBrush = basicOperatorGradient // Menggunakan gradien yang dibuat di sini
            textColor = SciCalcBasicButtonTextColor
        }
    }

    val finalBackgroundModifier = when {
        backgroundBrush != null -> Modifier.background(brush = backgroundBrush, shape = buttonShape)
        backgroundColor != null -> Modifier.background(color = backgroundColor, shape = buttonShape)
        else -> Modifier.background(Color.DarkGray, shape = buttonShape) // Fallback
    }

    Box(
        modifier = modifier
            .padding(2.dp) // layout_margin="2dp" pada setiap tombol
            .then(finalBackgroundModifier)
            .clip(buttonShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = info.description },
        contentAlignment = Alignment.Center
    ) {
        if (info.icon != null) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.description,
                tint = textColor, // Asumsi ikon juga berwarna sama dengan teks tombol
                modifier = Modifier.size(if (info.type == SciButtonType.SCIENTIFIC) 18.dp else 22.dp) // Ukuran ikon
            )
        } else {
            Text(
                text = info.symbol,
                color = textColor,
                fontSize = info.textSize,
                fontWeight = if (info.type == SciButtonType.BASIC_OPERATOR || info.type == SciButtonType.SCIENTIFIC) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScientificCalculatorScreenPreview() {
    MaterialTheme { // Atau tema kustom aplikasi Anda
        StealthScientificScreen()
    }
}