package com.example.speechtranscriber.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButtons(
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCancelTranscription: () -> Unit,
    onExport: () -> Unit,
    onSaveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Botón de menú (izquierda) - solo visible cuando no está escuchando
        AnimatedVisibility(
            visible = !isListening,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.8f),
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Column {
                // Menú desplegable
                AnimatedVisibility(
                    visible = showMenu,
                    enter = slideInVertically(animationSpec = tween(300, easing = EaseOutCubic), initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(animationSpec = tween(300, easing = EaseInCubic), targetOffsetY = { it }) + fadeOut(animationSpec = tween(300)),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Botón Exportar
                        FloatingActionButton(
                            onClick = onExport,
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Exportar transcripción",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Botón Guardar
                        FloatingActionButton(
                            onClick = onSaveSession,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Guardar sesión",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Botón principal del menú
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menú de opciones",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        // Botón de micrófono (derecha) - solo visible cuando no está escuchando
        AnimatedVisibility(
            visible = !isListening,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.8f),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = onStartListening,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Iniciar transcripción",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Botón Cancelar (derecha) - solo visible cuando está escuchando
        AnimatedVisibility(
            visible = isListening,
            enter = slideInHorizontally(animationSpec = tween(500, easing = EaseOutCubic), initialOffsetX = { 0 }) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(animationSpec = tween(500, easing = EaseInCubic), targetOffsetX = { 0 }) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = onCancelTranscription,
                containerColor = Color.Red
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancelar transcripción",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Botón Stop (izquierda) - solo visible cuando está escuchando
        AnimatedVisibility(
            visible = isListening,
            enter = slideInHorizontally(animationSpec = tween(500, easing = EaseOutCubic), initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(animationSpec = tween(500, easing = EaseInCubic), targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = onStopListening,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Detener transcripción",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
} 