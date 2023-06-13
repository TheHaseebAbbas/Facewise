package com.kuro.facewise.presentation.main

import android.net.Uri
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.domain.model.IslamicData
import com.kuro.facewise.util.UiText

data class MainState(
    val areAllFabVisible: Boolean = false,
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val lastEmotionResult: EmotionResult? = null,
    val islamicData: IslamicData? = null,
    val error: UiText? = null
)