package com.vocamoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vocamoney.data.local.entity.LedgerEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(entries: List<LedgerEntryEntity>)

    @Query("SELECT * FROM ledger_entries WHERE status = 'CONFIRMED' ORDER BY occurred_at_utc DESC")
    fun observeConfirmed(): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM ledger_entries WHERE id = :id")
    suspend fun getById(id: String): LedgerEntryEntity?
}
