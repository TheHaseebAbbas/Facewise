package com.kuro.facewise.presentation.profile.dialogs

import android.net.Uri

sealed class ChangePasswordDialogEvent {
    object OnUpdateClicked: ChangePasswordDialogEvent()
}