package com.kuro.facewise.presentation.main.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentConfirmationDialogBinding
import com.kuro.facewise.util.click

class ConfirmationDialogFragment : DialogFragment(R.layout.fragment_confirmation_dialog) {

    private lateinit var binding: FragmentConfirmationDialogBinding

    private var submitClickListener: SubmitClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentConfirmationDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.btnAction click {
            submitClickListener?.onSubmitted()
            dismiss()
        }
    }

    interface SubmitClickListener {
        fun onSubmitted()
    }

    companion object {
        fun newInstance(onSubmitted: () -> Unit): ConfirmationDialogFragment =
            ConfirmationDialogFragment().apply {
                this.submitClickListener = object : SubmitClickListener {
                    override fun onSubmitted() {
                        onSubmitted()
                    }
                }
            }
    }
}