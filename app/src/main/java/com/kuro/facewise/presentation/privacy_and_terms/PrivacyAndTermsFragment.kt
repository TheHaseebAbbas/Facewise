package com.kuro.facewise.presentation.privacy_and_terms

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.navArgs
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentPrivacyAndTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyAndTermsFragment : Fragment(R.layout.fragment_privacy_and_terms) {

    private lateinit var binding: FragmentPrivacyAndTermsBinding

    private val args: PrivacyAndTermsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPrivacyAndTermsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.tvPrivacyAndTermsHeading.text = getString(args.stringResTitleId)
        binding.tvPrivacyAndTerms.text = Html.fromHtml(getString(args.stringResBodyId), Html.FROM_HTML_MODE_COMPACT)
    }
}
