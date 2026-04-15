package com.ielts.vocab.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsHelper(private val dataStore: DataStore<Preferences>) {
    private val KEY_DATA_IMPORTED = booleanPreferencesKey("data_imported")
    private val KEY_DAILY_GOAL = intPreferencesKey("daily_goal")
    private val KEY_SPEECH_RATE = intPreferencesKey("speech_rate")

    suspend fun isDataImported(): Boolean =
        dataStore.data.map { it[KEY_DATA_IMPORTED] ?: false }.first()

    suspend fun setDataImported() {
        dataStore.edit { it[KEY_DATA_IMPORTED] = true }
    }

    suspend fun getDailyGoal(): Int =
        dataStore.data.map { it[KEY_DAILY_GOAL] ?: 20 }.first()

    suspend fun setDailyGoal(goal: Int) {
        dataStore.edit { it[KEY_DAILY_GOAL] = goal }
    }

    suspend fun getSpeechRate(): Int =
        dataStore.data.map { it[KEY_SPEECH_RATE] ?: 100 }.first()

    suspend fun setSpeechRate(rate: Int) {
        dataStore.edit { it[KEY_SPEECH_RATE] = rate }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideSettingsHelper(dataStore: DataStore<Preferences>): SettingsHelper =
        SettingsHelper(dataStore)
}
