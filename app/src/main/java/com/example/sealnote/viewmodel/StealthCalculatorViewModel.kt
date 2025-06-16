package com.example.sealnote.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class StealthCalculatorViewModel : ViewModel() {

    // --- Kalkulator Logic ---
    // REVISI: Mengubah displayText untuk menyimpan ekspresi
    var displayText by mutableStateOf("0")
        private set

    // REVISI: Variabel untuk menyimpan ekspresi penuh sebelum dievaluasi
    private var currentExpression: String = ""
    private var lastInputWasOperator: Boolean = false
    private var lastInputWasEquals: Boolean = false

    fun onCalculatorButtonClick(symbol: String) {
        when (symbol) {
            "C" -> clearAll()
            "+", "-", "×", "÷" -> handleOperator(symbol)
            "=" -> handleEquals()
            "," -> handleDecimal()
            else -> handleNumber(symbol) // Angka 0-9
        }
        // Reset triple click counter jika user mulai mengetik angka setelah C
        if (symbol != targetButtonSymbolForTripleClick) {
            handler.removeCallbacks(resetCounter)
            clickCount = 0
        }
    }

    fun onBackspaceClick() {
        if (currentExpression.isNotEmpty()) {
            currentExpression = currentExpression.dropLast(1)
            if (currentExpression.isEmpty()) {
                displayText = "0"
                lastInputWasOperator = false
                lastInputWasEquals = false
            } else {
                displayText = currentExpression
                // Cek apakah karakter terakhir adalah operator setelah backspace
                lastInputWasOperator = isOperator(currentExpression.last().toString())
                lastInputWasEquals = false // Backspace pasti membatalkan state "equals"
            }
        } else {
            displayText = "0"
            lastInputWasOperator = false
            lastInputWasEquals = false
        }
    }

    private fun clearAll() {
        displayText = "0"
        currentExpression = ""
        lastInputWasOperator = false
        lastInputWasEquals = false
        // Selalu reset triple click counter saat "C" diklik
        handler.removeCallbacks(resetCounter)
        clickCount = 0
    }

    private fun handleNumber(numberChar: String) {
        if (lastInputWasEquals || displayText == "0") { // Jika sebelumnya "=" atau display hanya "0"
            currentExpression = numberChar // Mulai ekspresi baru
            displayText = numberChar
            lastInputWasEquals = false
        } else {
            currentExpression += numberChar
            displayText = currentExpression
        }
        lastInputWasOperator = false
    }

    private fun handleDecimal() {
        if (lastInputWasEquals) {
            currentExpression = "0,"
            displayText = "0,"
            lastInputWasEquals = false
        } else if (currentExpression.isEmpty() || lastInputWasOperator) {
            currentExpression += "0,"
            displayText = currentExpression
        } else if (!currentNumberContainsDecimal()) {
            currentExpression += ","
            displayText = currentExpression
        }
        lastInputWasOperator = false
    }

    private fun currentNumberContainsDecimal(): Boolean {
        // Mendapatkan angka terakhir dalam ekspresi untuk memeriksa desimal
        val parts = currentExpression.split("+", "-", "×", "÷")
        return parts.lastOrNull()?.contains(",") ?: false
    }

    private fun handleOperator(newOperator: String) {
        if (currentExpression.isEmpty()) {
            // Jika ekspresi kosong, dan user menekan operator (misal "-"), mulai dengan "0-"
            currentExpression = "0" + newOperator
            displayText = currentExpression
        } else if (lastInputWasEquals) { // Jika sebelumnya hasil perhitungan
            // Gunakan hasil perhitungan sebagai angka pertama untuk operasi baru
            currentExpression = displayText + newOperator
            displayText = currentExpression
            lastInputWasEquals = false
        } else if (lastInputWasOperator) {
            // Ganti operator terakhir jika operator baru ditekan
            currentExpression = currentExpression.dropLast(1) + newOperator
            displayText = currentExpression
        } else {
            currentExpression += newOperator
            displayText = currentExpression
        }
        lastInputWasOperator = true
    }

    private fun handleEquals() {
        if (currentExpression.isEmpty() || lastInputWasOperator) {
            // Jika ekspresi kosong atau berakhir dengan operator, tidak melakukan apa-apa atau tampilkan error
            return
        }

        try {
            // REVISI: Mengganti koma dengan titik untuk evaluasi BigMath
            val expressionToEvaluate = currentExpression.replace("×", "*").replace("÷", "/").replace(",", ".")

            // Evaluasi ekspresi menggunakan shunting-yard algorithm sederhana atau library
            // Untuk kesederhanaan, kita akan menggunakan pendekatan evaluasi berurutan dari kiri ke kanan
            // Untuk kalkulator yang lebih canggih, Anda akan memerlukan parser ekspresi yang tepat
            val result = evaluateExpression(expressionToEvaluate)
            displayText = formatResult(result)
            currentExpression = formatResult(result) // Set currentExpression ke hasil untuk operasi selanjutnya
            lastInputWasEquals = true
            lastInputWasOperator = false
        } catch (e: Exception) {
            displayText = "Error"
            currentExpression = ""
            lastInputWasEquals = false
            lastInputWasOperator = false
        }
    }

    // Fungsi sederhana untuk mengevaluasi ekspresi (perhatikan ini sangat dasar dan tidak menangani prioritas operator)
    // Untuk kalkulator nyata, Anda memerlukan algoritma Shunting-yard atau library ekspresi.
    private fun evaluateExpression(expression: String): BigDecimal {
        // Memisahkan angka dan operator
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        expression.forEach { char ->
            if (char.isDigit() || char == '.') {
                currentNumber.append(char)
            } else {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber = StringBuilder()
                }
                tokens.add(char.toString())
            }
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        if (tokens.isEmpty()) return BigDecimal.ZERO

        var result = tokens[0].toBigDecimal()
        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val nextNum = tokens[i+1].toBigDecimal()
            result = when (op) {
                "+" -> result.add(nextNum)
                "-" -> result.subtract(nextNum)
                "*" -> result.multiply(nextNum)
                "/" -> {
                    if (nextNum == BigDecimal.ZERO) throw ArithmeticException("Division by zero")
                    result.divide(nextNum, 8, RoundingMode.HALF_UP)
                }
                else -> throw IllegalArgumentException("Unknown operator: $op")
            }
            i += 2
        }
        return result
    }

    private fun formatResult(result: BigDecimal): String {
        // Hapus trailing zeros jika tidak ada desimal, lalu ganti titik menjadi koma
        // Batasi panjang tampilan agar tidak terlalu panjang
        val formatted = result.stripTrailingZeros().toPlainString()
        val maxLength = 12 // Misalnya, batasi hingga 12 karakter
        return if (formatted.length > maxLength) {
            // Jika terlalu panjang, coba bulatkan lagi atau gunakan notasi ilmiah
            // Untuk contoh ini, kita potong atau tampilkan error sederhana
            if (formatted.contains(".")) {
                val integerPart = formatted.substringBefore(".")
                val decimalPart = formatted.substringAfter(".")
                if (integerPart.length >= maxLength) "ERR" // Integer terlalu besar
                else formatted.substring(0, minOf(maxLength, formatted.length)).replace(".", ",")
            } else {
                "ERR" // Integer terlalu besar
            }
        } else {
            formatted.replace(".", ",")
        }
    }

    private fun isOperator(symbol: String): Boolean {
        return symbol == "+" || symbol == "-" || symbol == "×" || symbol == "÷"
    }

    // --- Triple Click Logic ---
    private var clickCount = 0
    var targetButtonSymbolForTripleClick: String = "C" // Default target: "C"
        set(value) {
            field = value
            clickCount = 0 // Reset counter jika target diubah
            handler.removeCallbacks(resetCounter)
        }

    private val resetTime = 1000L // Reset dalam 1 detik (dipercepat sedikit)
    private val handler = Handler(Looper.getMainLooper())
    private val resetCounter = Runnable { clickCount = 0 }

    fun registerButtonClickForTripleClick(buttonSymbol: String, onTripleClick: () -> Unit) {
        if (buttonSymbol == targetButtonSymbolForTripleClick) {
            clickCount++
            handler.removeCallbacks(resetCounter)
            handler.postDelayed(resetCounter, resetTime)

            if (clickCount >= 3) { // Menggunakan >= 3 untuk lebih fleksibel
                onTripleClick() // Panggil callback jika sudah 3 kali klik
                resetCounter.run() // Reset counter setelah menavigasi
            }
        } else {
            // Jika tombol yang berbeda diklik, reset counter
            clickCount = 0
            handler.removeCallbacks(resetCounter)
        }
    }
}