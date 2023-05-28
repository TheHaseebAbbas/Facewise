package com.kuro.facewise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseRepository {
    override suspend fun signUp(
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> = flow {
        emit(Resource.Loading())
        val authResultTask =
            firebaseAuth.createUserWithEmailAndPassword(email, password)

        if (authResultTask.isSuccessful) {
            val authResult = authResultTask.await()
            emit(Resource.Success(authResult.user))
        } else {
            emit(
                Resource.Error(
                    message = authResultTask.exception?.message
                        ?: "Couldn't sign up. Please try again later"
                )
            )
        }
    }
}