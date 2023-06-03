package com.kuro.facewise.presentation.profile

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.kuro.facewise.MainNavGraphDirections
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentPrivacyAndTermsBinding
import com.kuro.facewise.databinding.FragmentProfileBinding
import com.kuro.facewise.presentation.privacy_and_terms.PrivacyAndTermsFragmentArgs
import com.kuro.facewise.util.click

class ProfileFragment : Fragment(R.layout.fragment_profile){
    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

binding.ivUpdateProfile click {
    showPopUpMenu()
}
    }
   private fun showPopUpMenu(){
       PopupMenu(requireContext(),binding.ivUpdateProfile).apply {
           inflate(R.menu.edit_profile_image_menu)
           show()
           setOnMenuItemClickListener {
               when (it.itemId) {
                   R.id.chooseImage -> {
                       Toast.makeText(requireContext(),"Choose Image",Toast.LENGTH_SHORT).show()
                   }

                   R.id.takePhoto -> {
                       Toast.makeText(requireContext(), "Take Photo", Toast.LENGTH_SHORT).show()
                   }

                   else -> {
                       Toast.makeText(requireContext(), "Remove Photo", Toast.LENGTH_SHORT).show()
                   }
               }
               true
           }
       }
   }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}