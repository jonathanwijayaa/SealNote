package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel // Import untuk ViewModel

// Import warna kustom dari Color.kt
import com.example.sealnote.ui.theme.CalcScreenBackground
import com.example.sealnote.ui.theme.CalcDisplayCardBackground
import com.example.sealnote.ui.theme.CalcDisplayTextColor
import com.example.sealnote.ui.theme.CalcDisplayDividerColor
import com.example.sealnote.ui.theme.CalcNonOperatorButtonBg
import com.example.sealnote.ui.theme.CalcOperatorGradientStart
import com.example.sealnote.ui.theme.CalcOperatorGradientEnd
import com.example.sealnote.ui.theme.CalcButtonTextColor
import com.example.sealnote.ui.theme.CalcButtonIconColor
import com.example.sealnote.viewmodel.StealthCalculatorViewModel // Import ViewModel kita

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
fun StealthCalculatorScreen(
    // Callback untuk navigasi ke LoginScreen
    onNavigateToLogin: () -> Unit,
    viewModel: StealthCalculatorViewModel = viewModel() // Inject ViewModel
) {
    // REVISI: Mengatur tombol target untuk triple-click
    // Anda bisa mengubah ini ke tombol lain sesuai kebutuhan, contohnya "7"
    // Pastikan tombol tersebut ada di konfigurasi buttonRowsConfig
    DisposableEffect(Unit) {
        viewModel.targetButtonSymbolForTripleClick = "C" // Targetkan tombol "C"
        onDispose { }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CalcScreenBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CalculatorDisplay(
                displayText = viewModel.displayText, // Ambil dari ViewModel
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )

            CalculatorButtonsGridModified(
                onButtonClick = { buttonSymbol, isBackspace ->
                    // Logika kalkulator ditangani di ViewModel
                    if (isBackspace) {
                        viewModel.onBackspaceClick()
                    } else {
                        viewModel.onCalculatorButtonClick(buttonSymbol)
                    }
                    // Daftarkan setiap klik tombol ke triple-click logic
                    viewModel.registerButtonClickForTripleClick(buttonSymbol, onNavigateToLogin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CalculatorDisplay(displayText: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CalcDisplayCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = displayText,
                color = CalcDisplayTextColor,
                fontSize = 56.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            HorizontalDivider(color = CalcDisplayDividerColor, thickness = 1.dp)
        }
    }
}

@Composable
private fun CalculatorButtonsGridModified(
    onButtonClick: (symbol: String, isBackspace: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonCornerRadius = 16.dp

    val buttonRowsConfig = listOf(
        listOf(
            CalcButtonInfo("", ButtonType.BACKSPACE, 24.sp, "Backspace", Icons.AutoMirrored.Filled.Backspace),
            CalcButtonInfo("X/Y", ButtonType.FUNCTION, 20.sp, "X divided by Y"),
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
            CalcButtonInfo(",", ButtonType.DECIMAL, 26.sp, "Comma"),
            CalcButtonInfo("=", ButtonType.OPERATOR, 30.sp, "Equal")
        )
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonRowsConfig.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { buttonInfo ->
                    CalculatorButtonModified(
                        info = buttonInfo,
                        onClick = { onButtonClick(buttonInfo.symbol, buttonInfo.type == ButtonType.BACKSPACE) },
                        shape = RoundedCornerShape(buttonCornerRadius),
                        modifier = Modifier.weight(1f)
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

    val CalcOperatorGradient = Brush.horizontalGradient(
        listOf(CalcOperatorGradientStart, CalcOperatorGradientEnd)
    )

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
            .aspectRatio(1f)
            .defaultMinSize(minWidth = 70.dp, minHeight = 70.dp)
            .then(finalBackgroundModifier)
            .clip(shape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = info.description },
        contentAlignment = Alignment.Center
    ) {
        if (info.icon != null) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.description,
                tint = CalcButtonIconColor,
                modifier = Modifier.size(28.dp)
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
        // Dalam preview, kita tidak memiliki LoginScreen, jadi kita berikan lambda kosong.
        StealthCalculatorScreen(onNavigateToLogin = {})
    }
}