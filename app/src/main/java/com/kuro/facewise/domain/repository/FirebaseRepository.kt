package com.kuro.facewise.domain.repository

import android.net.Uri
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.domain.model.RelevantEmotionData
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun signUp(name: String, email: String, password: String): Flow<Resource<Unit>>

    suspend fun signIn(email: String, password: String): Flow<Resource<Unit>>

    suspend fun updateProfile(name: String, imageUri: Uri?): Flow<Resource<Unit>>

    suspend fun updatePassword(email: String, oldPassword: String, newPassword: String): Flow<Resource<Unit>>

    suspend fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>

    suspend fun getRelevantEmotionData(emotion: String): Flow<Resource<RelevantEmotionData>>

    suspend fun putEmotionResult(emotionResult: EmotionResult): Flow<Resource<Unit>>

    suspend fun getLastEmotionResult(): Flow<Resource<EmotionResult>>
}