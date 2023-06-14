package com.kuro.facewise.domain.model

import org.threeten.bp.LocalDate
import java.util.Date

data class EmotionResult(
    val dominantEmotion: String,
    val angry: Int,
    val disgust: Int,
    val fear: Int,
    val happy: Int,
    val neutral: Int,
    val sad: Int,
    val surprise: Int,
    val time: Date?
) {
    constructor(): this("", 0, 0, 0, 0, 0, 0, 0, null)
}
