package com.kuro.facewise.domain.model

import android.os.Message
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmotionResponse(
    val dominantEmotion: String,
    val emotion: Emotion
): Parcelable