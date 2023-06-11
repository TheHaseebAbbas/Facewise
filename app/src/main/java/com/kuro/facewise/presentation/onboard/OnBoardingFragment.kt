package com.kuro.facewise.presentation.onboard

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.kuro.facewise.MainNavGraphDirections
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentOnBoardingBinding
import com.kuro.facewise.presentation.onboard.adapter.OnBoardingViewPagerAdapter
import com.kuro.facewise.util.PrefsProvider
import com.kuro.facewise.util.click
import com.kuro.facewise.util.constants.PrefsConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    private lateinit var binding: FragmentOnBoardingBinding

    @Inject
    lateinit var prefsProvider: PrefsProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentOnBoardingBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setListeners()
        handleBackPressed()
    }

    private fun setViewPager() {
        val adapter = OnBoardingViewPagerAdapter(this@OnBoardingFragment)
        binding.pager.adapter = adapter
        binding.pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 4) binding.btnSkip.visibility = View.INVISIBLE
                else binding.btnSkip.visibility = View.VISIBLE
            }
        })
        binding.wormDotsIndicator.attachTo(binding.pager)
    }

    private fun setListeners() {
        binding.btnNext click {
            if (binding.pager.currentItem != 4)
                binding.pager.currentItem++
            else {
                onFinishOnBoarding()
            }
        }

        binding.btnSkip click {
            onFinishOnBoarding()
        }
    }

    private fun onFinishOnBoarding() {
        findNavController().navigate(OnBoardingFragmentDirections.actionOnBoardingFragmentToSignInFragment())
        prefsProvider.setBool(PrefsConstants.ONBOARDING_COMPLETED, true)
    }

    private fun handleBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.pager.currentItem == 0) {
                        requireActivity().finish()
                    } else {
                        binding.pager.currentItem = binding.pager.currentItem - 1
                    }
                }
            })
    }
}