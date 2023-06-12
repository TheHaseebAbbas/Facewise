package com.kuro.facewise.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kuro.facewise.domain.model.EmotionResponse

data class EmotionResponseDto(
    @SerializedName("dominant_emotion")
    val dominantEmotion: String?,
    val emotion: EmotionDto?,
    val message: String?,
    val region: RegionDto?
) {
    fun toEmotionResponse(): EmotionResponse {
        return EmotionResponse(
            dominantEmotion = dominantEmotion!!,
            emotion = emotion!!.toEmotion()
        )
    }
}