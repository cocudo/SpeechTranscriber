package com.example.speechtranscriber.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportService: ExportService
) {
    
    fun exportTranscription(content: String, title: String? = null): Boolean {
        return try {
            // Limpiar archivos antiguos
            exportService.cleanupOldFiles()
            
            // Crear archivo TXT
            val fileUri = exportService.createTxtFile(content, title)
            
            if (fileUri != null) {
                // Lanzar intent de compartir
                launchShareIntent(fileUri)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun launchShareIntent(fileUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, "Transcripción de voz")
            putExtra(Intent.EXTRA_TEXT, "Transcripción generada con SpeechTranscriber")
            
            // Añadir flags para permitir que otras apps lean el archivo
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        // Lanzar el selector de apps
        val chooserIntent = Intent.createChooser(shareIntent, "Compartir transcripción")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
} 