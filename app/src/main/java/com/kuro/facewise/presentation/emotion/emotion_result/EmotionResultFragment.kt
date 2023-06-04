package com.kuro.facewise.presentation.emotion.emotion_result

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionResultBinding
import com.kuro.facewise.util.click
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu

class EmotionResultFragment : Fragment(R.layout.fragment_emotion_result) {

    private var _binding: FragmentEmotionResultBinding? = null
    private val binding
        get() = _binding!!

    private val args: EmotionResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEmotionResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()

        binding.root.showLongSnackBar(args.emotionResponse.dominantEmotion)
        setListeners()
    }

    private fun setListeners() {
        binding.ivUserProfile click {
            findNavController().showPopUpMenu(it)
        }
    }

    private fun setupPieChart() {
        binding.pcEmotionResult.apply {
            // setting values to pieChart
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            legend.isEnabled = true
            legend.isWordWrapEnabled = true
            legend.textSize = 14F
            legend.textColor = requireActivity().getColor(R.color.black)
            centerText = args.emotionResponse.dominantEmotion.uppercase()

            setDrawEntryLabels(false)
            animateY(1000)

            // populating values in pie chart
            val emotion = args.emotionResponse.emotion
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
                requireActivity().resources.getColor(R.color.happyYellow, null),
                requireActivity().resources.getColor(R.color.sadBlue, null),
                requireActivity().resources.getColor(R.color.angryRed, null),
                requireActivity().resources.getColor(R.color.surprisePurple, null),
                requireActivity().resources.getColor(R.color.disgustGreen, null),
                requireActivity().resources.getColor(R.color.neutralGray, null),
                requireActivity().resources.getColor(R.color.fearOrange, null),
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
