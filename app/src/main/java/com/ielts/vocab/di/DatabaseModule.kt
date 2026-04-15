package com.ielts.vocab.di

import android.content.Context
import androidx.room.Room
import com.ielts.vocab.data.local.AppDatabase
import com.ielts.vocab.data.local.dao.AssociationDao
import com.ielts.vocab.data.local.dao.DailyStatDao
import com.ielts.vocab.data.local.dao.LearningRecordDao
import com.ielts.vocab.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ielts_vocab_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao = database.wordDao()

    @Provides
    fun provideLearningRecordDao(database: AppDatabase): LearningRecordDao = database.learningRecordDao()

    @Provides
    fun provideAssociationDao(database: AppDatabase): AssociationDao = database.associationDao()

    @Provides
    fun provideDailyStatDao(database: AppDatabase): DailyStatDao = database.dailyStatDao()
}
