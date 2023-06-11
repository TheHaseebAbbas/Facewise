package com.kuro.facewise.presentation.auth.sign_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kuro.facewise.MainNavGraphDirections
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentSignInBinding
import com.kuro.facewise.presentation.auth.sign_up.SignUpFragment
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

    private lateinit var binding: FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentBackStackEntry = findNavController()
            .currentBackStackEntry!!
        currentBackStackEntry
            .savedStateHandle
            .getLiveData<Boolean>(SignUpFragment.IS_SIGN_UP_SUCCESSFUL)
            .observe(currentBackStackEntry) {
                if (it)
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSignInBinding.bind(view)
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
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
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
                findNavController().navigate(
                    MainNavGraphDirections.actionGlobalPrivacyAndTermsFragment(
                        R.string.facewise_terms_conditions_title,
                        R.string.facewise_terms_codition
                    )
                )
            }),
            Pair("Privacy Policy", View.OnClickListener {
                findNavController().navigate(
                    MainNavGraphDirections.actionGlobalPrivacyAndTermsFragment(
                        R.string.facewise_privacy_policy_tittle,
                        R.string.facewise_privacy_policy
                    )
                )
            })
        )
    }
}
