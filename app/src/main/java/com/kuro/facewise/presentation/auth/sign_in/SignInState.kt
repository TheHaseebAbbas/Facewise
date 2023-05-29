package com.kuro.facewise.presentation.auth.sign_in

import com.kuro.facewise.util.UiText

data class SignInState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: SignInError? = null
)

sealed class SignInError {
    class InvalidEmailError(val message: UiText?) : SignInError()
    class InvalidPasswordError(val message: UiText?) : SignInError()
    class ServerError(val message: UiText?) : SignInError()
}
