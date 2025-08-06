package com.example.speechtranscriber.repository

import com.example.speechtranscriber.data.TranscriptionDao
import com.example.speechtranscriber.data.TranscriptionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptionRepository @Inject constructor(
    private val transcriptionDao: TranscriptionDao
) {
    
    fun getAllTranscriptions(): Flow<List<TranscriptionEntity>> {
        return transcriptionDao.getAllTranscriptions()
    }
    
    suspend fun saveTranscription(content: String, title: String = "Sesión de transcripción"): Long {
        val transcription = TranscriptionEntity(
            content = content,
            timestamp = java.util.Date(),
            title = title
        )
        return transcriptionDao.insertTranscription(transcription)
    }
    
    suspend fun deleteTranscription(transcription: TranscriptionEntity) {
        transcriptionDao.deleteTranscription(transcription)
    }
    
    suspend fun deleteTranscriptionById(id: Long) {
        transcriptionDao.deleteTranscriptionById(id)
    }
    
    suspend fun getTranscriptionById(id: Long): TranscriptionEntity? {
        return transcriptionDao.getTranscriptionById(id)
    }
} 