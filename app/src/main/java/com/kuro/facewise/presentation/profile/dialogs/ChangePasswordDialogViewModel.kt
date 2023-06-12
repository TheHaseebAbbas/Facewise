package com.kuro.facewise.presentation.profile.dialogs

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.presentation.auth.sign_up.SignUpError
import com.kuro.facewise.presentation.auth.sign_up.SignUpState
import com.kuro.facewise.util.Resource
import com.kuro.facewise.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordDialogViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordDialogState())
    val state = _state.asStateFlow()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var oldPassword = ObservableField("")
    var newPassword = ObservableField("")
    var newConfirmPassword = ObservableField("")

    fun onEvent(event: ChangePasswordDialogEvent) {
        when (event) {
            ChangePasswordDialogEvent.OnUpdateClicked -> {
                if (validateUserInfo()) {
                    viewModelScope.launch {
                        _state.emit(ChangePasswordDialogState(isLoading = true))
                        repository.updatePassword(
                            email = userEmail!!,
                            oldPassword = oldPassword.get()!!.trim(),
                            newPassword = newPassword.get()!!.trim()
                        )
                            .collectLatest { result ->
                                when (result) {
                                    is Resource.Error -> {
                                        _state.emit(
                                            ChangePasswordDialogState(
                                                error = ChangePasswordDialogError.ServerError(
                                                    message = UiText.DynamicString(
                                                        result.message!!
                                                    )
                                                ),
                                            )
                                        )
                                    }
                                    is Resource.Success -> {
                                        _state.emit(
                                            ChangePasswordDialogState(
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
        }
    }

    private fun validateUserInfo(): Boolean {
        var isValid = true
        if (oldPassword.get().isNullOrBlank()) {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.InvalidOldPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_old_password
                        )
                    )
                )
            isValid = false
        }
        if (!newPassword.get().equals(newConfirmPassword.get())) {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.MatchPasswordError(
                        message = UiText.StringResource(
                            R.string.new_password_and_confirm_new_password_should_be_same
                        )
                    )
                )
            isValid = false
        } else {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.MatchPasswordError(
                        message = null
                    )
                )
        }
        if (newPassword.get().isNullOrBlank()) {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.InvalidNewPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_new_password
                        )
                    )
                )
            isValid = false
        } else if (newPassword.get()!!.length < 8) {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.InvalidNewPasswordError(
                        message = UiText.StringResource(
                            R.string.password_length_must_be_of_8_characters
                        )
                    )
                )
            isValid = false
        }
        if (newConfirmPassword.get().isNullOrBlank()) {
            _state.value =
                ChangePasswordDialogState(
                    error = ChangePasswordDialogError.InvalidNewConfirmPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_new_password_again
                        )
                    )
                )
            isValid = false
        }

        return isValid
    }
}