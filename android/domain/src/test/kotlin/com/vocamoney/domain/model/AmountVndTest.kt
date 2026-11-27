package com.vocamoney.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountVndTest {
    @Test fun formatsWithVietnameseGrouping() {
        assertEquals("35.000 ₫", AmountVnd.of(35_000)!!.format())
        assertEquals("15.000.000 ₫", AmountVnd.of(15_000_000)!!.format())
        assertEquals("500 ₫", AmountVnd.of(500)!!.format())
    }

    @Test fun rejectsZeroNegativeAndOverflow() {
        assertNull(AmountVnd.of(0))
        assertNull(AmountVnd.of(-35_000))
        assertNull(AmountVnd.of(AmountVnd.MAX_DONG + 1))
        assertNull(AmountVnd.of(Long.MAX_VALUE))
    }

    @Test fun acceptsUpperBound() {
        assertEquals(AmountVnd.MAX_DONG, AmountVnd.of(AmountVnd.MAX_DONG)!!.dong)
    }
}
