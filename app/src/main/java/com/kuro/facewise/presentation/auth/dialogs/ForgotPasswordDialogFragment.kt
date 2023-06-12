package com.kuro.facewise.presentation.auth.dialogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentForgotPasswordDialogBinding
import com.kuro.facewise.util.UiText
import com.kuro.facewise.util.addAfterTextChangeListener
import com.kuro.facewise.util.click
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordDialogFragment : DialogFragment(R.layout.fragment_forgot_password_dialog) {

    private val viewModel by viewModels<ForgotPasswordDialogViewModel>()

    private lateinit var binding: FragmentForgotPasswordDialogBinding

    private var submitClickListener: SubmitClickListener? = null

    interface SubmitClickListener {
        fun onSubmitted(message: UiText?)
    }

    companion object {
        fun newInstance(onSubmitted: (message: UiText?) -> Unit): ForgotPasswordDialogFragment =
            ForgotPasswordDialogFragment().apply {
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
        binding = FragmentForgotPasswordDialogBinding.bind(view)
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
                            R.string.a_link_to_reset_your_password_has_been_sent_to_your_email
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

    private fun handlingErrors(error: ForgotPasswordDialogError) {
        when (error) {
            is ForgotPasswordDialogError.InvalidEmailError ->
                binding.etEnterEmailAddress.error = error.message?.asString(requireContext())

            is ForgotPasswordDialogError.ServerError -> {
                submitClickListener?.onSubmitted(error.message)
                dismiss()
            }
        }
    }


    private fun setListeners() {
        binding.btnSendEmail click {
            viewModel.onEvent(ForgotPasswordDialogEvent.OnSendEmailClick)
        }
        addAfterTextChangeListener(
            Pair(binding.etEnterEmailAddress, getString(R.string.enter_an_email_please))
        )
    }
}