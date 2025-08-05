package com.example.speechtranscriber.export

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.speechtranscriber.R

@Composable
fun ExportButton(
    onExport: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onExport,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_export),
            contentDescription = "Exportar",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.export_transcription),
            style = MaterialTheme.typography.titleMedium
        )
    }
} 