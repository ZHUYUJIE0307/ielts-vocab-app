package com.ielts.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ielts.vocab.data.local.entity.DailyStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: DailyStatEntity)

    @Update
    suspend fun update(stat: DailyStatEntity)

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getByDate(date: String): DailyStatEntity?

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getByDateFlow(date: String): Flow<DailyStatEntity?>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT :limit")
    fun getRecentStats(limit: Int): Flow<List<DailyStatEntity>>

    @Query("SELECT COUNT(*) FROM daily_stats WHERE newWordsCount > 0 OR reviewWordsCount > 0")
    fun getActiveDaysCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_stats WHERE newWordsCount > 0 OR reviewWordsCount > 0")
    suspend fun getStreakCount(): Int

    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<DailyStatEntity>>
}
