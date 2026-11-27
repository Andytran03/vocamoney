package com.vocamoney.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vocamoney.domain.model.EntrySource
import com.vocamoney.domain.model.EntryStatus
import com.vocamoney.domain.model.EntryType
import com.vocamoney.domain.model.PaymentMethod

/**
 * The ledger is the financial source of truth (PRD §7). Money is whole dong in a Long,
 * time is a UTC instant in epoch milliseconds.
 */
@Entity(
    tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = VoiceSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["voice_session_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("voice_session_id"),
        Index("category_id"),
        Index("batch_id"),
        Index(value = ["status", "occurred_at_utc"]),
    ],
)
data class LedgerEntryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "batch_id") val batchId: String?,
    @ColumnInfo(name = "voice_session_id") val voiceSessionId: String?,
    val type: EntryType,
    @ColumnInfo(name = "amount_vnd") val amountVnd: Long,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    val description: String,
    val note: String?,
    @ColumnInfo(name = "payment_method") val paymentMethod: PaymentMethod,
    @ColumnInfo(name = "occurred_at_utc") val occurredAtUtc: Long,
    val status: EntryStatus,
    val source: EntrySource,
    @ColumnInfo(name = "created_at_utc") val createdAtUtc: Long,
    @ColumnInfo(name = "updated_at_utc") val updatedAtUtc: Long,
)
