package com.vocamoney.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vocamoney.data.local.dao.CategoryDao
import com.vocamoney.data.local.dao.LedgerDao
import com.vocamoney.data.local.dao.VoiceSessionDao
import com.vocamoney.data.local.entity.CategoryEntity
import com.vocamoney.data.local.entity.LedgerEntryEntity
import com.vocamoney.data.local.entity.VoiceSessionEntity
import com.vocamoney.domain.model.CategorySeed

/** Schema v1 (slice L0): ledger entries, voice sessions and categories. */
@Database(
    entities = [LedgerEntryEntity::class, VoiceSessionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class VocaMoneyDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun voiceSessionDao(): VoiceSessionDao

    companion object {
        const val NAME = "vocamoney.db"

        fun build(context: Context): VocaMoneyDatabase =
            Room.databaseBuilder(context, VocaMoneyDatabase::class.java, NAME)
                .addCallback(SeedCallback)
                .build()
    }
}

/**
 * Seeds the system categories inside the database-creation transaction, so the
 * categories exist before any other query can run.
 */
object SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        CategorySeed.all.forEach { category ->
            val values = ContentValues().apply {
                put("id", category.id)
                putNull("parent_id")
                put("group_name_vi", category.parentNameVi)
                put("name_vi", category.nameVi)
                put("type", category.type.name)
                put("is_system", if (category.isSystem) 1 else 0)
                put("sort_order", category.sortOrder)
            }
            db.insert("categories", SQLiteDatabase.CONFLICT_IGNORE, values)
        }
    }
}
