package com.example.sealnote.model

import kotlin.math.sqrt

class Scientific (){

    fun factorial(num: Int): Int {
        return if (num == 0 || num == 1) 1 else num * factorial(num - 1)
    }

    fun squareRoot(num: Double): Double {
        return sqrt(num)
    }

    fun sin(num: Double): Double {
        return sin(num)
    }
}