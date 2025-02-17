package com.yaabelozerov.superfinancer.data.local.room.finance

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GoalEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class FinanceDb: RoomDatabase() {
    abstract fun dao(): FinanceDao
}