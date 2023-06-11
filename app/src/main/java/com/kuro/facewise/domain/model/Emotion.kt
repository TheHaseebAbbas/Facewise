package com.kuro.facewise.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Emotion(
    val angry: Float,
    val disgust: Float,
    val fear: Float,
    val happy: Float,
    val neutral: Float,
    val sad: Float,
    val surprise: Float
): Parcelable