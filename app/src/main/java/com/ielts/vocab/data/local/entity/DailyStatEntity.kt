package com.ielts.vocab.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_stats",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyStatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,           // yyyy-MM-dd
    val newWordsCount: Int = 0,
    val reviewWordsCount: Int = 0,
    val masteredCount: Int = 0
)
