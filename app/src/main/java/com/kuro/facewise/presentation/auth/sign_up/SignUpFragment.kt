package com.kuro.facewise.presentation.auth.sign_up

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentSignUpBinding
import com.kuro.facewise.util.addAfterTextChangeListener
import com.kuro.facewise.util.click
import com.kuro.facewise.util.makeLinks
import com.kuro.facewise.util.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val viewModel by viewModels<SignUpViewModel>()

    private var _binding: FragmentSignUpBinding? = null
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
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    if (it.isLoading) return@repeatOnLifecycle
                    if (it.isSuccess) {
                        findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
                    }
                    if (it.error != null) {
                        handleSignUpError(it.error)
                    }
                }
            }
        }
    }

    private fun handleSignUpError(error: LoginError) {
        when (error) {
            is LoginError.InvalidConfirmPassword ->
                binding.etUserConfirmPassword.error = error.message?.asString(requireContext())

            is LoginError.InvalidEmail ->
                binding.etUserEmail.error = error.message?.asString(requireContext())

            is LoginError.InvalidName ->
                binding.etUserName.error = error.message?.asString(requireContext())

            is LoginError.InvalidPassword ->
                binding.etUserPassword.error = error.message?.asString(requireContext())

            is LoginError.ServerError ->
                showLongToast(error.message!!.asString(requireContext()))

            is LoginError.NotMatchPassword -> {
                binding.etUserPassword.error = error.message?.asString(requireContext())
                binding.etUserConfirmPassword.error = error.message?.asString(requireContext())
            }
        }
    }

    private fun setListeners() {
        setTermsConditionsAndPolicy()

        binding.btnSignIn click {
            findNavController().popBackStack()
        }

        binding.btnSignUp click {
            viewModel.onEvent(SignUpEvent.OnSignUp)
        }

        addAfterTextChangeListener(
            Pair(binding.etUserName, getString(R.string.enter_a_name_please)),
            Pair(binding.etUserEmail, getString(R.string.enter_an_email_please)),
            Pair(binding.etUserPassword, getString(R.string.enter_a_password_please)),
            Pair(binding.etUserConfirmPassword, getString(R.string.enter_password_again_please)),
        )
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