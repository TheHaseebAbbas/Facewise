package com.kuro.facewise.presentation.auth.sign_up

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentOnBoardingBinding
import com.kuro.facewise.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSignUpBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val spannableCheckboxString = SpannableString("By clicking on this, you're agreeing to the Terms & Conditions and Privacy Policy")

        val termsClickableSpan = object :ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(requireContext(), "Terms & Conditions", Toast.LENGTH_SHORT).show()
            }
        }
        val privacyClickableSpan = object :ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(requireContext(), "Privacy Policy", Toast.LENGTH_SHORT).show()
            }
        }
        spannableCheckboxString.setSpan(termsClickableSpan, 43, 62, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableCheckboxString.setSpan(privacyClickableSpan, 66, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.cbAgreeToTerms.text = spannableCheckboxString
        binding.cbAgreeToTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}