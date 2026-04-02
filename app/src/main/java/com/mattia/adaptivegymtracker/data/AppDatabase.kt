package com.mattia.adaptivegymtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mattia.adaptivegymtracker.data.entities.Exercise
import com.mattia.adaptivegymtracker.data.entities.UserProfile
import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import com.mattia.adaptivegymtracker.data.entities.WorkoutSet

@Database(
    entities = [UserProfile::class, Exercise::class, WorkoutSession::class, WorkoutSet::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
