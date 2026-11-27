package com.vocamoney.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CategorySeedTest {
    @Test fun seedsElevenUniqueCategories() {
        assertEquals(11, CategorySeed.all.size)
        assertEquals(11, CategorySeed.all.map { it.id }.toSet().size)
    }

    @Test fun salaryIsIncomeAndOthersAreNot() {
        assertEquals(CategoryType.INCOME, CategorySeed.find("INCOME_SALARY")!!.type)
        assertEquals(CategoryType.EXPENSE, CategorySeed.find("FOOD_COFFEE")!!.type)
    }

    @Test fun uncategorizedFallbackExists() {
        assertNotNull(CategorySeed.find(CategorySeed.UNCATEGORIZED))
    }
}
