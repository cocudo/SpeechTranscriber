package com.example.speechtranscriber.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    onNavigateToTranscription: () -> Unit,
    onNavigateToSavedSessions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp)
    ) {
        // Header del drawer
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        
        // Opciones del menú
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text(stringResource(R.string.menu_transcription)) },
            selected = false,
            onClick = onNavigateToTranscription,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text(stringResource(R.string.menu_saved_sessions)) },
            selected = false,
            onClick = onNavigateToSavedSessions,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.menu_settings)) },
            selected = false,
            onClick = onNavigateToSettings,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Footer del drawer
        Divider()
        Text(
            text = "Speech Transcriber v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
} 