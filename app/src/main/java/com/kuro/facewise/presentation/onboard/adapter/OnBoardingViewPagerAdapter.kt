package com.kuro.facewise.presentation.onboard.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kuro.facewise.presentation.onboard.Fragment1
import com.kuro.facewise.presentation.onboard.Fragment2
import com.kuro.facewise.presentation.onboard.Fragment3
import com.kuro.facewise.presentation.onboard.Fragment4
import com.kuro.facewise.presentation.onboard.Fragment5

class OnBoardingViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    companion object {
        private const val NUM_PAGES = 5
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> Fragment1()
        1 -> Fragment2()
        2 -> Fragment3()
        3 -> Fragment4()
        else -> Fragment5()
    }
}