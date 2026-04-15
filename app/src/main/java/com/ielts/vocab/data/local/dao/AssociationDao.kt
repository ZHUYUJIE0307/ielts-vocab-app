package com.ielts.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ielts.vocab.data.local.entity.AssociationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssociationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(associations: List<AssociationEntity>)

    @Query("SELECT * FROM associations WHERE wordId = :wordId")
    suspend fun getByWordId(wordId: Long): AssociationEntity?

    @Query("SELECT * FROM associations WHERE wordId = :wordId")
    fun getByWordIdFlow(wordId: Long): Flow<AssociationEntity?>
}
