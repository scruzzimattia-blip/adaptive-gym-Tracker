package com.mattia.adaptivegymtracker.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncService @Inject constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun syncSessionToCloud(session: WorkoutSession): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        
        return try {
            firestore.collection("users").document(uid)
                .collection("sessions").document(session.id.toString())
                .set(session, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
