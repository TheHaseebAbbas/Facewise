package com.kuro.facewise.presentation.profile.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentChangePasswordDialogBinding
import com.kuro.facewise.databinding.FragmentMainBinding

class ChangePasswordDialogFragment : DialogFragment(R.layout.fragment_change_password_dialog) {

    private lateinit var binding : FragmentChangePasswordDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChangePasswordDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)


    }


}