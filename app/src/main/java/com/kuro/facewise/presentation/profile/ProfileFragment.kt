package com.kuro.facewise.presentation.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.databinding.FragmentProfileBinding
import com.kuro.facewise.presentation.profile.dialogs.ChangePasswordDialogFragment
import com.kuro.facewise.util.ImageUtils
import com.kuro.facewise.util.click
import com.kuro.facewise.util.constants.AppConstants
import com.kuro.facewise.util.createImageUri
import com.kuro.facewise.util.showLongSnackBar
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel by viewModels<ProfileViewModel>()

    private lateinit var binding: FragmentProfileBinding

    private var imageUri: Uri? = null

    private val openCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->
        result?.let {
            if (it) {
                Log.d("TAG", "$imageUri: ")
                binding.imageUri = imageUri.toString()
            }
        }
    }

    private val chooseImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                Log.d("TAG", "$imageUri: ")
                viewModel.onEvent(ProfileEvent.OnImageResult(it.data))
                binding.imageUri = imageUri.toString()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.imageUri = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                imageUri = it.imageUri
                binding.isLoading = it.isLoading
                if (it.isLoading) return@collectLatest
                if (it.isSuccess) {
                    binding.root.showLongSnackBar("Updated.")
                }
                if (it.error != null) {
                    handlingErrors(it.error)
                }
            }
        }
    }

    private fun handlingErrors(error: ProfileError) {
        when (error) {
            is ProfileError.InvalidNameError ->
                binding.etUserEmail.error = error.message?.asString(requireContext())

            is ProfileError.ServerError ->
                binding.root.showLongSnackBar(error.message!!.asString(requireContext()))
        }
    }

    private fun setListeners() {
        binding.btnUpdate click {
            lifecycleScope.launch {
                if (imageUri != null) {
                    val compressedImage = Compressor.compress(
                        requireActivity(),
                        ImageUtils.fileFromContentUri(requireActivity(), imageUri!!),
                    )
                    Log.d("TAG", "$imageUri: ")
                    viewModel.onEvent(ProfileEvent.OnUpdateClicked(compressedImage.toUri()))
                } else {
                    viewModel.onEvent(ProfileEvent.OnUpdateClicked(null))
                }
            }
        }
        binding.ivUpdateProfile click {
            showPopUpMenu()
        }

        binding.btnChangePassword click {
            ChangePasswordDialogFragment
                .newInstance {
                    binding.root.showLongSnackBar(it!!.asString(requireActivity()))
                }
                .show(childFragmentManager, "ChangePasswordDialogFragment")
        }
    }

    private fun showPopUpMenu() {
        PopupMenu(requireContext(), binding.ivUpdateProfile).apply {
            inflate(R.menu.edit_profile_image_menu)
            show()
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.chooseImage -> {
                        val intent =
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            ).apply {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                        chooseImage.launch(intent)
                    }

                    R.id.takePhoto -> {
                        viewModel.onEvent(
                            ProfileEvent.OnImageResult(
                                createImageUri(
                                    requireActivity().applicationContext,
                                    AppConstants.KEY_PROFILE_TEMP_IMAGE
                                )
                            )
                        )
                        openCamera.launch(imageUri!!)
                    }

                    else -> {
                        viewModel.onEvent(ProfileEvent.OnImageResult(null))
                        binding.imageUri = imageUri.toString()
                    }
                }
                true
            }
        }
    }
}