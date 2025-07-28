package com.example.speechtranscriber.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.permission.PermissionState
import com.example.speechtranscriber.permission.PermissionStatusCard
import com.example.speechtranscriber.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCancelTranscription: () -> Unit
) {
    val temporaryTranscription by viewModel.temporaryTranscription
    val permanentTranscription by viewModel.permanentTranscription
    val isListening by viewModel.isListening
    val permissionState by viewModel.permissionState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card de permisos si no está concedido
        if (permissionState != PermissionState.Granted) {
            PermissionStatusCard(
                state = permissionState,
                onRequestPermission = onRequestPermission,
                onOpenSettings = onOpenSettings
            )
        }

        // El resto de la UI solo si el permiso está concedido
        if (permissionState == PermissionState.Granted) {
            // Título de la aplicación
            Text(
                text = "Transcripción de Voz",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Área de texto temporal (en tiempo real)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Transcripción en tiempo real:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (temporaryTranscription.isBlank()) {
                                if (isListening) "Escuchando..." else "El texto transcrito aparecerá aquí en tiempo real"
                            } else {
                                temporaryTranscription
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (temporaryTranscription.isBlank()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            // Área de texto permanente (acumulativo)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Transcripción permanente:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (permanentTranscription.isBlank()) {
                                "Las transcripciones completadas se guardarán aquí"
                            } else {
                                permanentTranscription
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (permanentTranscription.isBlank()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                }
            }

            // Controles de botones
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón principal de Iniciar/Parar
                    Button(
                        onClick = {
                            if (isListening) {
                                onStopListening()
                            } else {
                                onStartListening()
                            }
                        },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isListening) "Parar Transcripción" else "Iniciar Transcripción",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Botón de cancelar
                    OutlinedButton(
                        onClick = onCancelTranscription,
                        enabled = isListening || temporaryTranscription.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancelar Transcripción",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
