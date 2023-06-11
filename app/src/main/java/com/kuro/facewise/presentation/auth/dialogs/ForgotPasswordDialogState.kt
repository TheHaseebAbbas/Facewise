package com.kuro.facewise.presentation.auth.dialogs

import com.kuro.facewise.util.UiText

data class ForgotPasswordDialogState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: ForgotPasswordDialogError? = null
)

sealed class ForgotPasswordDialogError {
    class InvalidEmailError(val message: UiText?) : ForgotPasswordDialogError()
    class ServerError(val message: UiText?) : ForgotPasswordDialogError()
}
