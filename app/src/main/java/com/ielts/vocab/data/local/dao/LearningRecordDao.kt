package com.ielts.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ielts.vocab.data.local.entity.LearningRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: LearningRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<LearningRecordEntity>)

    @Update
    suspend fun update(record: LearningRecordEntity)

    @Query("SELECT * FROM learning_records WHERE wordId = :wordId")
    suspend fun getByWordId(wordId: Long): LearningRecordEntity?

    @Query("SELECT * FROM learning_records WHERE wordId = :wordId")
    fun getByWordIdFlow(wordId: Long): Flow<LearningRecordEntity?>

    @Query("SELECT * FROM learning_records WHERE nextReviewDate <= :today ORDER BY nextReviewDate")
    fun getDueReviews(today: String): Flow<List<LearningRecordEntity>>

    @Query("SELECT COUNT(*) FROM learning_records WHERE nextReviewDate <= :today")
    fun getDueReviewCount(today: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_records WHERE masteryLevel = 3")
    fun getMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_records")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_records WHERE lastReviewDate = :date")
    fun getReviewedOnDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_records WHERE nextReviewDate <= :today AND masteryLevel < 3")
    fun getPendingReviewCount(today: String): Flow<Int>

    @Query("SELECT * FROM learning_records WHERE lastReviewDate = :date")
    fun getRecordsByDate(date: String): Flow<List<LearningRecordEntity>>
}
