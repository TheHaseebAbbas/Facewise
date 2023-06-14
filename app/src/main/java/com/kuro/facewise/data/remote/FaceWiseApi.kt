package com.kuro.facewise.data.remote

import com.google.gson.JsonObject
import com.kuro.facewise.data.remote.dto.EmotionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface FaceWiseApi {

    @POST("/analyze")
    suspend fun getEmotion(
        @Body imageUrl: JsonObject
    ): EmotionResponseDto

    companion object {
        const val BASE_URL = "https://facewise.up.railway.app/"
    }
}