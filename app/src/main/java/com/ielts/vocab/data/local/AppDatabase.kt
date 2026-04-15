package com.ielts.vocab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ielts.vocab.data.local.dao.AssociationDao
import com.ielts.vocab.data.local.dao.DailyStatDao
import com.ielts.vocab.data.local.dao.LearningRecordDao
import com.ielts.vocab.data.local.dao.WordDao
import com.ielts.vocab.data.local.entity.AssociationEntity
import com.ielts.vocab.data.local.entity.DailyStatEntity
import com.ielts.vocab.data.local.entity.LearningRecordEntity
import com.ielts.vocab.data.local.entity.WordEntity

@Database(
    entities = [
        WordEntity::class,
        LearningRecordEntity::class,
        AssociationEntity::class,
        DailyStatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun learningRecordDao(): LearningRecordDao
    abstract fun associationDao(): AssociationDao
    abstract fun dailyStatDao(): DailyStatDao
}
