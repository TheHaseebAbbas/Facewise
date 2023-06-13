package com.kuro.facewise.presentation.auth.dialog

sealed class ForgotPasswordDialogEvent {
    object OnSendEmailClick : ForgotPasswordDialogEvent()
}
