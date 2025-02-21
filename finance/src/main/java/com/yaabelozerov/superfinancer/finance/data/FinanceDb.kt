package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GoalEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
internal abstract class FinanceDb: RoomDatabase() {
    abstract fun dao(): FinanceDao
    abstract fun statsDao(): StatsDao
}