package com.example.sealnote.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal

class StealthCalculatorViewModel : ViewModel() {

    // --- Triple Click Logic ---
    private var clickCount = 0
    // Sandi default. Nantinya bisa diubah dari halaman pengaturan.
    var targetButtonSymbolForTripleClick: String = "C"
        private set
    private val requiredClickCount: Int = 3

    private val resetTime = 1000L // Waktu dalam milidetik untuk mereset hitungan klik
    private val handler = Handler(Looper.getMainLooper())
    private val resetCounter = Runnable { clickCount = 0 }

    // Exposed event for UI to observe
    var onLoginRequired: (() -> Unit)? = null // This will be set by the UI to trigger navigation

    // --- Calculator State ---
    var displayTopRow by mutableStateOf("") // Added for the top display
    var displayBottomRow by mutableStateOf("0") // Changed from displayText

    private var currentExpression: String = ""
    private var lastInputWasOperator: Boolean = false
    private var lastInputWasEquals: Boolean = false

    var onCalculationFinished: ((expression: String, result: String) -> Unit)? = null

    // Renamed for clarity and to indicate it's the primary button handler
    fun onCalculatorButtonClick(symbol: String) {
        // Register button click for triple-click detection
        registerButtonClickForTripleClick(symbol)

        // Run calculator logic
        when (symbol) {
            "C" -> clearAll()
            "+", "-", "×", "÷" -> handleOperator(symbol)
            "=" -> handleEquals()
            "," -> handleDecimal()
            else -> handleNumber(symbol) // Numbers 0-9
        }
    }

    private fun registerButtonClickForTripleClick(buttonSymbol: String) {
        if (buttonSymbol == targetButtonSymbolForTripleClick) {
            clickCount++
            handler.removeCallbacks(resetCounter)
            handler.postDelayed(resetCounter, resetTime)

            if (clickCount >= requiredClickCount) {
                onLoginRequired?.invoke() // Trigger the navigation event
                clickCount = 0 // Reset count
                handler.removeCallbacks(resetCounter)
            }
        } else {
            // If another button is pressed, reset the count
            clickCount = 0
            handler.removeCallbacks(resetCounter)
        }
    }

    fun onBackspaceClick() {
        if (currentExpression.isNotEmpty()) {
            currentExpression = currentExpression.dropLast(1)
            displayBottomRow = if (currentExpression.isEmpty()) "0" else currentExpression
            displayTopRow = "" // Clear top row on backspace
            lastInputWasOperator = if (currentExpression.isNotEmpty()) isOperator(currentExpression.last().toString()) else false
            lastInputWasEquals = false
        } else {
            // If expression is already empty, reset to "0" and clear top row
            displayBottomRow = "0"
            displayTopRow = ""
        }
    }

    private fun clearAll() {
        displayBottomRow = "0"
        displayTopRow = "" // Clear top display
        currentExpression = ""
        lastInputWasOperator = false
        lastInputWasEquals = false
    }

    private fun handleNumber(numberChar: String) {
        if (lastInputWasEquals) {
            currentExpression = ""
            lastInputWasEquals = false
        }
        if (currentExpression == "0" && numberChar == "0") {
            // Do nothing if trying to add more zeros to "0"
            return
        }
        if (currentExpression == "0") {
            currentExpression = numberChar
        } else {
            currentExpression += numberChar
        }
        displayBottomRow = currentExpression
        displayTopRow = "" // Clear top row when typing new number
        lastInputWasOperator = false
    }

    private fun handleDecimal() {
        if (lastInputWasEquals) {
            currentExpression = "0,"
            lastInputWasEquals = false
        } else if (currentExpression.isEmpty() || lastInputWasOperator) {
            currentExpression += "0,"
        } else {
            // Check if the last number segment already contains a decimal
            val lastOperatorIndex = currentExpression.indexOfLast { it in "+-×÷" }
            val lastNumberSegment = if (lastOperatorIndex != -1) {
                currentExpression.substring(lastOperatorIndex + 1)
            } else {
                currentExpression // If no operator, the whole expression is the number
            }
        }
        displayBottomRow = currentExpression
        lastInputWasOperator = false
    }

    private fun handleOperator(newOperator: String) {
        if (currentExpression.isNotEmpty()) {
            if (lastInputWasOperator) {
                // Replace the last operator if consecutive operators are entered
                currentExpression = currentExpression.dropLast(1) + newOperator
            } else {
                currentExpression += newOperator
            }
            lastInputWasEquals = false
            lastInputWasOperator = true
            displayBottomRow = currentExpression
            displayTopRow = "" // Clear top row when an operator is pressed
        }
    }

    private fun handleEquals() {
        if (currentExpression.isEmpty() || lastInputWasOperator) {
            return
        }

        val expressionToEvaluate = currentExpression
            .replace("×", "*")
            .replace("÷", "/")
            .replace(",", ".")

        val originalExpressionForHistory = currentExpression

        try {
            val expression = ExpressionBuilder(expressionToEvaluate).build()
            val result = expression.evaluate()
            val formattedResult = formatResult(result.toBigDecimal())

            displayTopRow = "$originalExpressionForHistory ="
            displayBottomRow = formattedResult
            onCalculationFinished?.invoke(originalExpressionForHistory, formattedResult)
            currentExpression = formattedResult.replace(",", ".") // Set current expression to result for chaining
            lastInputWasEquals = true
            lastInputWasOperator = false
        } catch (e: Exception) {
            displayTopRow = "$originalExpressionForHistory ="
            displayBottomRow = "Error"
            onCalculationFinished?.invoke(originalExpressionForHistory, "Error")
            currentExpression = ""
            lastInputWasEquals = false
            lastInputWasOperator = false
        }
    }

    private fun formatResult(result: BigDecimal): String {
        val formatted = result.stripTrailingZeros().toPlainString()
        return formatted.replace(".", ",")
    }

    private fun isOperator(symbol: String): Boolean {
        return symbol == "+" || symbol == "-" || symbol == "×" || symbol == "÷"
    }
}