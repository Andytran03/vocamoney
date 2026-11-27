package com.vocamoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vocamoney.data.local.entity.VoiceSessionEntity

@Dao
interface VoiceSessionDao {
    /** Fails on a duplicate idempotency key instead of silently replacing (BR-04). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: VoiceSessionEntity)

    @Query("SELECT * FROM voice_sessions WHERE idempotency_key = :key LIMIT 1")
    suspend fun findByIdempotencyKey(key: String): VoiceSessionEntity?
}
