package com.mattia.adaptivegymtracker.di

import android.content.Context
import com.mattia.adaptivegymtracker.data.AppDatabase
import com.mattia.adaptivegymtracker.data.WorkoutDao
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import com.mattia.adaptivegymtracker.domain.AdaptiveEngine
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(appDatabase: AppDatabase): WorkoutDao {
        return appDatabase.workoutDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(workoutDao: WorkoutDao): WorkoutRepository {
        return WorkoutRepository(workoutDao)
    }

    @Provides
    @Singleton
    fun provideAdaptiveEngine(repository: WorkoutRepository): AdaptiveEngine {
        return AdaptiveEngine(repository)
    }
}
