package com.example.speechtranscriber.model

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechRecognizerHelper (
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
){
    private var speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private var recognitionIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // ✅ ya preparamos para los siguientes pasos
    }
    private var listener: RecognitionListener? = null
    private var isRestarting = false
    private var isListening = false

    init {
     initListener()
     speechRecognizer.setRecognitionListener(listener)
    }

    private fun initListener(){

        listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Speech ended")
            }

            override fun onError(error: Int) {
                isListening = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                    SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                    SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Timeout de red"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció ninguna voz"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                    SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera agotado"
                    else -> "Error desconocido: $error"
                }
                onError(message)

                if (error != SpeechRecognizer.ERROR_CLIENT &&
                    error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS &&
                    error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    Handler(context.mainLooper).postDelayed({
                        restartListening()
                    }, 1000)
                }
            }

            override fun onResults(results: Bundle) {
                isListening = false
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { recognizedText ->
                    onResult(recognizedText)
                }
                restartListening()
            }

            override fun onPartialResults(partialResults: Bundle) {
                val partial = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull().orEmpty()
                if (partial.isNotBlank()) {
                    onResult(partial)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun startListening() {
        if (isListening) return  // ya está escuchando, no hacer nada
        try {
            speechRecognizer.cancel() // limpiamos cualquier estado anterior
            Handler(context.mainLooper).postDelayed({
                speechRecognizer.startListening(recognitionIntent)
                isListening = true
                Log.d("SpeechRecognizer", "Iniciando reconocimiento")
            }, 300)
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error al iniciar: ${e.message}")
            onError("No se pudo iniciar el reconocimiento")
            isListening = false
        }
    }

    fun stopListening() {
        if (!isListening) return // no estaba escuchando
        try {
            speechRecognizer.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error al detener: ${e.message}")
        } finally {
            isListening = false
        }
    }

    fun destroy(){
        isListening = false
        isRestarting = false
        try {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error al destruir: ${e.message}")
        }
    }

    private fun restartListening() {
        if (isRestarting) return
        isRestarting = true

        stopListening()  // esta función ya actualiza isListening

        Handler(context.mainLooper).postDelayed({
            startListening()
            isRestarting = false
        }, 1000)
    }

}