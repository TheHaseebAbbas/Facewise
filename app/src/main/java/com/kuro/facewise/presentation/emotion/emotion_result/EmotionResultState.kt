package com.kuro.facewise.presentation.emotion.emotion_result

import com.kuro.facewise.domain.model.RelevantEmotionData
import com.kuro.facewise.util.UiText

data class EmotionResultState(
    val isLoading: LoadingState? = null,
    val isSuccess: Boolean = false,
    val relevantEmotionData: RelevantEmotionData? = null,
    val error: UiText? = null
)

sealed class LoadingState {
    object GetRelevantEmotionDataLoading: LoadingState()
    object PutRecognisedEmotionLoading: LoadingState()
}
