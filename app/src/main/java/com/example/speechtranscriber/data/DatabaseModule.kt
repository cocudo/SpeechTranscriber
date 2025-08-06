package com.example.speechtranscriber.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideTranscriptionDatabase(@ApplicationContext context: Context): TranscriptionDatabase {
        return TranscriptionDatabase.createDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideTranscriptionDao(database: TranscriptionDatabase): TranscriptionDao {
        return database.transcriptionDao()
    }
} 