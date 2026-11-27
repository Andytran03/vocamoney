package com.vocamoney.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vocamoney.domain.model.VoiceSessionStatus

/**
 * One tap-to-talk session (PRD §8). Raw audio is never stored, only the transcript.
 * The unique idempotency key stops a retried session from saving twice (BR-04).
 */
@Entity(
    tableName = "voice_sessions",
    indices = [Index(value = ["idempotency_key"], unique = true)],
)
data class VoiceSessionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "created_at_utc") val createdAtUtc: Long,
    @ColumnInfo(name = "updated_at_utc") val updatedAtUtc: Long,
    val status: VoiceSessionStatus,
    val transcript: String?,
    @ColumnInfo(name = "recognizer_provider") val recognizerProvider: String,
    @ColumnInfo(name = "error_code") val errorCode: String?,
    // No foreign key yet: bank_transactions arrives in slice L7.
    @ColumnInfo(name = "linked_bank_transaction_id") val linkedBankTransactionId: String?,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "retention_expires_at") val retentionExpiresAt: Long?,
)
