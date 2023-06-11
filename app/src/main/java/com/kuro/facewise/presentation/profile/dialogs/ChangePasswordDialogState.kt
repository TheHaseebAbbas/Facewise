package com.kuro.facewise.presentation.profile.dialogs

import com.kuro.facewise.util.UiText

data class ChangePasswordDialogState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: ChangePasswordDialogError? = null
)

sealed class ChangePasswordDialogError {
    class InvalidOldPasswordError(val message: UiText?) : ChangePasswordDialogError()
    class InvalidNewPasswordError(val message: UiText?) : ChangePasswordDialogError()
    class InvalidNewConfirmPasswordError(val message: UiText?) : ChangePasswordDialogError()
    class MatchPasswordError(val message: UiText?) : ChangePasswordDialogError()
    class ServerError(val message: UiText?) : ChangePasswordDialogError()
}