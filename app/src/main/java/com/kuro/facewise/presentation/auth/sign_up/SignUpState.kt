package com.kuro.facewise.presentation.auth.sign_up

import com.kuro.facewise.util.UiText

data class SignUpState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: SignUpError? = null
)

sealed class SignUpError {
    class InvalidNameError(val message: UiText?) : SignUpError()
    class InvalidEmailError(val message: UiText?) : SignUpError()
    class InvalidPasswordError(val message: UiText?) : SignUpError()
    class InvalidConfirmPasswordError(val message: UiText?) : SignUpError()
    class MatchPasswordError(val message: UiText?) : SignUpError()
    class ServerError(val message: UiText?) : SignUpError()
    class TermsAndConditionsError(val message: UiText?) : SignUpError()
}
