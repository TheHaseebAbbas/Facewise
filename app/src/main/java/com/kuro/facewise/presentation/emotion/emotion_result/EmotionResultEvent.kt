package com.kuro.facewise.presentation.emotion.emotion_result

import com.kuro.facewise.domain.model.EmotionResult

sealed class EmotionResultEvent {
    class GetRelevantEmotionData(val emotion: String): EmotionResultEvent()
    class PutEmotionResult(val emotionResult: EmotionResult): EmotionResultEvent()
}
