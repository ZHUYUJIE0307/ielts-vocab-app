package com.ielts.vocab.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "learning_records",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wordId"], unique = true),
        Index(value = ["nextReviewDate"]),
        Index(value = ["lastReviewDate"])
    ]
)
data class LearningRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val reviewStage: Int = 0,
    val nextReviewDate: String,
    val lastReviewDate: String,
    val masteryLevel: Int = 0,  // 0=新词, 1=学习中, 2=复习中, 3=已掌握
    val totalReviews: Int = 0,
    val correctCount: Int = 0
)
