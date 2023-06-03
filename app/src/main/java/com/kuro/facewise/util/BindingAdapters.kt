package com.kuro.facewise.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("rotateFab")
fun View.rotateFab(rotate: Boolean) {
    this.animate().setDuration(200)
        .rotation(if (rotate) 135f else 0f)
}