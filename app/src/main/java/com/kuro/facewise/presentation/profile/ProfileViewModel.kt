package com.kuro.facewise.presentation.profile

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import com.kuro.facewise.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val currentUser = FirebaseAuth.getInstance().currentUser

    var userName = ObservableField(currentUser?.displayName)

    val userEmail = currentUser!!.email

    val userPassword = currentUser!!.uid
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnUpdateClicked -> {
                requestUpdateProfile(event.uri)
            }

            is ProfileEvent.OnImageResult -> {
                _state.value = _state.value.copy(
                    imageUri = event.uri
                )
            }
        }
    }

    private fun requestUpdateProfile(uri: Uri?) {
        if (validateInfo()) {
            viewModelScope.launch {
                _state.emit(ProfileState(isLoading = true))
                repository.updateProfile(
                    name = userName.get()!!.trim(),
                    imageUri = uri
                )
                    .collectLatest { result ->
                        when (result) {
                            is Resource.Error -> {
                                _state.emit(
                                    ProfileState(
                                        error = ProfileError.ServerError(
                                            message = UiText.DynamicString(
                                                result.message!!
                                            )
                                        )
                                    )
                                )
                            }

                            is Resource.Success -> {
                                _state.emit(
                                    ProfileState(
                                        isSuccess = true,
                                    )
                                )
                            }

                            is Resource.Loading -> Unit
                        }
                    }
            }
        }
    }

    private fun validateInfo(): Boolean {
        var isValid = true
        if (userName.get().isNullOrBlank()) {
            _state.value =
                ProfileState(error = ProfileError.InvalidNameError(message = UiText.StringResource(R.string.enter_a_name_please)))
            isValid = false
        }
        return isValid
    }
}