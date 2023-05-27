package com.kuro.facewise.data.remote.dto

import com.kuro.facewise.domain.model.EmotionResponse

data class EmotionResponseDto(
    val dominant_emotion: String,
    val emotion: EmotionDto,
    val region: RegionDto
) {
    fun toEmotionResponse(): EmotionResponse {
        return EmotionResponse(
            dominant_emotion = dominant_emotion,
            emotion = emotion.toEmotion()
        )
    }
}