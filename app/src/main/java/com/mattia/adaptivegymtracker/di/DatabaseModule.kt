package com.mattia.adaptivegymtracker.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.room.Room
import com.mattia.adaptivegymtracker.data.AppDatabase
import com.mattia.adaptivegymtracker.data.WorkoutDao
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import com.mattia.adaptivegymtracker.domain.AdaptiveEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val DB_KEY_ALIAS = "AdaptiveGymTrackerDBKey"

    private fun getSecureDatabaseKey(): ByteArray {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        
        if (!keyStore.containsAlias(DB_KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            val keySpec = KeyGenParameterSpec.Builder(
                DB_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
            
            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }

        val secretKeyEntry = keyStore.getEntry(DB_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey.encoded
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = getSecureDatabaseKey()
        val supportFactory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gym_tracker_secure_database"
        )
        // Ensure SQLCipher uses the SupportFactory wrapper
        .openHelperFactory(supportFactory)
        // Destructive migration for development transition
        .fallbackToDestructiveMigration()
        .build()
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
