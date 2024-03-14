package com.example.mvi_compose

import android.content.Context
import android.util.Log
import com.example.mvi_compose.unitTesting.MockUnitTest
import com.example.mvi_compose.unitTesting.Operations
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

@RunWith(MockitoJUnitRunner::class)
class ComputationTest {
    @Mock
    lateinit var operators: Operations
    lateinit var mockUnitTest: MockUnitTest

    @Before
    fun setUp(){
        mockUnitTest = MockUnitTest(operators)
    }

    @Test
    fun givenValidInput_getAddition_shouldCallAddOperator() {
        val x = 5
        val y = 10
        val response = mockUnitTest.getAddition(x, y)
        assertEquals(0, response)
        verify(operators).add(x, y)
    }

    @Test
    fun givenValidInput_getSubtraction_shouldCallSubtractOperator() {
        val x = 5
        val y = 10
        mockUnitTest.getSubtraction(x, y)
        val response = Operations.subtract(x, y)
        assertEquals(-5, response)
    }

    @Test
    fun givenValidInput_getMultiplication_shouldCallMultiplyOperator() {
        val x = 5
        val y = 10
        mockUnitTest.getMultiplication(x, y)
        Operations.multiply(x, y)
    }

    @Test
    fun givenValidInput_getDivision_shouldCallDivideOperator() {
        val x = 5
        val y = 1
        mockUnitTest.getDivision(x, y)
        Operations.divide(x, y)
    }

}