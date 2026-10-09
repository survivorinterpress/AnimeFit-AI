package com.example.data.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.0f)
                isInitialized = true
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun speak(text: String, coachId: String = "ren") {
        if (!isInitialized || text.isBlank()) return
        
        // Coach voice differentiation
        if (coachId == "ren") {
            tts?.setPitch(1.15f) // energetic, higher shonen pitch
            tts?.setSpeechRate(1.08f) // faster, pumped up
        } else {
            tts?.setPitch(0.95f) // cool, calm, tactical pitch
            tts?.setSpeechRate(0.98f) // measured and sharp
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "anime_coach_speech")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
