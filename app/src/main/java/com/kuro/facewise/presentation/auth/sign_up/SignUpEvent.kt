package com.kuro.facewise.presentation.auth.sign_up

sealed class SignUpEvent {
    object OnSignUp : SignUpEvent()
}
