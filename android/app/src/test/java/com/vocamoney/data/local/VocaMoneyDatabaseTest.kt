package com.vocamoney.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocamoney.data.local.entity.LedgerEntryEntity
import com.vocamoney.data.local.entity.VoiceSessionEntity
import com.vocamoney.domain.model.CategorySeed
import com.vocamoney.domain.model.EntrySource
import com.vocamoney.domain.model.EntryStatus
import com.vocamoney.domain.model.EntryType
import com.vocamoney.domain.model.PaymentMethod
import com.vocamoney.domain.model.VoiceSessionStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class VocaMoneyDatabaseTest {
    private lateinit var db: VocaMoneyDatabase

    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VocaMoneyDatabase::class.java)
            .addCallback(SeedCallback)
            .allowMainThreadQueries()
            .build()
    }

    @After fun tearDown() = db.close()

    @Test fun seedsElevenSystemCategoriesOnCreate() = runTest {
        val categories = db.categoryDao().getAll()
        assertEquals(CategorySeed.all.map { it.id }, categories.map { it.id })
        assertEquals("Cà phê", categories.first { it.id == "FOOD_COFFEE" }.nameVi)
    }

    @Test fun duplicateIdempotencyKeyIsRejected() = runTest {
        val dao = db.voiceSessionDao()
        dao.insert(session(id = "s1", key = "k1"))
        try {
            dao.insert(session(id = "s2", key = "k1"))
            fail("expected a unique constraint failure")
        } catch (expected: SQLiteConstraintException) {
            // BR-04: a retried voice session must not create a second record.
        }
        assertEquals("s1", dao.findByIdempotencyKey("k1")!!.id)
    }

    @Test fun ledgerEntryRoundTripsAmountAsWholeDong() = runTest {
        db.voiceSessionDao().insert(session(id = "s1", key = "k1"))
        db.ledgerDao().insertAll(listOf(entry(id = "e1", sessionId = "s1", amount = 35_000)))
        val saved = db.ledgerDao().getById("e1")!!
        assertEquals(35_000L, saved.amountVnd)
        assertEquals(EntryType.EXPENSE, saved.type)
        assertEquals("FOOD_COFFEE", saved.categoryId)
    }

    @Test fun ledgerEntryWithUnknownCategoryIsRejected() = runTest {
        try {
            db.ledgerDao().insertAll(listOf(entry(id = "e1", sessionId = null, amount = 1, categoryId = "NOT_A_CATEGORY")))
            fail("expected a foreign key failure")
        } catch (expected: SQLiteConstraintException) {
            // Categories must be stable known IDs, never free-form names (PRD §8).
        }
        assertNull(db.ledgerDao().getById("e1"))
    }

    private fun session(id: String, key: String) = VoiceSessionEntity(
        id = id,
        createdAtUtc = NOW,
        updatedAtUtc = NOW,
        status = VoiceSessionStatus.PREVIEW,
        transcript = "Mua cà phê 35 nghìn",
        recognizerProvider = "fake",
        errorCode = null,
        linkedBankTransactionId = null,
        idempotencyKey = key,
        retentionExpiresAt = null,
    )

    private fun entry(id: String, sessionId: String?, amount: Long, categoryId: String = "FOOD_COFFEE") = LedgerEntryEntity(
        id = id,
        batchId = null,
        voiceSessionId = sessionId,
        type = EntryType.EXPENSE,
        amountVnd = amount,
        categoryId = categoryId,
        description = "Cà phê",
        note = null,
        paymentMethod = PaymentMethod.UNKNOWN,
        occurredAtUtc = NOW,
        status = EntryStatus.CONFIRMED,
        source = EntrySource.VOICE,
        createdAtUtc = NOW,
        updatedAtUtc = NOW,
    )

    private companion object {
        const val NOW = 1_764_000_000_000L
    }
}
