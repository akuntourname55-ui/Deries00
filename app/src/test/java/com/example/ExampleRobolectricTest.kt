package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SicboPeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sicbo 6 Pola Tracker", appName)
    }

    @Test
    fun `sicbo 6 calculation logic`() {
        val bigPeriod = SicboPeriod(
            periodNumber = "20261006-001",
            timestamp = System.currentTimeMillis(),
            dice = listOf(4, 5, 3, 6, 2, 4) // sum = 24
        )
        assertEquals(24, bigPeriod.total)
        assertTrue(bigPeriod.isBig)
        assertFalse(bigPeriod.isSmall)
        assertTrue(bigPeriod.isEven)

        val smallPeriod = SicboPeriod(
            periodNumber = "20261006-002",
            timestamp = System.currentTimeMillis(),
            dice = listOf(1, 2, 3, 1, 4, 2) // sum = 13
        )
        assertEquals(13, smallPeriod.total)
        assertFalse(smallPeriod.isBig)
        assertTrue(smallPeriod.isSmall)
        assertTrue(smallPeriod.isOdd)

        val quadPeriod = SicboPeriod(
            periodNumber = "20261006-003",
            timestamp = System.currentTimeMillis(),
            dice = listOf(5, 5, 2, 5, 5, 1) // four 5s
        )
        assertEquals(5, quadPeriod.same4Dice)
    }
}
