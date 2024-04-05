package com.example.mvi_compose

import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.mvi_compose.unitTesting.GeoUtils
import com.example.mvi_compose.unitTesting.MockUnitTest
import com.example.mvi_compose.unitTesting.Operations
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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
class GeoUtilsTest {
    @Mock
    private val geocoder: Geocoder? = null

    @Mock
    private val addressForHomeLund: Address? = null
    private var geoUtils: GeoUtils? = null
    @Before
    fun setUp() {
//        geoUtils = GeoUtils(context = null)
        geoUtils = GeoUtils(geocoder!!)
    }

    @Test
    @Throws(Exception::class)
    fun coordinateWithNoZipCodeReturnNull() {
        val zipCode = geoUtils!!.getCurrentCode(0.0, 0.0)
        assertNull(zipCode)
    }

    @Test
    @Throws(Exception::class)
    fun validGeolocationPasses() {
        val homeLundZip = "22223"

        // whenever we ask for Lund home address for the postal code,
        // the mock object is going to return 22223
        `when`(addressForHomeLund?.getPostalCode() ?: "")
            .thenReturn(homeLundZip)

        // whenever we mocked GeoCoder for the addresses
        // associated with the Home(Lund) latitude and longitude
        // we want to return the Home(Lund) mock address
        `when`(
            geocoder!!.getFromLocation(
                anyDouble(),
                anyDouble(),
                anyInt()
            )
        )
            .thenReturn(listOf(addressForHomeLund))
        val zipCodeOne = geoUtils!!.getCurrentCode(45.4534534, -34.2334234)
        val zipCodeTwo = geoUtils!!.getCurrentCode(67.4534534, 78.2334234)

        System.out.println(homeLundZip)
        System.out.println(zipCodeOne)
        System.out.println(assertEquals(homeLundZip, zipCodeOne))

        assertEquals(homeLundZip, zipCodeOne)
        assertEquals(homeLundZip, zipCodeTwo)
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