package com.example.speechtranscriber.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun generateTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun createTxtFile(content: String, title: String? = null): Uri? {
        return try {
            // Crear directorio temporal si no existe
            val cacheDir = File(context.cacheDir, "exports")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            
            // Generar nombre del archivo con título o timestamp
            val fileName = if (!title.isNullOrBlank() && title != "Sesión de transcripción") {
                // Usar título limpio (sin caracteres especiales)
                val cleanTitle = title.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
                if (cleanTitle.isNotBlank()) {
                    "transcripcion_${cleanTitle.replace(" ", "_")}.txt"
                } else {
                    val timestamp = generateTimestamp()
                    "transcripcion_$timestamp.txt"
                }
            } else {
                val timestamp = generateTimestamp()
                "transcripcion_$timestamp.txt"
            }
            
            val file = File(cacheDir, fileName)
            
            // Escribir contenido al archivo
            FileWriter(file).use { writer ->
                writer.write(content)
            }
            
            // Generar URI usando FileProvider para compartir
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun cleanupOldFiles() {
        try {
            val cacheDir = File(context.cacheDir, "exports")
            if (cacheDir.exists()) {
                val files = cacheDir.listFiles()
                files?.forEach { file ->
                    // Eliminar archivos más antiguos de 24 horas
                    val currentTime = System.currentTimeMillis()
                    val fileAge = currentTime - file.lastModified()
                    val oneDayInMillis = 24 * 60 * 60 * 1000L
                    
                    if (fileAge > oneDayInMillis) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 