package com.kuro.facewise.presentation.profile

import android.net.Uri

sealed class ProfileEvent {
    class OnUpdateClicked(val uri: Uri?): ProfileEvent()
    class OnImageResult(val uri: Uri?): ProfileEvent()
}