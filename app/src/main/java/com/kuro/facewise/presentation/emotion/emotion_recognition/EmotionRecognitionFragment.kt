package com.kuro.facewise.presentation.emotion.emotion_recognition

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionRecognitionBinding
import com.kuro.facewise.presentation.main.dialog.ConfirmationDialogFragment
import com.kuro.facewise.util.ImageUtils
import com.kuro.facewise.util.PrefsProvider
import com.kuro.facewise.util.click
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EmotionRecognitionFragment : Fragment(R.layout.fragment_emotion_recognition) {

    @Inject
    lateinit var prefsProvider: PrefsProvider

    private val viewModel by viewModels<EmotionRecognitionViewModel>()

    private lateinit var binding: FragmentEmotionRecognitionBinding

    private val args: EmotionRecognitionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEmotionRecognitionBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.imageUri = args.imageUri
        binding.profileImageUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.isLoading = it.isLoading
                if (it.isLoading) return@collectLatest
                if (it.emotion != null) {
                    findNavController().navigate(
                        EmotionRecognitionFragmentDirections
                            .actionEmotionRecognitionFragmentToEmotionResultFragment(
                                emotionResponse = it.emotion
                            )
                    )
                }
                if (it.error != null) {
                    binding.root.showLongSnackBar(it.error.asString(requireActivity()))
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnScanImage click {
            lifecycleScope.launch {
                val compressedImage = Compressor.compress(
                    requireActivity(),
                    ImageUtils.fileFromContentUri(requireActivity(), args.imageUri.toUri()),
                )
                viewModel.onEvent(EmotionRecognitionEvent.OnEmotionRecognition(imageUri = compressedImage.toUri()))
            }
        }
        binding.ivUserProfile click {
            findNavController().showPopUpMenu(it) {
                ConfirmationDialogFragment.newInstance {
                    FirebaseAuth.getInstance().signOut()
                    prefsProvider.clear()
                    requireActivity().recreate()
                }.show(childFragmentManager, "ConfirmationDialogFragment")
            }
        }
    }
}