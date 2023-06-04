package com.kuro.facewise.presentation.emotion.emotion_recognition

import com.kuro.facewise.domain.model.EmotionResponse

data class EmotionRecognitionState(
    val isLoading: Boolean = false,
    val emotion: EmotionResponse? = null,
    val error: String? = null
)
