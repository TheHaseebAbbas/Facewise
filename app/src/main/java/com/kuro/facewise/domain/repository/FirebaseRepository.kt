package com.kuro.facewise.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun signUp(email: String, password: String): Flow<Resource<FirebaseUser>>
}