package com.kuro.facewise.presentation.emotion.emotion_recognition

import android.net.Uri

sealed class EmotionRecognitionEvent {
    class OnEmotionRecognition(val imageUri: Uri) : EmotionRecognitionEvent()
}
