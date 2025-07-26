package com.example.speechtranscriber.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.speechtranscriber.model.SpeechRecognizerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    // Texto temporal que se muestra en tiempo real durante la transcripción
    private val _temporaryTranscription = mutableStateOf("")
    val temporaryTranscription: State<String> = _temporaryTranscription

    // Texto permanente que se acumula con las transcripciones completadas
    private val _permanentTranscription = mutableStateOf("")
    val permanentTranscription: State<String> = _permanentTranscription

    //Estado de la escucha
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    // Estado para controlar si se tienen permisos
    private val _hasAudioPermission = mutableStateOf(false)
    val hasAudioPermission: State<Boolean> = _hasAudioPermission

    private var recognizerHelper: SpeechRecognizerHelper? = null

    // Método para actualizar el texto temporal en tiempo real
    fun updateTemporaryTranscription(text: String) {
        _temporaryTranscription.value = text
    }

    // Método para añadir texto al área temporal durante la transcripción
    fun appendTemporaryTranscription(text: String) {
        val currentTranscription = _temporaryTranscription.value
        _temporaryTranscription.value = if(currentTranscription.isBlank()) text else "$currentTranscription $text"
    }

    // Método para limpiar el texto temporal
    fun clearTemporaryTranscription() {
        _temporaryTranscription.value = ""
    }

    // Método para finalizar la transcripción (mover temporal a permanente)
    fun finalizeTranscription() {
        if (_temporaryTranscription.value.isNotBlank()) {
            val currentPermanent = _permanentTranscription.value
            val newText = _temporaryTranscription.value
            
            _permanentTranscription.value = if (currentPermanent.isBlank()) {
                newText
            } else {
                "$currentPermanent\n\n$newText"
            }
            
            _temporaryTranscription.value = ""
        }
    }

    // Método para cancelar la transcripción (borrar temporal sin mover a permanente)
    fun cancelTranscription() {
        _temporaryTranscription.value = ""
    }

    // Método para limpiar todo el texto permanente
    fun clearPermanentTranscription() {
        _permanentTranscription.value = ""
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

    // Método para actualizar el estado de permisos
    fun updateAudioPermission(hasPermission: Boolean) {
        _hasAudioPermission.value = hasPermission
    }
}
