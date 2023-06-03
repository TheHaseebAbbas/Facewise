package com.kuro.facewise.presentation.main


import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentMainBinding
import com.kuro.facewise.util.click
import com.kuro.facewise.util.constants.AppConstants
import com.kuro.facewise.util.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()

    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding!!

    private var imageUri: Uri? = null

    private val openCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->
        result?.let {
            showLongToast(imageUri!!.toString())
        }
    }

    private val chooseImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                viewModel.onEvent(MainEvent.OnImageResult(it.data!!))
                showLongToast(imageUri!!.toString())
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.areAllFabVisible = it.areAllFabVisible
                imageUri = it.imageUri
            }
        }
    }

    private fun setListeners() {

        binding.expandableCardLayout click {
            handleToggleSection(binding.ivArrowDown)
            setRecentEmotionProgressBars()
        }

        binding.fabAdd click {
            viewModel.onEvent(MainEvent.OnMainFabClick)
        }

        binding.fabCamera click {
            viewModel.onEvent(MainEvent.OnMainFabClick)
            viewModel.onEvent(MainEvent.OnImageResult(createImageUri()))
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
    }

    private fun setRecentEmotionProgressBars() {
        val progressBars = arrayOf(
            Pair(binding.layoutEmotionDetails.angryLinearProgress, 65),
            Pair(binding.layoutEmotionDetails.disgustLinearProgress, 36),
            Pair(binding.layoutEmotionDetails.fearLinearProgress, 71),
            Pair(binding.layoutEmotionDetails.happyLinearProgress, 88),
            Pair(binding.layoutEmotionDetails.neutralLinearProgress, 44),
            Pair(binding.layoutEmotionDetails.sadLinearProgress, 51),
            Pair(binding.layoutEmotionDetails.surpriseLinearProgress, 78)
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

    private fun createImageUri(): Uri {
        val image = File(requireActivity().applicationContext.filesDir, AppConstants.KEY_TEMP_IMAGE)
        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            AppConstants.KEY_FILE_PROVIDER_AUTHORITY,
            image
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}