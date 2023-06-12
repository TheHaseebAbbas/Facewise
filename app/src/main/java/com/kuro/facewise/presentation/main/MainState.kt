package com.kuro.facewise.presentation.main

import android.net.Uri
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.util.UiText

data class MainState(
    val areAllFabVisible: Boolean = false,
    val imageUri: Uri? = null,
    val isLoading: MainLoadingState? = null,
    val lastEmotionResult: EmotionResult? = null,
    val error: UiText? = null
)

sealed class MainLoadingState {
    object GetLastEmotionResultLoading: MainLoadingState()
    object GetRandomDataLoading: MainLoadingState()
}
