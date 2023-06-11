package com.kuro.facewise.presentation.auth.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentForgotPasswordDialogBinding
import com.kuro.facewise.util.click

class ForgotPasswordDialogFragment : DialogFragment(R.layout.fragment_forgot_password_dialog) {

    private lateinit var binding : FragmentForgotPasswordDialogBinding

    private var submitClickListener: SubmitClickListener? = null

    interface SubmitClickListener {
        fun onSubmitted()
    }

    companion object{
        fun newInstance(onSubmitted: () -> Unit) : ForgotPasswordDialogFragment = ForgotPasswordDialogFragment().apply {
            this.submitClickListener = object : SubmitClickListener {
                override fun onSubmitted() {
                    onSubmitted()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentForgotPasswordDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.btnSendLink click {
            submitClickListener?.onSubmitted()
            dismiss()
        }
    }

}