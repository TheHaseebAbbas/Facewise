package com.kuro.facewise.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun signUp(name: String, email: String, password: String): Flow<Resource<Unit>>

    suspend fun signIn(email: String, password: String): Flow<Resource<Unit>>

}