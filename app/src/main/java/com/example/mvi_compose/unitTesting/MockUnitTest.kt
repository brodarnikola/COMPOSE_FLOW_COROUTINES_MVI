package com.example.mvi_compose.unitTesting

class MockUnitTest(private val operators: Operations) {
    fun getAddition(x: Int, y: Int): Int = operators.add(x, y)

    fun getSubtraction(x: Int, y: Int): Int = operators.subtract(x, y)

    fun getMultiplication(x: Int, y: Int): Int = operators.multiply(x, y)

    fun getDivision(x: Int, y: Int): Int = operators.divide(x, y)
}

object Operations {
    fun add(x: Int, y: Int): Int = x + y

    fun subtract(x: Int, y: Int): Int = x - y

    fun multiply(x: Int, y: Int): Int = x * y

    fun divide(x: Int, y: Int): Int = x / y
}