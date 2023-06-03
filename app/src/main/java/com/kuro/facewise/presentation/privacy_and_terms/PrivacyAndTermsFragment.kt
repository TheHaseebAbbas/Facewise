package com.kuro.facewise.presentation.privacy_and_terms

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.htmlEncode
import androidx.navigation.fragment.navArgs
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentEmotionResultBinding
import com.kuro.facewise.databinding.FragmentPrivacyAndTermsBinding

class PrivacyAndTermsFragment : Fragment(R.layout.fragment_privacy_and_terms) {

    private var _binding: FragmentPrivacyAndTermsBinding? = null
    private val binding
        get() = _binding!!

    private val args: PrivacyAndTermsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPrivacyAndTermsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.tvPrivacyAndTermsHeading.text = getString(args.stringResTitleId)
        binding.tvPrivacyAndTerms.text = Html.fromHtml(getString(args.stringResBodyId))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

