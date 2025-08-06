package com.example.speechtranscriber.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechtranscriber.model.SpeechRecognizerHelper
import com.example.speechtranscriber.permission.PermissionManager
import com.example.speechtranscriber.permission.PermissionState
import com.example.speechtranscriber.export.ExportManager
import com.example.speechtranscriber.repository.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.speechtranscriber.data.TranscriptionEntity

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val exportManager: ExportManager,
    private val transcriptionRepository: TranscriptionRepository
) : ViewModel() {

    // Texto temporal que se muestra en tiempo real durante la transcripción
    private val _temporaryTranscription = mutableStateOf("")
    val temporaryTranscription: State<String> = _temporaryTranscription

    // Texto permanente que se acumula con las transcripciones completadas
    private val _permanentTranscription = mutableStateOf("")
    val permanentTranscription: State<String> = _permanentTranscription

    //Estado de la escucha
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    private var recognizerHelper: SpeechRecognizerHelper? = null

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.NotRequested)
    val permissionState: StateFlow<PermissionState> = _permissionState

    // Estado para el guardado de sesión
    private val _isSavingSession = mutableStateOf(false)
    val isSavingSession: State<Boolean> = _isSavingSession

    private val _saveSessionMessage = mutableStateOf("")
    val saveSessionMessage: State<String> = _saveSessionMessage

    // Estado para las sesiones guardadas
    private val _savedSessions = mutableStateOf<List<TranscriptionEntity>>(emptyList())
    val savedSessions: State<List<TranscriptionEntity>> = _savedSessions

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

    fun checkMicrophonePermission() {
        viewModelScope.launch {
            _permissionState.value = permissionManager.checkMicrophonePermission()
        }
    }

    fun updatePermissionState(state: PermissionState) {
        _permissionState.value = state
    }

    fun openAppSettings(context: Context) {
        permissionManager.openAppSettings(context)
    }
    
    fun exportTranscription(): Boolean {
        return if (_permanentTranscription.value.isNotBlank()) {
            exportManager.exportTranscription(_permanentTranscription.value)
        } else {
            false
        }
    }

    // Método para guardar la sesión actual en la base de datos
    fun saveSession() {
        if (_permanentTranscription.value.isBlank()) {
            _saveSessionMessage.value = "No hay contenido para guardar"
            return
        }

        viewModelScope.launch {
            try {
                _isSavingSession.value = true
                _saveSessionMessage.value = ""
                
                val sessionId = transcriptionRepository.saveTranscription(
                    content = _permanentTranscription.value
                )
                
                _saveSessionMessage.value = "Sesión guardada correctamente (ID: $sessionId)"
            } catch (e: Exception) {
                _saveSessionMessage.value = "Error al guardar la sesión: ${e.message}"
            } finally {
                _isSavingSession.value = false
            }
        }
    }

    // Método para limpiar el mensaje de guardado
    fun clearSaveSessionMessage() {
        _saveSessionMessage.value = ""
    }

    // Método para cargar las sesiones guardadas
    fun loadSavedSessions() {
        viewModelScope.launch {
            try {
                transcriptionRepository.getAllTranscriptions().collect { sessions ->
                    _savedSessions.value = sessions
                }
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }
}
