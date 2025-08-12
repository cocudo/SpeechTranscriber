package com.example.speechtranscriber.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Close
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Botón de micrófono (estado inicial)
        AnimatedVisibility(
            visible = !isListening,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                animationSpec = tween(300),
                initialScale = 0.8f
            ),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(
                animationSpec = tween(300),
                targetScale = 0.8f
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
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
        
        // Botones de control (estado de grabación)
        AnimatedVisibility(
            visible = isListening,
            enter = slideInHorizontally(
                animationSpec = tween(500, easing = EaseOutCubic),
                initialOffsetX = { 0 } // Comienza desde la posición del micrófono
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                animationSpec = tween(500, easing = EaseInCubic),
                targetOffsetX = { 0 } // Regresa a la posición del micrófono
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
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
        
        // Botón de stop (aparece desde la posición del micrófono hacia la izquierda)
        AnimatedVisibility(
            visible = isListening,
            enter = slideInHorizontally(
                animationSpec = tween(500, easing = EaseOutCubic),
                initialOffsetX = { fullWidth -> fullWidth } // Comienza desde la posición del micrófono
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                animationSpec = tween(500, easing = EaseInCubic),
                targetOffsetX = { fullWidth -> fullWidth } // Regresa a la posición del micrófono
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
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