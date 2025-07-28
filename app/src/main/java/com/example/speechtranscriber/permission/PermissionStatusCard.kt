package com.example.speechtranscriber.permission

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.R

@Composable
fun PermissionStatusCard(
    state: PermissionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val (color, message) = when (state) {
        is PermissionState.Granted -> Pair(
            colorResource(id = R.color.permission_granted),
            stringResource(id = R.string.permission_granted)
        )
        is PermissionState.Denied -> Pair(
            colorResource(id = R.color.permission_denied),
            stringResource(id = R.string.permission_denied)
        )
        is PermissionState.PermanentlyDenied -> Pair(
            colorResource(id = R.color.permission_denied),
            stringResource(id = R.string.permission_permanently_denied)
        )
        is PermissionState.NotRequested -> Pair(
            colorResource(id = R.color.permission_info),
            stringResource(id = R.string.permission_microphone_description)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.permission_microphone_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            // Icono de ayuda con tooltip
            var showTooltip by remember { mutableStateOf(false) }
            IconButton(onClick = { showTooltip = !showTooltip }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_help),
                    contentDescription = "Ayuda"
                )
            }
            DropdownMenu(
                expanded = showTooltip,
                onDismissRequest = { showTooltip = false }
            ) {
                Text(
                    text = stringResource(id = R.string.permission_info_tooltip),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            when (state) {
                is PermissionState.Denied, is PermissionState.NotRequested -> {
                    Button(onClick = onRequestPermission) {
                        Text(text = stringResource(id = R.string.permission_request))
                    }
                }
                is PermissionState.PermanentlyDenied -> {
                    Button(onClick = onOpenSettings) {
                        Text(text = stringResource(id = R.string.permission_open_settings))
                    }
                }
                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
} 