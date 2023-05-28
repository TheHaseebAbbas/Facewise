package com.kuro.facewise.presentation.auth.sign_in

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentSignInBinding
import com.kuro.facewise.util.click
import com.kuro.facewise.util.makeLinks

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSignInBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        findNavController().navigate(R.id.action_global_emotionRecognitionFragment)
    }

    private fun setListeners() {
        setTermsConditionsAndPolicy()
        binding.btnSignUp click {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun setTermsConditionsAndPolicy() {
        binding.tvTermsConditionsAndPolicy.makeLinks(
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
