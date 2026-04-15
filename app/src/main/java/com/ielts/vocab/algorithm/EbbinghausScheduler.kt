package com.ielts.vocab.algorithm

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object EbbinghausScheduler {
    // Review intervals in days: 1, 2, 4, 7, 15, 30
    private val INTERVALS = intArrayOf(1, 2, 4, 7, 15, 30)
    const val MAX_STAGE = 5

    fun calculateNextReviewDate(stage: Int): String {
        val clampedStage = stage.coerceIn(0, MAX_STAGE)
        val intervalDays = INTERVALS[clampedStage]
        val nextDate = LocalDate.now().plusDays(intervalDays.toLong())
        return nextDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun getNextIntervalDays(stage: Int): Int {
        val clampedStage = stage.coerceIn(0, MAX_STAGE)
        return INTERVALS[clampedStage]
    }

    fun isDueForReview(nextReviewDate: String): Boolean {
        val today = LocalDate.now()
        val reviewDate = LocalDate.parse(nextReviewDate)
        return !reviewDate.isAfter(today)
    }

    fun getStageDescription(stage: Int): String {
        return when (stage) {
            0 -> "新词"
            1 -> "1天后复习"
            2 -> "2天后复习"
            3 -> "4天后复习"
            4 -> "7天后复习"
            5 -> "15天后复习"
            else -> "已掌握"
        }
    }

    fun getMasteryDescription(masteryLevel: Int): String {
        return when (masteryLevel) {
            0 -> "不认识"
            1 -> "学习中"
            2 -> "复习中"
            3 -> "已掌握"
            else -> "未知"
        }
    }
}
