package com.vocamoney.domain

import com.vocamoney.domain.model.AmountVnd
import com.vocamoney.domain.model.CategorySeed
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Guards fixtures/utterances_vi.json, which later parser slices (L1, L2) test against. */
class FixturesTest {
    private val fixtures: List<JsonObject> by lazy {
        val dir = System.getProperty("vocamoney.fixturesDir") ?: "../../fixtures"
        val text = File(dir, "utterances_vi.json").readText()
        Json.parseToJsonElement(text).jsonObject["utterances"]!!.jsonArray.map { it.jsonObject }
    }

    @Test fun coversEveryRowOfPrdFixtureTable() {
        assertTrue("expected the 8 rows of PRD §9 at least", fixtures.size >= 8)
        assertEquals(fixtures.size, fixtures.map { it["id"]!!.jsonPrimitive.content }.toSet().size)
    }

    @Test fun expectedItemsUseKnownCategoriesAndValidAmounts() {
        for (fixture in fixtures) {
            val items = fixture["expected"]!!.jsonObject["items"]?.jsonArray ?: continue
            for (item in items) {
                val obj = item.jsonObject
                val categoryId = obj["category_id"]!!.jsonPrimitive.content
                assertNotNull("unknown category $categoryId", CategorySeed.find(categoryId))
                val amount = obj["amount_vnd"]!!.jsonPrimitive.long
                assertNotNull("invalid amount $amount", AmountVnd.of(amount))
            }
        }
    }

    @Test fun unresolvedUtterancesNeverCarryItems() {
        // BR-02 / BR-09: an utterance without a trusted amount must not produce ledger items.
        fixtures
            .filter { it["expected"]!!.jsonObject["intent"]!!.jsonPrimitive.content in setOf("CLARIFICATION", "UNKNOWN") }
            .forEach { assertTrue(it["expected"]!!.jsonObject["items"]?.jsonArray.isNullOrEmpty()) }
    }
}
