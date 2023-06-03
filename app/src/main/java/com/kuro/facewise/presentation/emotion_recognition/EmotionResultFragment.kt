package com.kuro.facewise.presentation.emotion_recognition

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionRecognitionBinding
import com.kuro.facewise.databinding.FragmentEmotionResultBinding
import com.kuro.facewise.util.click

class EmotionResultFragment : Fragment(R.layout.fragment_emotion_result) {
    private var _binding: FragmentEmotionResultBinding? = null

    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEmotionResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()

        setListeners()
    }

    private fun setupPieChart() {
        binding.pcEmotionResult.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            legend.isEnabled = true
            this.centerText = "Emotion Result"
            this.setDrawEntryLabels(false)
            animateY(1000)
        }
        populatePieChart()
    }

    private fun populatePieChart() {
        val emotions = listOf(
            "Happy",
            "Sad",
            "Anger",
            "Surprise",
            "Disgust",
            "Neutral",
            "Fear"
        )

        val percentages = listOf(2f, 2f, 2f, 2f, 2f, 88f, 2f)
        val maxElement = percentages.maxByOrNull { it }
        val maxIndex = percentages.indexOf(maxElement)

        val entries = ArrayList<PieEntry>()
        for (i in emotions.indices) {
            entries.add(PieEntry(percentages[i], emotions[i]))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            colors = listOf(
                requireActivity().resources.getColor(R.color.happyYellow, null),
                requireActivity().resources.getColor(R.color.sadBlue, null),
                requireActivity().resources.getColor(R.color.angryRed, null),
                requireActivity().resources.getColor(R.color.surprisePurple, null),
                requireActivity().resources.getColor(R.color.disgustGreen, null),
                requireActivity().resources.getColor(R.color.neutralGray, null),
                requireActivity().resources.getColor(R.color.fearOrange, null),
            )
            this.valueTextColor = Color.TRANSPARENT
            valueTextSize = 12f
        }

        val data = PieData(dataSet)
        binding.pcEmotionResult.data = data
        binding.pcEmotionResult.highlightValue(maxIndex.toFloat(),0)
    }

    private fun setListeners() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
