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
    var displayText by mutableStateOf("0")
        private set

    private var currentExpression: String = ""
    private var lastInputWasOperator: Boolean = false
    private var lastInputWasEquals: Boolean = false

    var onCalculationFinished: ((expression: String, result: String) -> Unit)? = null

    fun onCalculatorButtonClick(symbol: String, onTripleClick: () -> Unit) {
        when (symbol) {
            "C" -> clearAll()
            "+", "-", "×", "÷" -> handleOperator(symbol)
            "=" -> handleEquals()
            "," -> handleDecimal()
            else -> handleNumber(symbol) // Digits 0-9
        }
        registerButtonClickForTripleClick(symbol, onTripleClick)
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
                lastInputWasOperator = isOperator(currentExpression.last().toString())
                lastInputWasEquals = false
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
        handler.removeCallbacks(resetCounter)
        clickCount = 0
    }

    private fun handleNumber(numberChar: String) {
        if (lastInputWasEquals) {
            currentExpression = numberChar
            lastInputWasEquals = false
        } else if (currentExpression == "0" && numberChar != "0" && numberChar != ",") {
            currentExpression = numberChar
        } else if (lastInputWasOperator) {
            currentExpression += numberChar
        } else {
            // FIX: Use Regex to find the last number part
            val lastNumberPartMatch = Regex("[0-9]+(,[0-9]*)?$").find(currentExpression)
            val lastNumberPart = lastNumberPartMatch?.value ?: ""

            if (lastNumberPart.length < 12) {
                currentExpression += numberChar
            } else {
                return
            }
        }
        displayText = currentExpression
        lastInputWasOperator = false
    }

    private fun handleDecimal() {
        if (lastInputWasEquals) {
            currentExpression = "0,"
            lastInputWasEquals = false
        } else if (currentExpression.isEmpty() || lastInputWasOperator) {
            currentExpression += "0,"
        } else {
            // FIX: Use Regex to find the last number part and then check contains
            val lastNumberPartMatch = Regex("[0-9]+(,[0-9]*)?$").find(currentExpression)
            val lastNumberPart = lastNumberPartMatch?.value ?: ""

            if (!lastNumberPart.contains(',')) { // Corrected: Use Char literal for contains
                currentExpression += ","
            } else {
                return
            }
        }
        displayText = currentExpression
        lastInputWasOperator = false
    }

    private fun handleOperator(newOperator: String) {
        if (currentExpression.isEmpty()) {
            if (newOperator == "-") {
                currentExpression = "-"
            } else {
                currentExpression = "0" + newOperator
            }
        } else if (lastInputWasEquals) {
            currentExpression = displayText + newOperator
            lastInputWasEquals = false
        } else if (lastInputWasOperator) {
            currentExpression = currentExpression.dropLast(1) + newOperator
        } else {
            currentExpression += newOperator
        }
        displayText = currentExpression
        lastInputWasOperator = true
    }

    private fun handleEquals() {
        if (currentExpression.isEmpty() || lastInputWasOperator) {
            return
        }

        val originalExpressionForHistory = currentExpression

        try {
            val expressionToEvaluate = currentExpression
                .replace("×", "*")
                .replace("÷", "/")
                .replace(",", ".")

            val result = evaluateExpression(expressionToEvaluate)
            val formattedResult = formatResult(result)

            displayText = formattedResult
            currentExpression = formattedResult
            lastInputWasEquals = true
            lastInputWasOperator = false

            onCalculationFinished?.invoke(originalExpressionForHistory + " =", formattedResult)

        } catch (e: Exception) {
            displayText = "Error"
            currentExpression = ""
            lastInputWasEquals = false
            lastInputWasOperator = false
            onCalculationFinished?.invoke(originalExpressionForHistory + " =", "Error")
        }
    }

    private fun evaluateExpression(expression: String): BigDecimal {
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

        var result: BigDecimal
        try {
            result = tokens[0].toBigDecimal()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid number format: ${tokens[0]}", e)
        }

        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val nextNum: BigDecimal
            try {
                nextNum = tokens[i+1].toBigDecimal()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid number format after operator: ${tokens[i+1]}", e)
            }

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
        val formatted = result.stripTrailingZeros().toPlainString()
        val maxLength = 12

        return if (formatted.length > maxLength) {
            if (formatted.contains(".")) {
                val integerPart = formatted.substringBefore(".")
                if (integerPart.length >= maxLength) {
                    "ERR"
                } else {
                    val maxDecimalLength = maxLength - integerPart.length - 1
                    if (maxDecimalLength >= 0) {
                        formatted.substring(0, integerPart.length + 1 + minOf(formatted.substringAfter(".").length, maxDecimalLength)).replace(".", ",")
                    } else {
                        "ERR"
                    }
                }
            } else {
                "ERR"
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
    var targetButtonSymbolForTripleClick: String = "C"
        set(value) {
            field = value
            clickCount = 0
            handler.removeCallbacks(resetCounter)
        }

    private val resetTime = 1000L
    private val handler = Handler(Looper.getMainLooper())
    private val resetCounter = Runnable { clickCount = 0 }

    fun registerButtonClickForTripleClick(buttonSymbol: String, onTripleClick: () -> Unit) {
        if (buttonSymbol == targetButtonSymbolForTripleClick) {
            clickCount++
            handler.removeCallbacks(resetCounter)
            handler.postDelayed(resetCounter, resetTime)

            if (clickCount >= 3) {
                onTripleClick()
                resetCounter.run()
            }
        } else {
            clickCount = 0
            handler.removeCallbacks(resetCounter)
        }
    }
}