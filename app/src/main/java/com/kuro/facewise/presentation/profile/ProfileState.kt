package com.kuro.facewise.presentation.profile

import android.net.Uri
import com.kuro.facewise.util.UiText

data class ProfileState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: ProfileError? = null,
    val imageUri: Uri? = null
)

sealed class ProfileError {
    class InvalidNameError(val message: UiText?) : ProfileError()
    class ServerError(val message: UiText?) : ProfileError()
}