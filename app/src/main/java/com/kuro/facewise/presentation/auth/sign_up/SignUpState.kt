package com.kuro.facewise.presentation.auth.sign_up

import com.google.firebase.auth.FirebaseUser
import com.kuro.facewise.util.UiText

data class SignUpState(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isNew: Boolean = false,
    val user: FirebaseUser? = null,
    val error: LoginError? = null
)

sealed class LoginError {
    class InvalidName(val message: UiText?) : LoginError()
    class InvalidEmail(val message: UiText?) : LoginError()
    class InvalidPassword(val message: UiText?) : LoginError()
    class InvalidConfirmPassword(val message: UiText?) : LoginError()
    class NotMatchPassword(val message: UiText?) : LoginError()
    class ServerError(val message: UiText?) : LoginError()
}
