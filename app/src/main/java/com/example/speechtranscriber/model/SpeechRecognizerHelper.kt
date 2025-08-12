package com.example.speechtranscriber.model

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.speechtranscriber.utils.NumberConverter
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
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false) // Deshabilitamos resultados parciales para evitar duplicaciones
        // Configuraciones básicas
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }
    private var listener: RecognitionListener? = null
    private var isListening = false
    private var shouldContinueListening = false // Controla si debe continuar escuchando
    private var handler = Handler(Looper.getMainLooper())
    private var restartRunnable: Runnable? = null
    private var lastRecognizedText = "" // Para evitar duplicaciones
    private var lastRecognitionTime = 0L // Timestamp del último reconocimiento
    private val DUPLICATE_TIMEOUT_MS = 2000L // 2 segundos para permitir repeticiones

    init {
        initListener()
        speechRecognizer.setRecognitionListener(listener)
    }

    private fun initListener(){
        listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "✅ Ready for speech - Listening started")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "🎤 Speech started - User is speaking")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Log del nivel de audio para diagnóstico
                if (rmsdB > 0) {
                    Log.d("SpeechRecognizer", "📊 Audio level: $rmsdB dB")
                }
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizer", "📦 Buffer received")
            }
            
            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "🔚 Speech ended - User stopped speaking")
                isListening = false
                // Reiniciar automáticamente si debe continuar escuchando
                if (shouldContinueListening) {
                    restartListeningAfterDelay()
                }
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
                Log.e("SpeechRecognizer", "❌ Error occurred: $message (code: $error)")
                
                // Solo reiniciar automáticamente para ciertos errores y si debe continuar escuchando
                if (shouldContinueListening && 
                    (error == SpeechRecognizer.ERROR_NO_MATCH || 
                     error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
                    Log.d("SpeechRecognizer", "🔄 Auto-restarting after error: $message")
                    restartListeningAfterDelay()
                } else {
                    onError(message)
                }
            }

            override fun onResults(results: Bundle) {
                isListening = false
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { recognizedText ->
                    val currentTime = System.currentTimeMillis()
                    val timeSinceLastRecognition = currentTime - lastRecognitionTime
                    
                    // Verificar si es un duplicado reciente
                    val isDuplicate = recognizedText.trim() == lastRecognizedText.trim() && 
                                     timeSinceLastRecognition < DUPLICATE_TIMEOUT_MS
                    
                    if (!isDuplicate && recognizedText.trim().isNotBlank()) {
                        // Procesar números hablados antes de enviar el resultado
                        val processedText = NumberConverter.convertSpokenNumbersToDigits(recognizedText.trim())
                        
                        Log.d("SpeechRecognizer", "✅ Final result: '$recognizedText' -> '$processedText' (time since last: ${timeSinceLastRecognition}ms)")
                        lastRecognizedText = recognizedText.trim()
                        lastRecognitionTime = currentTime
                        onResult(processedText)
                    } else if (isDuplicate) {
                        Log.d("SpeechRecognizer", "⚠️ Skipping duplicate result: '$recognizedText' (time since last: ${timeSinceLastRecognition}ms)")
                    } else {
                        Log.d("SpeechRecognizer", "⚠️ Skipping empty result")
                    }
                } ?: run {
                    Log.w("SpeechRecognizer", "⚠️ No results found in bundle")
                }
                
                // Reiniciar automáticamente si debe continuar escuchando
                if (shouldContinueListening) {
                    restartListeningAfterDelay()
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // Deshabilitamos los resultados parciales para evitar duplicaciones
                // Solo procesamos resultados finales
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "📅 Event received: $eventType")
            }
        }
    }

    private fun restartListeningAfterDelay() {
        // Cancelar cualquier reinicio pendiente
        restartRunnable?.let { handler.removeCallbacks(it) }
        
        restartRunnable = Runnable {
            if (shouldContinueListening && !isListening) {
                Log.d("SpeechRecognizer", "🔄 Auto-restarting recognition...")
                startListening()
            }
        }
        
        // Reiniciar después de 500ms
        handler.postDelayed(restartRunnable!!, 500)
    }

    fun startListening() {
        if (isListening) {
            Log.d("SpeechRecognizer", "⚠️ Already listening, ignoring start request")
            return
        }
        try {
            Log.d("SpeechRecognizer", "🚀 Starting speech recognition...")
            shouldContinueListening = true // Marcar que debe continuar escuchando
            lastRecognizedText = "" // Resetear el último texto reconocido
            lastRecognitionTime = 0L // Resetear el timestamp
            speechRecognizer.cancel() // limpiamos cualquier estado anterior
            speechRecognizer.startListening(recognitionIntent)
            isListening = true
            Log.d("SpeechRecognizer", "✅ Recognition started successfully")
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "❌ Error al iniciar: ${e.message}")
            onError("No se pudo iniciar el reconocimiento: ${e.message}")
            isListening = false
            shouldContinueListening = false
        }
    }

    fun stopListening() {
        if (!isListening && !shouldContinueListening) {
            Log.d("SpeechRecognizer", "⚠️ Not listening, ignoring stop request")
            return
        }
        try {
            Log.d("SpeechRecognizer", "🛑 Stopping speech recognition...")
            shouldContinueListening = false // Marcar que NO debe continuar escuchando
            restartRunnable?.let { handler.removeCallbacks(it) } // Cancelar reinicios pendientes
            speechRecognizer.stopListening()
            Log.d("SpeechRecognizer", "✅ Recognition stopped successfully")
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "❌ Error al detener: ${e.message}")
        } finally {
            isListening = false
        }
    }

    fun destroy(){
        Log.d("SpeechRecognizer", "🗑️ Destroying speech recognizer")
        shouldContinueListening = false
        isListening = false
        restartRunnable?.let { handler.removeCallbacks(it) }
        try {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
            Log.d("SpeechRecognizer", "✅ Speech recognizer destroyed successfully")
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "❌ Error al destruir: ${e.message}")
        }
    }
}