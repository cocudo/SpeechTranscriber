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
import com.example.speechtranscriber.ui.MainScreen
import com.example.speechtranscriber.ui.theme.SpeechTranscriberTheme
import com.example.speechtranscriber.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var speechHelper: SpeechRecognizerHelper
    private lateinit var requestAudioPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() //Comentamos el EdgeToEdge en prevision de usarlo más adelante cuando afinemos la interfaz.

        speechHelper = SpeechRecognizerHelper(this,
            onResult = { text -> viewModel.appendTranscription(text)},
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                //actualizacion de texto solo si es un error critico.
                if (error.contains("Permisos")|| error.contains("No se pudo iniciar")){
                    viewModel.updateTranscription("Error: $error")
                }
            }
        )

        viewModel.setSpeecRecognizer(speechHelper)

        // Preparamos la solicitud de permiso
        requestAudioPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.onListeningStarted()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de micrófono denegado",
                    Toast.LENGTH_LONG
                ).show()
                //viewModel.updateTranscription("Permiso de micrófono denegado.")
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
                        }
                    )

                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechHelper.destroy()
    }
}