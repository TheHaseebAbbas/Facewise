package com.kuro.facewise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor() : FirebaseRepository {
    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val firebaseAuth = FirebaseAuth.getInstance()
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                val request = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                val currentUser = firebaseAuth.currentUser!!
                currentUser.updateProfile(request).await()
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(message = "Couldn't sign up."))
            }
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't sign up."
                )
            )
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't sign up."
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun signIn(email: String, password: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val firebaseAuth = FirebaseAuth.getInstance()
            val authResult =
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(message = "Couldn't sign in."))
            }
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't sign in."
                )
            )
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't sign in."
            )
        )
    }.flowOn(Dispatchers.IO)

}