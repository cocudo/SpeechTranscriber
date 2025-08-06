package com.example.speechtranscriber.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.permission.PermissionState
import com.example.speechtranscriber.permission.PermissionStatusCard
import com.example.speechtranscriber.export.ExportButton
import com.example.speechtranscriber.viewmodel.MainViewModel
import androidx.compose.ui.res.stringResource
import com.example.speechtranscriber.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCancelTranscription: () -> Unit,
    onExport: () -> Unit,
    onSaveSession: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val temporaryTranscription by viewModel.temporaryTranscription
    val permanentTranscription by viewModel.permanentTranscription
    val isListening by viewModel.isListening
    val permissionState by viewModel.permissionState.collectAsState()
    
    // Estados para guardar sesión
    val isSavingSession by viewModel.isSavingSession
    val saveSessionMessage by viewModel.saveSessionMessage
    
    // Estados para la sesión actual
    val currentSessionId by viewModel.currentSessionId
    val isEditingSession by viewModel.isEditingSession
    
    // Estado para el título de la sesión
    val sessionTitle by viewModel.sessionTitle

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.menu_transcription),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.menu_drawer)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
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
                // Campo de título de la sesión
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Título de la sesión:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = sessionTitle,
                            onValueChange = { viewModel.updateSessionTitle(it) },
                            placeholder = {
                                Text("Introduce un título para esta sesión (opcional)")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

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

                // Área de texto permanente (acumulativo) - Aumentada aún más
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
                                .height(260.dp) // Aumentado de 200dp a 260dp
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
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                        }
                    }
                }

                // Controles de botones en layout horizontal
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
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isListening) {
                                    stringResource(R.string.button_stop_transcription)
                                } else {
                                    stringResource(R.string.button_start_transcription)
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        // Botones secundarios en layout horizontal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón de cancelar
                            OutlinedButton(
                                onClick = onCancelTranscription,
                                enabled = isListening || temporaryTranscription.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.button_cancel),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Botón de exportar
                            OutlinedButton(
                                onClick = onExport,
                                enabled = permanentTranscription.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.button_export),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Botón de guardar sesión
                            OutlinedButton(
                                onClick = onSaveSession,
                                enabled = permanentTranscription.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditingSession) {
                                        stringResource(R.string.button_save_session_edit)
                                    } else {
                                        stringResource(R.string.button_save_session)
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Mensaje de estado del guardado (si existe)
                        if (saveSessionMessage.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (saveSessionMessage.contains("Error")) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = saveSessionMessage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (saveSessionMessage.contains("Error")) {
                                            MaterialTheme.colorScheme.onErrorContainer
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    IconButton(
                                        onClick = { viewModel.clearSaveSessionMessage() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text(
                                            text = "×",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
