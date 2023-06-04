package com.kuro.facewise.util

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter

@BindingAdapter("rotateFab")
fun View.rotateFab(rotate: Boolean) {
    this.animate().setDuration(200)
        .rotation(if (rotate) 135f else 0f)
}

@BindingAdapter("setImageFromUri")
fun ImageView.setImageFromUri(uri: String) {
    setImageURI(uri.toUri())
}