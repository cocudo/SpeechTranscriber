package com.example.speechtranscriber.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao {
    
    @Query("SELECT * FROM transcriptions ORDER BY timestamp DESC")
    fun getAllTranscriptions(): Flow<List<TranscriptionEntity>>
    
    @Insert
    suspend fun insertTranscription(transcription: TranscriptionEntity): Long
    
    @Delete
    suspend fun deleteTranscription(transcription: TranscriptionEntity)
    
    @Query("DELETE FROM transcriptions WHERE id = :id")
    suspend fun deleteTranscriptionById(id: Long)
    
    @Query("SELECT * FROM transcriptions WHERE id = :id")
    suspend fun getTranscriptionById(id: Long): TranscriptionEntity?
} 