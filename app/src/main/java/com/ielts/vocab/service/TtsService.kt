package com.ielts.vocab.service

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface TtsService {
    fun speak(word: String)
    fun stop()
    fun setSpeechRate(rate: Float)
    fun isReady(): Boolean
}

@Singleton
class TtsServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TtsService, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingWord: String? = null
    private var speechRate = 1.0f

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setSpeechRate(speechRate)
            isInitialized = true
            pendingWord?.let { speak(it) }
            pendingWord = null
        }
    }

    override fun speak(word: String) {
        if (isInitialized) {
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_${System.currentTimeMillis()}")
        } else {
            pendingWord = word
        }
    }

    override fun stop() {
        tts?.stop()
    }

    override fun setSpeechRate(rate: Float) {
        speechRate = rate
        tts?.setSpeechRate(rate)
    }

    override fun isReady(): Boolean = isInitialized

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
    }
}
