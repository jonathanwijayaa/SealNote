package com.example.sealnote.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel

class StealthCalculatorViewModel : ViewModel() {
    private var clickCount = 0
    private val resetTime = 2000L // Reset dalam 2 detik
    private val handler = Handler(Looper.getMainLooper())
    private val resetCounter = Runnable { clickCount = 0 }

    fun onButtonClick(onTripleClick: () -> Unit) {
        clickCount++
        handler.removeCallbacks(resetCounter)
        handler.postDelayed(resetCounter, resetTime)

        if (clickCount == 3) {
            onTripleClick() // Panggil callback jika sudah 3 kali klik
            resetCounter.run() // Reset counter setelah menavigasi
        }
    }
}