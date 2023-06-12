package com.kuro.facewise.presentation.emotion.emotion_result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionResultBinding
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.util.click
import com.kuro.facewise.util.getSimpleDateFormat
import com.kuro.facewise.util.shareImageFromView
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class EmotionResultFragment : Fragment(R.layout.fragment_emotion_result) {

    val viewModel by viewModels<EmotionResultViewModel>()

    private lateinit var binding: FragmentEmotionResultBinding

    private val args: EmotionResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEmotionResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val emotionResponse = args.emotionResponse
        binding.emotionResponse = emotionResponse
        binding.profileImageUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        viewModel.onEvent(EmotionResultEvent.GetRelevantEmotionData(emotionResponse.dominantEmotion))
        viewModel.onEvent(EmotionResultEvent.PutEmotionResult(
            EmotionResult(
                dominantEmotion = emotionResponse.dominantEmotion,
                angry = emotionResponse.emotion.angry,
                disgust = emotionResponse.emotion.disgust,
                fear = emotionResponse.emotion.fear,
                happy = emotionResponse.emotion.happy,
                neutral = emotionResponse.emotion.neutral,
                sad = emotionResponse.emotion.sad,
                surprise = emotionResponse.emotion.surprise,
                time = Date()
            )
        ))

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                if (it.isLoading != null) {
                    when (it.isLoading) {
                        LoadingState.GetRelevantEmotionDataLoading -> {

                        }
                        LoadingState.PutRecognisedEmotionLoading -> {

                        }
                    }
                }
                if (it.isSuccess) {
                    binding.root.showLongSnackBar("Data uploaded.")
                }
                if (it.relevantEmotionData != null) {
                    binding.relevantEmotionData = it.relevantEmotionData
                }
                if (it.error != null) {
                    binding.root.showLongSnackBar(it.error.asString(requireActivity()))
                }
            }
        }
    }

    private fun setListeners() {
        binding.ivUserProfile click {
            findNavController().showPopUpMenu(it)
        }
        binding.btnShareRelevantAyah click {
            shareImageFromView(binding.cardRelevantAyah,it)
        }
        binding.btnShareRelevantHadith click {
            shareImageFromView(binding.cardRelevantHadith,it)
        }
        binding.btnShareRelevantIslamicIncident click {
            shareImageFromView(binding.cardRelevantIslamicIncidence,it)
        }
        binding.btnShareEmotionResult click {
            shareImageFromView(binding.cardRecognizedEmotion,it)
        }
    }
}
