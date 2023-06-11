package com.kuro.facewise.data.repository

import android.net.Uri
import android.util.Log
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
            if (currentUser != null) {
                val reference = firebaseStorage
                    .reference
                    .child("${FirebaseConstants.KEY_COLLECTION_USERS}/${currentUser.uid}")
                if (imageUri != null) {
                    reference
                        .putFile(imageUri).await()
                    Log.d("TAG", "updateProfile: if")
                } else {
                    Log.d("TAG", "updateProfile: empty imageUri")
                    firebaseStorage.getReferenceFromUrl(currentUser.photoUrl.toString())
                        .delete()
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {

                        }.await()
                    Log.d("TAG", "updateProfile: after on success")
                }
                val request = UserProfileChangeRequest.Builder()
                reference.downloadUrl.addOnSuccessListener {
                    Log.d("TAG", "updateProfile: success")
                    request.displayName = name
                    request.photoUri = it
                }.addOnFailureListener {
                    Log.d("TAG", "updateProfile: failure")
                    request.displayName = name
                }.await()
                currentUser.updateProfile(request.build()).await()
                Log.d("TAG", "updateProfile: ${currentUser.photoUrl}")
                Log.d("TAG", "updateProfile: ${currentUser.displayName}")
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(message = "Couldn't update profile."))
            }
        } catch (exception: Exception) {
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

}