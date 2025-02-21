package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class StatPair(val first: Int, val second: Long)
fun StatPair.toRublePair() = first to second.div(100L)

@Dao
interface StatsDao {
    @Query("SELECT COUNT(*) AS first, COALESCE(SUM(amountInKopecks), 0) AS second FROM goals WHERE enabled = 0")
    fun closedGoalsCountAndSum(): Flow<StatPair>

    @Query("SELECT COUNT(*) AS first, COALESCE(SUM(amountInKopecks), 0) AS second FROM goals WHERE enabled = 1")
    fun openGoalsCountAndSum(): Flow<StatPair>

    @Query("SELECT timestamp FROM transactions ORDER BY timestamp DESC LIMIT 1")
    fun getLastTransactionTimestamp(): Flow<Long?>

    @Query("SELECT timestamp FROM transactions ORDER BY timestamp ASC LIMIT 1")
    fun getFirstTransactionTimestamp(): Flow<Long?>
}