package com.kuro.facewise.presentation.emotion.emotion_result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionResultBinding
import com.kuro.facewise.util.click
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu

class EmotionResultFragment : Fragment(R.layout.fragment_emotion_result) {

    private lateinit var binding: FragmentEmotionResultBinding

    private val args: EmotionResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEmotionResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.emotionResponse = args.emotionResponse
        binding.root.showLongSnackBar(args.emotionResponse.dominantEmotion)
        setListeners()
    }

    private fun setListeners() {
        binding.ivUserProfile click {
            findNavController().showPopUpMenu(it)
        }
    }
}
