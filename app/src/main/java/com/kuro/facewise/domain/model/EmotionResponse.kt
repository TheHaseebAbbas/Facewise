package com.kuro.facewise.domain.model

data class EmotionResponse(
    val dominant_emotion: String,
    val emotion: Emotion
)