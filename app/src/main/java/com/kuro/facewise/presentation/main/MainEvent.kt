package com.kuro.facewise.presentation.main

import android.net.Uri

sealed class MainEvent {
    object OnMainFabClick : MainEvent()
    class OnImageResult(val uri: Uri) : MainEvent()
    object OnGetLastEmotionResult : MainEvent()
    object OnGetRandomIslamicData : MainEvent()
}
