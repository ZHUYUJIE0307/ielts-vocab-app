package com.ielts.vocab

import android.app.Application
import android.util.Log
import com.ielts.vocab.data.importer.WordDataImporter
import com.ielts.vocab.di.SettingsHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class IeltsApp : Application() {

    @Inject lateinit var wordDataImporter: WordDataImporter
    @Inject lateinit var settingsHelper: SettingsHelper

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d("IeltsApp", "Application starting...")
        importDataIfNeeded()
    }

    private fun importDataIfNeeded() {
        appScope.launch {
            try {
                val imported = settingsHelper.isDataImported()
                if (!imported) {
                    Log.d("IeltsApp", "Importing data...")
                    wordDataImporter.importIfNeeded(false)
                    settingsHelper.setDataImported()
                    Log.d("IeltsApp", "Data imported successfully")
                } else {
                    Log.d("IeltsApp", "Data already imported")
                }
            } catch (e: Exception) {
                Log.e("IeltsApp", "Error importing data", e)
                // Continue anyway - app can still work
            }
        }
    }
}
