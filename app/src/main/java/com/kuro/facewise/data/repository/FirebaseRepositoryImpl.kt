package com.kuro.facewise.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import com.kuro.facewise.util.constants.FirebaseConstants
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
                    .setPhotoUri(null)
                    .build()
                val currentUser = firebaseAuth.currentUser!!
                currentUser.updateProfile(request).await()
                val firestore = FirebaseFirestore.getInstance()
                val map = HashMap<String, Boolean>()
                map["exists"] = true
                firestore.collection(FirebaseConstants.KEY_COLLECTION_USERS)
                    .document(currentUser.uid)
                    .set(map)
                    .await()
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

    override suspend fun updateProfile(name: String, imageUri: Uri?): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val firebaseStorage = FirebaseStorage.getInstance()
            val request = UserProfileChangeRequest.Builder()
            if (currentUser != null) {
                val reference = firebaseStorage
                    .reference
                    .child("${FirebaseConstants.KEY_COLLECTION_USERS}/${currentUser.uid}")
                if (imageUri != null) {
                    reference
                        .putFile(imageUri).await()
                    request.photoUri = reference.downloadUrl.await()
                } else {
                    currentUser.photoUrl?.let {
                        firebaseStorage.getReferenceFromUrl(it.toString())
                            .delete()
                            .await()
                    }
                    request.photoUri = null
                }
                request.displayName = name
                currentUser.updateProfile(request.build()).await()
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(message = "Couldn't update profile."))
            }
        } catch (exception: Exception) {
            Log.d("TAG", "updateProfile: ${exception.localizedMessage}")
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't update profile."
                )
            )
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't update profile."
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser!!

            val credential = EmailAuthProvider
                .getCredential(email, oldPassword)

            currentUser.reauthenticate(credential)
                .addOnSuccessListener {
                    Log.d("TAG", "updatePassword: success")
                    currentUser.updatePassword(newPassword)
                }
                .await()
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            Log.d("TAG", "catch1: ${exception.localizedMessage}")
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't update password."
                )
            )
        }
    }.catch { exception ->
        Log.d("TAG", "catch2: ${exception.localizedMessage}")
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't update password."
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email).await()
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't send password reset email."
                )
            )
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't send password reset email."
            )
        )
    }.flowOn(Dispatchers.IO)
}