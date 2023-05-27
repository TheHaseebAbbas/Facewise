package com.kuro.facewise.presentation.auth.sign_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentSignInBinding
import com.kuro.facewise.databinding.FragmentSignUpBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSignInBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
