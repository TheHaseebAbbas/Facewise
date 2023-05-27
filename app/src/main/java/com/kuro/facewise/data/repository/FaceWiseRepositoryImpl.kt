package com.kuro.facewise.data.repository

import android.net.Uri
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
    private val storageReference: StorageReference
) : FaceWiseRepository {
    override fun getEmotions(imageUri: Uri): Flow<Resource<EmotionResponse>> = flow {
        emit(Resource.Loading())
        try {
            val imageReference = storageReference.child("image")

            val result = imageReference
                .putFile(imageUri).await()

            if (result.task.isSuccessful) {
                val emotion = api.getEmotion(
                    JsonObject().apply {
                        this.addProperty(
                            "img_path",
                            imageReference.downloadUrl.await().toString()
                        )
                    }
                ).toEmotionResponse()
                emit(Resource.Success(emotion))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.message ?: "An unknown error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server. Check your internet connection."))
        }
    }
}