package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
internal data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val image: String,
    val name: String,
    val amountInKopecks: Long
)
