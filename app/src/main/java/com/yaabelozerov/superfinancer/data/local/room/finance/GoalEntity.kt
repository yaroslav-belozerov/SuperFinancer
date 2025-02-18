package com.yaabelozerov.superfinancer.data.local.room.finance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val image: String,
    val name: String,
    val amountInKopecks: Long
)
