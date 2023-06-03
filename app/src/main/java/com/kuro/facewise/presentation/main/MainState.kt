package com.kuro.facewise.presentation.main

import android.net.Uri

data class MainState(
    val areAllFabVisible: Boolean = false,
    val imageUri: Uri? = null
)