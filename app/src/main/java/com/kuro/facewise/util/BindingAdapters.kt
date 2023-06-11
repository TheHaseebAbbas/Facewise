package com.kuro.facewise.util

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.imageview.ShapeableImageView
import com.kuro.facewise.R
import com.kuro.facewise.domain.model.EmotionResponse
import com.squareup.picasso.Picasso

@BindingAdapter("rotateFab")
fun View.rotateFab(rotate: Boolean) {
    this.animate().setDuration(200)
        .rotation(if (rotate) 135f else 0f)
}

@BindingAdapter("setImageFromUri")
fun ImageView.setImageFromUri(uri: String?) {
    setImageURI(uri?.toUri())
}

@BindingAdapter("setImageFromUri")
fun ShapeableImageView.setImageFromUri(uri: String?) {
    Picasso.get()
        .load(uri)
        .placeholder(R.drawable.user_icon)
        .into(this)
}

@BindingAdapter("setupPieChart")
fun PieChart.setupPieChart(emotionResponse: EmotionResponse) {
    // setting values to pieChart
    setUsePercentValues(true)
    description.isEnabled = false
    isDrawHoleEnabled = true
    legend.isEnabled = true
    legend.isWordWrapEnabled = true
    legend.textSize = 14F
    legend.textColor = ContextCompat.getColor(context, R.color.black)
    centerText = emotionResponse.dominantEmotion.uppercase()
    setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat_extrabold))

    setDrawEntryLabels(false)
    animateY(1000)

    // populating values in pie chart
    val emotion = emotionResponse.emotion
    val emotions = listOf(
        Pair("Happy", emotion.happy),
        Pair("Sad", emotion.sad),
        Pair("Anger", emotion.angry),
        Pair("Surprise", emotion.surprise),
        Pair("Disgust", emotion.disgust),
        Pair("Neutral", emotion.neutral),
        Pair("Fear", emotion.fear),
    )

    val emotionsColor = listOf(
        resources.getColor(R.color.happyYellow, null),
        resources.getColor(R.color.sadBlue, null),
        resources.getColor(R.color.angryRed, null),
        resources.getColor(R.color.surprisePurple, null),
        resources.getColor(R.color.disgustGreen, null),
        resources.getColor(R.color.neutralGray, null),
        resources.getColor(R.color.fearOrange, null),
    )

    val maxElement = emotions.maxByOrNull { it.second }
    val maxIndex = emotions.indexOf(maxElement)

    val entries = ArrayList<PieEntry>()
    for (i in emotions.indices) {
        entries.add(PieEntry(emotions[i].second, emotions[i].first))
    }

    val dataSet = PieDataSet(entries, null).apply {
        colors = emotionsColor
        valueTextColor = Color.TRANSPARENT
    }

    data = PieData(dataSet)
    highlightValue(maxIndex.toFloat(), 0)
}