package com.kuro.facewise.util

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

infix fun View.click(v: View.OnClickListener) {
    setOnClickListener(v)
}

fun AppCompatActivity.showShortToast(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun String?.isNotNullOrEmpty() = this.isNullOrEmpty().not()