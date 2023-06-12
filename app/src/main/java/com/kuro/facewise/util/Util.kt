package com.kuro.facewise.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import com.google.android.material.textfield.TextInputLayout
import com.kuro.facewise.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = false
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun addAfterTextChangeListener(vararg textInputLayouts: Pair<TextInputLayout, String>) {
    for (textInputLayout in textInputLayouts) {
        textInputLayout.first.editText!!.doAfterTextChanged {
            if (it.toString().isBlank()) {
                textInputLayout.first.error = textInputLayout.second
            } else {
                textInputLayout.first.error = null
            }
        }
    }
}

fun getSimpleDateFormat() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

fun shareImageFromView(cardView: View,btnShare:View){

    btnShare.visibility = View.GONE

    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(cardView.width, View.MeasureSpec.EXACTLY)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(cardView.height, View.MeasureSpec.AT_MOST)
    cardView.measure(widthMeasureSpec, heightMeasureSpec)
    cardView.layout(0, 0, cardView.measuredWidth, cardView.measuredHeight)

    val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val background = cardView.background
    background.draw(canvas)
    cardView.draw(canvas)

    btnShare.visibility = View.VISIBLE

    val imageUri = createImageUri(cardView.context,AppConstants.KEY_SHARE_TEMP_IMAGE)

    val outputStream = cardView.context.applicationContext.contentResolver.openOutputStream(imageUri)
    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
    outputStream!!.close()

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "image/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri)
    startActivity(cardView.context,Intent.createChooser(shareIntent,"Share Image"),null)
}

fun createImageUri(applicationContext: Context, fileName: String): Uri {
    val image = File(applicationContext.filesDir, fileName)
    return FileProvider.getUriForFile(
        applicationContext,
        AppConstants.KEY_FILE_PROVIDER_AUTHORITY,
        image
    )
}
