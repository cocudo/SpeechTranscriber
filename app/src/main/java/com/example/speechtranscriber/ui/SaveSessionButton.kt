package com.example.speechtranscriber.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.R

@Composable
fun SaveSessionButton(
    onSaveSession: () -> Unit,
    enabled: Boolean,
    isSaving: Boolean,
    message: String,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Botón de guardar sesión
        Button(
            onClick = onSaveSession,
            enabled = enabled && !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isSaving) {
                    stringResource(R.string.saving_session)
                } else {
                    stringResource(R.string.save_session)
                },
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Mensaje de estado del guardado
        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.contains("Error")) {
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
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.contains("Error")) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onClearMessage,
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