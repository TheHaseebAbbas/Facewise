package com.kuro.facewise.presentation.emotion_recognition

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionRecognitionBinding
import com.kuro.facewise.databinding.FragmentMainBinding
import com.kuro.facewise.util.click

class EmotionRecognitionFragment : Fragment(R.layout.fragment_emotion_recognition) {
    private var _binding: FragmentEmotionRecognitionBinding? = null

    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEmotionRecognitionBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.btnScanImage click {
            binding.btnScanImage.visibility = View.GONE
            binding.spinKit.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}