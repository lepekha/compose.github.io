package com.inhelp

import com.inhelp.utils.FormatNumber
import com.inhelp.utils.format
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals("5.00", 4.9999999999.format(2))
    }

    @Test
    fun addition_isCorrect1() {
        assertEquals("5.00", BigDecimal.valueOf(5.0034534534).format(2))
    }
}
