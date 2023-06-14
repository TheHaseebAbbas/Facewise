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
import com.kuro.facewise.presentation.main.dialog.ConfirmationDialogFragment
import com.kuro.facewise.util.PrefsProvider
import com.kuro.facewise.util.click
import com.kuro.facewise.util.shareImageFromView
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class EmotionResultFragment : Fragment(R.layout.fragment_emotion_result) {

    @Inject
    lateinit var prefsProvider: PrefsProvider

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
        viewModel.onEvent(
            EmotionResultEvent.PutEmotionResult(
                EmotionResult(
                    dominantEmotion = emotionResponse.dominantEmotion,
                    angry = emotionResponse.emotion.angry.toInt(),
                    disgust = emotionResponse.emotion.disgust.toInt(),
                    fear = emotionResponse.emotion.fear.toInt(),
                    happy = emotionResponse.emotion.happy.toInt(),
                    neutral = emotionResponse.emotion.neutral.toInt(),
                    sad = emotionResponse.emotion.sad.toInt(),
                    surprise = emotionResponse.emotion.surprise.toInt(),
                    time = Date()
                )
            )
        )

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.isLoading = it.isLoading
                binding.isError = false

                if (it.isSuccess) {
                    // emotion data uploaded to the server
                }
                if (it.islamicData != null) {
                    binding.relevantEmotionData = it.islamicData
                }
                if (it.error != null) {
                    binding.root.showLongSnackBar(it.error.asString(requireActivity()))
                    binding.isError = true
                }
            }
        }
    }

    private fun setListeners() {
        binding.ivUserProfile click {
            findNavController().showPopUpMenu(it) {
                ConfirmationDialogFragment.newInstance {
                    FirebaseAuth.getInstance().signOut()
                    prefsProvider.clear()
                    requireActivity().recreate()
                }.show(childFragmentManager, "ConfirmationDialogFragment")
            }
        }
        binding.btnShareRelevantAyah click {
            shareImageFromView(binding.cardRelevantAyah, it)
        }
        binding.btnShareRelevantHadith click {
            shareImageFromView(binding.cardRelevantHadith, it)
        }
        binding.btnShareRelevantIslamicIncident click {
            shareImageFromView(binding.cardRelevantIslamicIncidence, it)
        }
        binding.btnShareEmotionResult click {
            shareImageFromView(binding.cardRecognizedEmotion, it)
        }
    }
}
