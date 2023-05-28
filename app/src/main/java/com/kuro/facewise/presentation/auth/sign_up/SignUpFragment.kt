package com.kuro.facewise.presentation.auth.sign_up

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentOnBoardingBinding
import com.kuro.facewise.databinding.FragmentSignUpBinding
import com.kuro.facewise.util.click
import com.kuro.facewise.util.makeLinks

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSignUpBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        setTermsConditionsAndPolicy()

        binding.btnSignIn click {
            findNavController().popBackStack()
        }
    }

    private fun setTermsConditionsAndPolicy() {
        binding.txtAgreeToTerms.makeLinks(
            Pair("Terms & Conditions", View.OnClickListener {
                Toast.makeText(requireContext(), "Terms & Conditions", Toast.LENGTH_SHORT).show()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                Toast.makeText(requireContext(), "Privacy Policy", Toast.LENGTH_SHORT).show()
            })
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}