package com.example.speechtranscriber

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.speechtranscriber.model.SpeechRecognizerHelper
import com.example.speechtranscriber.permission.PermissionState
import com.example.speechtranscriber.ui.MainScreen
import com.example.speechtranscriber.ui.theme.SpeechTranscriberTheme
import com.example.speechtranscriber.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var speechHelper: SpeechRecognizerHelper
    private lateinit var requestAudioPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        speechHelper = SpeechRecognizerHelper(this,
            onResult = { text -> viewModel.appendTemporaryTranscription(text)},
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                //actualizacion de texto solo si es un error critico.
                if (error.contains("Permisos")|| error.contains("No se pudo iniciar")){
                    viewModel.updateTemporaryTranscription("Error: $error")
                }
            }
        )

        viewModel.setSpeecRecognizer(speechHelper)

        // Verificar permisos al iniciar
        checkAudioPermission()

        // Preparamos la solicitud de permiso
        requestAudioPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.updatePermissionState(PermissionState.Granted)
                // NO iniciamos automáticamente la escucha - el usuario debe pulsar el botón
            } else {
                viewModel.updatePermissionState(PermissionState.Denied)
                Toast.makeText(
                    this,
                    "Permiso de micrófono denegado",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        setContent {
            SpeechTranscriberTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onRequestPermission = {
                            requestAudioPermission.launch(android.Manifest.permission.RECORD_AUDIO)
                        },
                        onOpenSettings = {
                            viewModel.openAppSettings(this)
                        },
                        onStartListening = {
                            if (ContextCompat.checkSelfPermission(
                                    this, android.Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                speechHelper.startListening()
                                viewModel.onListeningStarted()
                            } else {
                                requestAudioPermission.launch(android.Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onStopListening = {
                            speechHelper.stopListening()
                            viewModel.onListeningStopped()
                            // Finalizar la transcripción moviendo el texto temporal al permanente
                            viewModel.finalizeTranscription()
                        },
                        onCancelTranscription = {
                            speechHelper.stopListening()
                            viewModel.onListeningStopped()
                            // Cancelar la transcripción borrando el texto temporal
                            viewModel.cancelTranscription()
                        }
                    )
                }
            }
        }
    }

    private fun checkAudioPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        
        // Usar solo el nuevo sistema de permisos
        val permissionState = if (hasPermission) {
            PermissionState.Granted
        } else {
            PermissionState.NotRequested
        }
        viewModel.updatePermissionState(permissionState)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechHelper.destroy()
    }
}