package com.kuro.facewise.presentation.emotion.emotion_result

import com.kuro.facewise.domain.model.RelevantEmotionData
import com.kuro.facewise.util.UiText

data class EmotionResultState(
    val isLoading: EmotionResultLoadingState? = null,
    val isSuccess: Boolean = false,
    val relevantEmotionData: RelevantEmotionData? = null,
    val error: UiText? = null
)

sealed class EmotionResultLoadingState {
    class GetRelevantEmotionDataLoading(val isLoading: Boolean = false): EmotionResultLoadingState()
    object PutRecognisedEmotionLoading: EmotionResultLoadingState()
}
