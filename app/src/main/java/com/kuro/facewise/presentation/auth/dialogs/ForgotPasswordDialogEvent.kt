package com.kuro.facewise.presentation.auth.dialogs

sealed class ForgotPasswordDialogEvent {
    object OnSendEmailClick : ForgotPasswordDialogEvent()
}
