package com.kuro.facewise.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.kuro.facewise.domain.model.Ayah
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.domain.model.Hadith
import com.kuro.facewise.domain.model.Incident
import com.kuro.facewise.domain.model.IslamicData
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

    override suspend fun getRelevantEmotionData(emotion: String): Flow<Resource<IslamicData>> =
        flow {
            emit(Resource.Loading())
            try {
                val documentReference = FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.KEY_COLLECTION_EMOTION_DATABASE)
                    .document(emotion)
                val ayah = documentReference
                    .collection(FirebaseConstants.KEY_COLLECTION_AYAHS)
                    .get()
                    .await()
                    .documents[(0 until 30).random()].toObject(Ayah::class.java)
                val hadith = documentReference
                    .collection(FirebaseConstants.KEY_COLLECTION_AHADITH)
                    .get()
                    .await()
                    .documents[(0 until 30).random()].toObject(Hadith::class.java)
                val incident = documentReference
                    .collection(FirebaseConstants.KEY_COLLECTION_INCIDENTS)
                    .get()
                    .await()
                    .documents[(0 until 30).random()].toObject(Incident::class.java)
                emit(
                    Resource.Success(
                        IslamicData(
                            ayah = ayah!!,
                            hadith = hadith!!,
                            incident = incident!!
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(
                    Resource.Error(
                        message = exception.localizedMessage
                            ?: "Couldn't get relevant emotion data."
                    )
                )
            }
        }.catch { exception ->
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't get relevant emotion data"
                )
            )
        }.flowOn(Dispatchers.IO)

    override suspend fun putEmotionResult(emotionResult: EmotionResult): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading())
            try {
                FirebaseFirestore.getInstance()
                    .collection(FirebaseConstants.KEY_COLLECTION_USERS)
                    .document(FirebaseAuth.getInstance().uid!!)
                    .collection(FirebaseConstants.KEY_COLLECTION_RECOGNIZED_EMOTION)
                    .document()
                    .set(emotionResult).await()
                emit(Resource.Success(Unit))
            } catch (exception: Exception) {
                emit(
                    Resource.Error(
                        message = exception.localizedMessage
                            ?: "Couldn't save result in database."
                    )
                )
            }
        }.catch { exception ->
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't save result in database."
                )
            )
        }.flowOn(Dispatchers.IO)

    override suspend fun getLastEmotionResult(): Flow<Resource<EmotionResult>> = flow {
        emit(Resource.Loading())
        try {
            val emotionResult = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().uid!!)
                .collection(FirebaseConstants.KEY_COLLECTION_RECOGNIZED_EMOTION)
                .orderBy(FirebaseConstants.KEY_PROPERTY_TIME, Query.Direction.DESCENDING)
                .limit(1L)
                .get()
                .await()
                .documents[0].toObject(EmotionResult::class.java)
            Log.d("TAG", "getLastEmotionResult: $emotionResult")
            emit(Resource.Success(emotionResult))
        } catch (exception: Exception) {
            Log.d("TAG", "getLastEmotionResult: ${exception.localizedMessage}")
//            emit (
//                Resource.Error(
//                    message = exception.localizedMessage
//                        ?: "Couldn't get Last Emotion details."
//                )
//            )
            // TODO
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't get Last Emotion details."
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun getRandomIslamicData(): Flow<Resource<IslamicData>> = flow {
        emit(Resource.Loading())
        try {
            val emotionsList = listOf(
                "angry",
                "disgust",
                "fear",
                "happy",
                "neutral",
                "sad",
                "surprise"
            )
            val ayahEmotion = emotionsList[(0 until 7).random()]
            val hadithEmotion = emotionsList[(0 until 7).random()]
            val incidentEmotion = emotionsList[(0 until 7).random()]
            val ayahDocumentReference = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.KEY_COLLECTION_EMOTION_DATABASE)
                .document(ayahEmotion)
            val hadithDocumentReference = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.KEY_COLLECTION_EMOTION_DATABASE)
                .document(hadithEmotion)
            val incidentDocumentReference = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.KEY_COLLECTION_EMOTION_DATABASE)
                .document(incidentEmotion)
            val ayah = ayahDocumentReference
                .collection(FirebaseConstants.KEY_COLLECTION_AYAHS)
                .get()
                .await()
                .documents[(0 until 30).random()].toObject(Ayah::class.java)
            val hadith = hadithDocumentReference
                .collection(FirebaseConstants.KEY_COLLECTION_AHADITH)
                .get()
                .await()
                .documents[(0 until 30).random()].toObject(Hadith::class.java)
            val incident = incidentDocumentReference
                .collection(FirebaseConstants.KEY_COLLECTION_INCIDENTS)
                .get()
                .await()
                .documents[(0 until 30).random()].toObject(Incident::class.java)
            emit(
                Resource.Success(
                    IslamicData(
                        ayah = ayah!!,
                        hadith = hadith!!,
                        incident = incident!!
                    )
                )
            )
        } catch (exception: Exception) {
            emit(
                Resource.Error(
                    message = exception.localizedMessage
                        ?: "Couldn't get islamic data."
                )
            )
        }
    }.catch { exception ->
        emit(
            Resource.Error(
                message = exception.localizedMessage
                    ?: "Couldn't get islamic data"
            )
        )
    }.flowOn(Dispatchers.IO)
}