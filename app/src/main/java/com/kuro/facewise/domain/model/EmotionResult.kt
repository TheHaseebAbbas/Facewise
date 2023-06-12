package com.kuro.facewise.domain.model

import java.util.Date

data class EmotionResult(
    val dominantEmotion: String,
    val angry: Float,
    val disgust: Float,
    val fear: Float,
    val happy: Float,
    val neutral: Float,
    val sad: Float,
    val surprise: Float,
    val time: Date
)
