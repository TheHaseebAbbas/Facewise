package com.kuro.facewise.presentation.profile.dialogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentChangePasswordDialogBinding
import com.kuro.facewise.util.click

class ChangePasswordDialogFragment : DialogFragment(R.layout.fragment_change_password_dialog) {

    private lateinit var binding: FragmentChangePasswordDialogBinding

    private var submitClickListener: SubmitClickListener? = null

    interface SubmitClickListener {
        fun onSubmitted()
    }

    companion object {
        fun newInstance(onSubmitted: () -> Unit): ChangePasswordDialogFragment =
            ChangePasswordDialogFragment().apply {
                this.submitClickListener = object : SubmitClickListener {
                    override fun onSubmitted() {
                        onSubmitted()
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

        setListeners()
    }

    private fun setListeners() {
        binding.btnUpdate click {
            submitClickListener?.onSubmitted()
            dismiss()
        }
    }
}