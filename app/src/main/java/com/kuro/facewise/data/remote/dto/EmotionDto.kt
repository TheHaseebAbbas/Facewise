package com.kuro.facewise.data.remote.dto

import com.kuro.facewise.domain.model.Emotion

data class EmotionDto(
    val angry: Double,
    val disgust: Double,
    val fear: Double,
    val happy: Double,
    val neutral: Double,
    val sad: Double,
    val surprise: Double
) {
    fun toEmotion(): Emotion {
        return Emotion(
            angry = angry.toFloat(),
            disgust = disgust.toFloat(),
            fear = fear.toFloat(),
            happy = happy.toFloat(),
            neutral = neutral.toFloat(),
            sad = sad.toFloat(),
            surprise = surprise.toFloat()
        )
    }
}