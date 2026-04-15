package com.ielts.vocab.data.repository

import com.ielts.vocab.algorithm.EbbinghausScheduler
import com.ielts.vocab.data.local.dao.DailyStatDao
import com.ielts.vocab.data.local.dao.LearningRecordDao
import com.ielts.vocab.data.local.entity.DailyStatEntity
import com.ielts.vocab.data.local.entity.LearningRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningRepository @Inject constructor(
    private val learningRecordDao: LearningRecordDao,
    private val dailyStatDao: DailyStatDao
) {
    fun getDueReviews(today: String): Flow<List<LearningRecordEntity>> =
        learningRecordDao.getDueReviews(today)

    fun getDueReviewCount(today: String): Flow<Int> =
        learningRecordDao.getDueReviewCount(today)

    fun getMasteredCount(): Flow<Int> = learningRecordDao.getMasteredCount()

    fun getLearnedCount(): Flow<Int> = learningRecordDao.getLearnedCount()

    fun getRecordByWordId(wordId: Long): Flow<LearningRecordEntity?> =
        learningRecordDao.getByWordIdFlow(wordId)

    suspend fun getRecordByWordIdSync(wordId: Long): LearningRecordEntity? =
        learningRecordDao.getByWordId(wordId)

    fun getPendingReviewCount(today: String): Flow<Int> =
        learningRecordDao.getPendingReviewCount(today)

    fun getRecentStats(limit: Int): Flow<List<DailyStatEntity>> =
        dailyStatDao.getRecentStats(limit)

    fun getActiveDaysCount(): Flow<Int> = dailyStatDao.getActiveDaysCount()

    suspend fun markWordLearned(wordId: Long, knownLevel: Int) {
        val today = LocalDate.now().toString()
        val existing = learningRecordDao.getByWordId(wordId)

        if (existing == null) {
            val stage = if (knownLevel >= 2) 1 else 0
            val mastery = when (knownLevel) {
                0 -> 0  // 不认识
                1 -> 1  // 模糊
                else -> 2 // 认识
            }
            val record = LearningRecordEntity(
                wordId = wordId,
                reviewStage = stage,
                nextReviewDate = EbbinghausScheduler.calculateNextReviewDate(stage),
                lastReviewDate = today,
                masteryLevel = mastery,
                totalReviews = 1,
                correctCount = if (knownLevel >= 2) 1 else 0
            )
            learningRecordDao.insert(record)
        } else {
            val newStage = if (knownLevel >= 2) {
                minOf(existing.reviewStage + 1, EbbinghausScheduler.MAX_STAGE)
            } else {
                0
            }
            val newMastery = when {
                newStage >= 5 -> 3  // 已掌握
                knownLevel == 0 -> 0  // 不认识，重置
                else -> 2  // 复习中
            }
            val updated = existing.copy(
                reviewStage = newStage,
                nextReviewDate = EbbinghausScheduler.calculateNextReviewDate(newStage),
                lastReviewDate = today,
                masteryLevel = newMastery,
                totalReviews = existing.totalReviews + 1,
                correctCount = existing.correctCount + if (knownLevel >= 2) 1 else 0
            )
            learningRecordDao.update(updated)
        }

        updateDailyStat(today, isNew = existing == null, isReview = existing != null)
    }

    private suspend fun updateDailyStat(date: String, isNew: Boolean, isReview: Boolean) {
        val existing = dailyStatDao.getByDate(date)
        if (existing == null) {
            dailyStatDao.insert(
                DailyStatEntity(
                    date = date,
                    newWordsCount = if (isNew) 1 else 0,
                    reviewWordsCount = if (isReview) 1 else 0
                )
            )
        } else {
            dailyStatDao.update(
                existing.copy(
                    newWordsCount = existing.newWordsCount + if (isNew) 1 else 0,
                    reviewWordsCount = existing.reviewWordsCount + if (isReview) 1 else 0
                )
            )
        }
    }

    suspend fun getStreakCount(): Int {
        var streak = 0
        var date = LocalDate.now()
        while (true) {
            val stat = dailyStatDao.getByDate(date.toString())
            if (stat != null && (stat.newWordsCount > 0 || stat.reviewWordsCount > 0)) {
                streak++
                date = date.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    fun getStatsByDate(date: String): Flow<DailyStatEntity?> =
        dailyStatDao.getByDateFlow(date)

    fun getAllStats(): Flow<List<DailyStatEntity>> = dailyStatDao.getAllStats()
}
