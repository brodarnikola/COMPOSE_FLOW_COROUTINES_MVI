package com.example.mvi_compose

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.mvi_compose.unitTesting.CalculatorActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * JUnit4 Ui Tests for [CalculatorActivity] using the [AndroidJUnitRunner].
 * This class uses the JUnit4 syntax for tests.
 *
 *
 * With the new AndroidJUnit runner you can run both JUnit3 and JUnit4 tests in a single test
 * suite. The [AndroidRunnerBuilder] which extends JUnit's
 * [AllDefaultPossibilitiesBuilder] will create a single [ ] from all tests and run them.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class CalculatorInstrumentationTest {
    /**
     * Use [ActivityScenario] to create and launch of the activity.
     */
    @Before
    fun launchActivity() {
        ActivityScenario.launch(CalculatorActivity::class.java)
    }

    @Test
    fun noOperandShowsComputationError() {
        val expectedResult = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.computationError)
        Espresso.onView(withId(R.id.operation_add_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.operation_result_text_view))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedResult)))
    }

    @Test
    fun typeOperandsAndPerformAddOperation() {
        performOperation(R.id.operation_add_btn, "16.0", "16.0", "32.0")
    }

    @Test
    fun typeOperandsAndPerformSubOperation() {
        performOperation(R.id.operation_sub_btn, "32.0", "16.0", "16.0")
    }

    @Test
    fun typeOperandsAndPerformDivOperation() {
        performOperation(R.id.operation_div_btn, "128.0", "16.0", "8.0")
    }

    @Test
    fun divZeroForOperandTwoShowsError() {
        val expectedResult = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.computationError)
        performOperation(R.id.operation_div_btn, "128.0", "0.0", expectedResult)
    }

    @Test
    fun typeOperandsAndPerformMulOperation() {
        performOperation(R.id.operation_mul_btn, "16.0", "16.0", "256.0")
    }

    private fun performOperation(
        btnOperationResId: Int, operandOne: String,
        operandTwo: String, expectedResult: String
    ) {
        // Type the two operands in the EditText fields
        Espresso.onView(withId(R.id.operand_one_edit_text)).perform(
            ViewActions.typeText(operandOne),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(withId(R.id.operand_two_edit_text)).perform(
            ViewActions.typeText(operandTwo),
            ViewActions.closeSoftKeyboard()
        )

        // Click on a given operation button
        Espresso.onView(ViewMatchers.withId(btnOperationResId)).perform(ViewActions.click())

        // Check the expected test is displayed in the Ui
        Espresso.onView(withId(R.id.operation_result_text_view))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedResult)))
    }
}