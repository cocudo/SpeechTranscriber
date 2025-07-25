package com.example.speechtranscriber.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.speechtranscriber.model.SpeechRecognizerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    // mutable state flow para el texto reconocido por voz
    private val _transcription = mutableStateOf("Pulsa el botón para comenzar a dictar")
    val transcription: State<String> = _transcription

    //Estado de la escucha
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    private var recognizerHelper: SpeechRecognizerHelper? = null

    //Metodo para actualizar el texto reconocido por voz
    fun updateTranscription(text: String) {
        _transcription.value = text
    }

    //añadimos una funcion para acumular el texto reconocido por voz
    fun appendTranscription(text: String) {
        val currentTranscription = _transcription.value
        _transcription.value += if(currentTranscription.isBlank()) text else "$currentTranscription $text"
    }

    fun clearTranscription() {
        _transcription.value = ""
    }

    fun onListeningStopped() {
        _isListening.value = false
    }

    fun setSpeecRecognizer(helper: SpeechRecognizerHelper) {
        this.recognizerHelper = helper
    }

    fun onListeningStarted() {
        _isListening.value = true
    }
}
