package com.kuro.facewise.presentation.main

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentMainBinding
import com.kuro.facewise.domain.model.EmotionResult
import com.kuro.facewise.presentation.main.dialog.ConfirmationDialogFragment
import com.kuro.facewise.util.PrefsProvider
import com.kuro.facewise.util.click
import com.kuro.facewise.util.constants.AppConstants
import com.kuro.facewise.util.constants.PrefsConstants
import com.kuro.facewise.util.createImageUri
import com.kuro.facewise.util.getSimpleDateFormat
import com.kuro.facewise.util.shareImageFromView
import com.kuro.facewise.util.showLongSnackBar
import com.kuro.facewise.util.showPopUpMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    @Inject
    lateinit var prefsProvider: PrefsProvider

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: FragmentMainBinding

    private var imageUri: Uri? = null

    private var emotionResult: EmotionResult? = null

    private val openCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->
        result?.let {
            if (it) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToEmotionRecognitionFragment(
                        imageUri = imageUri!!.toString()
                    )
                )
            }
        }
    }

    private val chooseImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                viewModel.onEvent(MainEvent.OnImageResult(it.data!!))
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToEmotionRecognitionFragment(
                        imageUri = imageUri!!.toString()
                    )
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        if (handleNavigation()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            binding.userName = currentUser?.displayName
            binding.profileImageUrl = currentUser?.photoUrl.toString()
            binding.viewModel = viewModel

            viewModel.onEvent(MainEvent.OnGetLastEmotionResult)
            getRandomIslamicDataOnNextDay()

            setObservers()
            setListeners()
        }
    }

    private fun getRandomIslamicDataOnNextDay() {
        val currentDay = LocalDate.now()

        if (prefsProvider.getString("lastIslamicDateDate").isNullOrEmpty()) {
            prefsProvider.setString(
                "lastIslamicDateDate",
                currentDay.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )
            viewModel.onEvent(MainEvent.OnGetRandomIslamicData)
        } else if (currentDay > LocalDate.parse(
                prefsProvider.getString("lastIslamicDateDate")!!,
                DateTimeFormatter.ofPattern("dd MMM yyyy")
            )
        ) {
            prefsProvider.setString(
                "lastIslamicDateDate",
                currentDay.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            )
            viewModel.onEvent(MainEvent.OnGetRandomIslamicData)
        } else {

            binding.randomEmotionData =
                prefsProvider.getIslamicData(PrefsConstants.KEY_ISLAMIC_DATA)
        }

    }

    private fun handleNavigation() =
        if (!prefsProvider.getBool(PrefsConstants.ONBOARDING_COMPLETED)) {
            findNavController().navigate(MainFragmentDirections.actionGlobalOnBoardingFragment())
            false
        } else if (FirebaseAuth.getInstance().currentUser == null) {
            findNavController().navigate(MainFragmentDirections.actionGlobalSignInFragment())
            false
        } else {
            true
        }


    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.areAllFabVisible = it.areAllFabVisible
                imageUri = it.imageUri
                binding.isLoading = it.isLoading
                if (it.islamicData != null) {
                    Log.d("TAG", "setObservers: ${it.islamicData}")
                    binding.randomEmotionData = it.islamicData
                    prefsProvider.setIslamicData(PrefsConstants.KEY_ISLAMIC_DATA, it.islamicData)
                }
                if (it.lastEmotionResult != null) {
                    Log.d("TAG", "setObservers: ${it.lastEmotionResult}")
                    emotionResult = it.lastEmotionResult
                    binding.expandableCardLayout.isClickable = true
                    binding.ivArrowDown.visibility = View.VISIBLE
                    binding.time = getSimpleDateFormat().format(it.lastEmotionResult.time!!)
                    binding.recentEmotion = it.lastEmotionResult.dominantEmotion.uppercase()
                } else {
                    binding.time = getString(R.string.dashes)
                    binding.recentEmotion = getString(R.string.dashes)
                }
                if (it.error != null) {
                    binding.root.showLongSnackBar(it.error.asString(requireActivity()))
                }
            }
        }
    }

    private fun setListeners() {
        binding.fabAdd click {
            viewModel.onEvent(MainEvent.OnMainFabClick)
        }

        binding.fabCamera click {
            viewModel.onEvent(MainEvent.OnMainFabClick)
            viewModel.onEvent(
                MainEvent.OnImageResult(
                    createImageUri(
                        requireActivity().applicationContext,
                        AppConstants.KEY_EMOTION_RECOGNITION_TEMP_IMAGE
                    )
                )
            )
            openCamera.launch(imageUri!!)
        }

        binding.fabGallery click {
            viewModel.onEvent(MainEvent.OnMainFabClick)
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            chooseImage.launch(intent)
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

        binding.expandableCardLayout click {
            handleToggleSection(binding.ivArrowDown)
            setRecentEmotionProgressBars(
                angry = emotionResult!!.angry,
                disgust = emotionResult!!.disgust,
                fear = emotionResult!!.fear,
                happy = emotionResult!!.happy,
                neutral = emotionResult!!.neutral,
                sad = emotionResult!!.sad,
                surprise = emotionResult!!.surprise
            )
        }
        binding.expandableCardLayout.isClickable = false
        binding.ivArrowDown.visibility = View.INVISIBLE
        binding.btnShareAyahOfDay click {
            shareImageFromView(binding.cardAyahOfDay, it)
        }
        binding.btnShareHadithOfDay click {
            shareImageFromView(binding.cardHadithOfDay, it)
        }
        binding.btnShareIslamicIncidentOfDay click {
            shareImageFromView(binding.cardIslamicIncidenceOfDay, it)
        }
    }

    private fun setRecentEmotionProgressBars(
        angry: Int,
        disgust: Int,
        fear: Int,
        happy: Int,
        neutral: Int,
        sad: Int,
        surprise: Int,
    ) {
        val progressBars = arrayOf(
            Pair(
                binding.layoutEmotionDetails.angryLinearProgress,
                if (angry == 0) 1 else angry
            ),
            Pair(
                binding.layoutEmotionDetails.disgustLinearProgress,
                if (disgust == 0) 1 else disgust
            ),
            Pair(
                binding.layoutEmotionDetails.fearLinearProgress,
                if (fear == 0) 1 else fear
            ),
            Pair(
                binding.layoutEmotionDetails.happyLinearProgress,
                if (happy == 0) 1 else happy
            ),
            Pair(
                binding.layoutEmotionDetails.neutralLinearProgress,
                if (neutral == 0) 1 else neutral
            ),
            Pair(
                binding.layoutEmotionDetails.sadLinearProgress,
                if (sad == 0) 1 else sad
            ),
            Pair(
                binding.layoutEmotionDetails.surpriseLinearProgress,
                if (surprise == 0) 1 else surprise
            )
        )

        for ((progressBar, progressValue) in progressBars) {
            val progressAnimation =
                ObjectAnimator.ofInt(progressBar, "progress", 0, progressValue)
            progressAnimation.duration = 1000
            progressAnimation.start()
        }
    }

    private fun handleToggleSection(view: View) {
        val show = toggleArrow(view)
        if (show) {
            expand(binding.layoutEmotionDetails.layout, object : AnimListener {
                override fun onFinish() {
                    nestedScrollTo(binding.nestedScrollView, binding.layoutEmotionDetails.layout)
                }
            })
        } else {
            collapse(binding.layoutEmotionDetails.layout)
        }
    }

    private fun expand(v: View, animListener: AnimListener) {
        val a = expandAction(v)
        a.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                animListener.onFinish()
            }

            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) = Unit
        })
        v.startAnimation(a)
    }

    private fun expandAction(v: View): Animation {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetedHeight = v.measuredHeight
        v.layoutParams.height = 0
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetedHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (targetedHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
        return a
    }

    private fun nestedScrollTo(nested: NestedScrollView, targetView: View) {
        nested.post { nested.scrollTo(500, targetView.bottom) }
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    interface AnimListener {
        fun onFinish()
    }

    private fun toggleArrow(view: View): Boolean = if (view.rotation == 0f) {
        view.animate().setDuration(200).rotation(180f)
        true
    } else {
        view.animate().setDuration(200).rotation(0f)
        false
    }
}