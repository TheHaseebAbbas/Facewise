package com.kuro.facewise.presentation.main


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentMainBinding
import com.kuro.facewise.util.click


class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null

    var isAllFabsVisible: Boolean? = null
    private val binding
        get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        //setRecentEmotionProgressBars()
        setFloatingActionButtons()
        setListeners()

    }

    private fun setListeners() {

        binding.cardRecentEmotion click {

            /*if (binding.layoutEmotionDetails.layout.visibility == View.GONE) {
                binding.layoutEmotionDetails.layout.visibility = View.VISIBLE
                setRecentEmotionProgressBars()
            } else binding.layoutEmotionDetails.layout.visibility = View.GONE

            if (binding.ivArrowDown.visibility == View.GONE)
                binding.ivArrowDown.visibility = View.VISIBLE
            else binding.ivArrowDown.visibility = View.GONE*/


        }

        binding.expandableCardLayout click {
            handleToggleSection(binding.ivArrowDown)
            setRecentEmotionProgressBars()
        }
    }

    private fun setFloatingActionButtons() {
        //binding.fabAdd.shrink()

        binding.fabCamera.visibility = View.GONE
        binding.fabGallery.visibility = View.GONE
        binding.cardTextCamera.visibility = View.GONE
        binding.textGallery.visibility = View.GONE

        isAllFabsVisible = false

        binding.fabAdd click {
            isAllFabsVisible = if (!isAllFabsVisible!!) {

                binding.viewBlur.visibility = View.VISIBLE

                binding.fabCamera.show()
                binding.fabGallery.show()
                binding.cardTextCamera.visibility = View.VISIBLE
                binding.textGallery.visibility = View.VISIBLE

                //binding.fabAdd.extend()

                rotateFab(binding.fabAdd,true)

                true
            } else {

                binding.viewBlur.visibility = View.GONE

                binding.fabCamera.hide()
                binding.fabGallery.hide()
                binding.cardTextCamera.visibility = View.GONE
                binding.textGallery.visibility = View.GONE

                //binding.fabAdd.shrink()

                rotateFab(binding.fabAdd,false)

                false
            }
        }

        binding.fabCamera click {
            Toast.makeText(context, "Camera", Toast.LENGTH_SHORT).show()
        }
        binding.fabGallery click {
            Toast.makeText(context, "Gallery", Toast.LENGTH_SHORT).show()
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
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                animListener.onFinish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
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
    private fun toggleArrow(view: View): Boolean {
        return if (view.rotation == 0f) {
            view.animate().setDuration(200).rotation(180f)
            true
        } else {
            view.animate().setDuration(200).rotation(0f)
            false
        }
    }

    private fun rotateFab(v: View, rotate: Boolean) {
        v.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                }
            })
            .rotation(if (rotate) 135f else 0f)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}