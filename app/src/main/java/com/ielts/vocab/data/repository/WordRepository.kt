package com.ielts.vocab.data.repository

import com.ielts.vocab.data.local.dao.AssociationDao
import com.ielts.vocab.data.local.dao.WordDao
import com.ielts.vocab.data.local.entity.AssociationEntity
import com.ielts.vocab.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao,
    private val associationDao: AssociationDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    fun getWordById(id: Long): Flow<WordEntity?> = wordDao.getByIdFlow(id)

    suspend fun getWordByIdSync(id: Long): WordEntity? = wordDao.getById(id)

    fun getByWordList(listIndex: Int): Flow<List<WordEntity>> = wordDao.getByWordList(listIndex)

    fun searchWords(query: String): Flow<List<WordEntity>> = wordDao.searchWords(query)

    fun getTotalCount(): Flow<Int> = wordDao.getTotalCountFlow()

    suspend fun getTotalCountSync(): Int = wordDao.getTotalCount()

    fun getUnlearnedCount(): Flow<Int> = wordDao.getUnlearnedCountFlow()

    suspend fun getUnlearnedWords(limit: Int, offset: Int): List<WordEntity> =
        wordDao.getUnlearnedWords(limit, offset)

    fun getWordListIndices(): Flow<List<Int>> = wordDao.getWordListIndices()

    suspend fun getCountByWordList(listIndex: Int): Int = wordDao.getCountByWordList(listIndex)

    fun getAssociation(wordId: Long): Flow<AssociationEntity?> = associationDao.getByWordIdFlow(wordId)

    suspend fun getAssociationSync(wordId: Long): AssociationEntity? = associationDao.getByWordId(wordId)
}
