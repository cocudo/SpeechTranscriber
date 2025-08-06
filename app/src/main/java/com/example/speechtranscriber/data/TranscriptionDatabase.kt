package com.example.speechtranscriber.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [TranscriptionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TranscriptionDatabase : RoomDatabase() {
    abstract fun transcriptionDao(): TranscriptionDao
    
    companion object {
        fun createDatabase(context: Context): TranscriptionDatabase {
            return Room.databaseBuilder(
                context,
                TranscriptionDatabase::class.java,
                "transcription_database"
            ).build()
        }
    }
} 