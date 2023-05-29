package com.kuro.facewise.presentation.auth.sign_in

sealed class SignInEvent {
    object OnSignIn : SignInEvent()
}
