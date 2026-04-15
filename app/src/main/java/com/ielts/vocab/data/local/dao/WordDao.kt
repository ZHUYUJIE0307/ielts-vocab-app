package com.ielts.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ielts.vocab.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>): List<Long>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<WordEntity?>

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getByWord(word: String): WordEntity?

    @Query("SELECT * FROM words WHERE wordListIndex = :listIndex ORDER BY id")
    fun getByWordList(listIndex: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word LIKE :query || '%' ORDER BY word LIMIT 50")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY id")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM words")
    fun getTotalCountFlow(): Flow<Int>

    @Query("SELECT * FROM words WHERE id NOT IN (SELECT wordId FROM learning_records) ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getUnlearnedWords(limit: Int, offset: Int): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words WHERE id NOT IN (SELECT wordId FROM learning_records)")
    fun getUnlearnedCountFlow(): Flow<Int>

    @Query("SELECT DISTINCT wordListIndex FROM words ORDER BY wordListIndex")
    fun getWordListIndices(): Flow<List<Int>>

    @Query("SELECT COUNT(*) FROM words WHERE wordListIndex = :listIndex")
    suspend fun getCountByWordList(listIndex: Int): Int
}
