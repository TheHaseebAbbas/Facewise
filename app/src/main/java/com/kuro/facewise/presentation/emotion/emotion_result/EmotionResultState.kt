package com.kuro.facewise.presentation.emotion.emotion_result

import com.kuro.facewise.domain.model.IslamicData
import com.kuro.facewise.util.UiText

data class EmotionResultState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val islamicData: IslamicData? = null,
    val error: UiText? = null
)
