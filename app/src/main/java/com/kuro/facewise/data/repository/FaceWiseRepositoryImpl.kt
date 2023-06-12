package com.kuro.facewise.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonObject
import com.kuro.facewise.data.remote.FaceWiseApi
import com.kuro.facewise.domain.model.EmotionResponse
import com.kuro.facewise.domain.repository.FaceWiseRepository
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FaceWiseRepositoryImpl @Inject constructor(
    private val api: FaceWiseApi,
) : FaceWiseRepository {
    override fun getEmotions(imageUri: Uri): Flow<Resource<EmotionResponse>> = flow {
        emit(Resource.Loading())
        try {
            val imageReference = FirebaseStorage.getInstance().reference.child("image")

            val result = imageReference
                .putFile(imageUri).await()

            if (result.task.isSuccessful) {
                val emotion = api.getEmotion(
                    JsonObject().apply {
                        addProperty(
                            "img_path",
                            imageReference.downloadUrl.await().toString()
                        )
                    }
                )
                if (emotion.message == null) {
                    emit(Resource.Success(emotion.toEmotionResponse()))
                } else {
                    emit(Resource.Error(message = emotion.message))
                }
            }
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.message ?: "An unknown error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.message ?: "Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message ?: "An Unknown Error occurred."))
        }
    }
}