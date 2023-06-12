package com.kuro.facewise.presentation.profile.dialogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentChangePasswordDialogBinding
import com.kuro.facewise.util.UiText
import com.kuro.facewise.util.addAfterTextChangeListener
import com.kuro.facewise.util.click
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordDialogFragment : DialogFragment(R.layout.fragment_change_password_dialog) {

    private val viewModel by viewModels<ChangePasswordDialogViewModel>()

    private lateinit var binding: FragmentChangePasswordDialogBinding

    private var submitClickListener: SubmitClickListener? = null

    interface SubmitClickListener {
        fun onSubmitted(message: UiText?)
    }

    companion object {
        fun newInstance(onSubmitted: (message: UiText?) -> Unit): ChangePasswordDialogFragment =
            ChangePasswordDialogFragment().apply {
                this.submitClickListener = object : SubmitClickListener {
                    override fun onSubmitted(message: UiText?) {
                        onSubmitted(message)
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChangePasswordDialogBinding.bind(view)
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
                    submitClickListener?.onSubmitted(
                        message = UiText.StringResource(
                            R.string.password_updated_successfully
                        )
                    )
                    dismiss()
                }
                if (it.error != null) {
                    handlingErrors(it.error)
                }
            }
        }
    }

    private fun handlingErrors(error: ChangePasswordDialogError) {
        when (error) {
            is ChangePasswordDialogError.InvalidOldPasswordError ->
                binding.etOldPassword.error = error.message?.asString(requireContext())

            is ChangePasswordDialogError.InvalidNewPasswordError ->
                binding.etNewPassword.error = error.message?.asString(requireContext())

            is ChangePasswordDialogError.InvalidNewConfirmPasswordError ->
                binding.etConfirmNewPassword.error = error.message?.asString(requireContext())

            is ChangePasswordDialogError.MatchPasswordError -> {
                binding.etNewPassword.error = error.message?.asString(requireContext())
                binding.etConfirmNewPassword.error = error.message?.asString(requireContext())
            }

            is ChangePasswordDialogError.ServerError -> {
                submitClickListener?.onSubmitted(error.message)
                dismiss()
            }
        }
    }

    private fun setListeners() {
        binding.btnUpdate click {
            viewModel.onEvent(ChangePasswordDialogEvent.OnUpdateClicked)
        }

        addAfterTextChangeListener(
            Pair(binding.etOldPassword, getString(R.string.enter_old_password)),
            Pair(binding.etNewPassword, getString(R.string.enter_new_password)),
            Pair(binding.etConfirmNewPassword, getString(R.string.enter_new_password_again)),
        )
    }
}