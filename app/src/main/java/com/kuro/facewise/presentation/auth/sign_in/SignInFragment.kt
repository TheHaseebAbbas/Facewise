package com.kuro.facewise.presentation.auth.sign_in

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentSignInBinding
import com.kuro.facewise.util.addAfterTextChangeListener
import com.kuro.facewise.util.click
import com.kuro.facewise.util.makeLinks
import com.kuro.facewise.util.showLongSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val viewModel by viewModels<SignInViewModel>()

    private var _binding: FragmentSignInBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = DataBindingUtil.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.isLoading = it.isLoading
                if (it.isLoading) return@collectLatest
                if (it.isSuccess) {
                    findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
                }
                if (it.error != null) {
                    handlingErrors(it.error)
                }
            }
        }
    }

    private fun handlingErrors(error: SignInError) {
        when (error) {
            is SignInError.InvalidEmailError ->
                binding.etUserEmail.error = error.message?.asString(requireContext())

            is SignInError.InvalidPasswordError ->
                binding.etUserPassword.error = error.message?.asString(requireContext())

            is SignInError.ServerError ->
                binding.root.showLongSnackBar(error.message!!.asString(requireContext()))
        }
    }

    private fun setListeners() {
        setTermsConditionsAndPolicy()

        binding.btnSignUp click {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnSignIn click {
            viewModel.onEvent(SignInEvent.OnSignIn)
        }

        addAfterTextChangeListener(
            Pair(binding.etUserEmail, getString(R.string.enter_an_email_please)),
            Pair(binding.etUserPassword, getString(R.string.enter_a_password_please)),
        )
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
