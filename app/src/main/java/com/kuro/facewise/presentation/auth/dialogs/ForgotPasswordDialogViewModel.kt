package com.kuro.facewise.presentation.auth.dialogs

import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuro.facewise.R
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import com.kuro.facewise.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordDialogViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordDialogState())
    val state = _state.asStateFlow()

    var userEmail = ObservableField("")

    fun onEvent(event: ForgotPasswordDialogEvent) {
        when (event) {
            ForgotPasswordDialogEvent.OnSendEmailClick -> {
                if (validateUserInfo()) {
                    viewModelScope.launch {
                        _state.emit(ForgotPasswordDialogState(isLoading = true))
                        repository.sendPasswordResetEmail(
                            email = userEmail.get()!!.trim()
                        )
                            .collectLatest { result ->
                                when (result) {
                                    is Resource.Error -> {
                                        _state.emit(
                                            ForgotPasswordDialogState(
                                                error = ForgotPasswordDialogError.ServerError(
                                                    message = UiText.DynamicString(
                                                        result.message!!
                                                    )
                                                ),
                                            )
                                        )
                                    }

                                    is Resource.Success -> {
                                        _state.emit(
                                            ForgotPasswordDialogState(
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
        if (userEmail.get().isNullOrBlank()) {
            _state.value =
                ForgotPasswordDialogState(
                    error = ForgotPasswordDialogError.InvalidEmailError(
                        message = UiText.StringResource(R.string.enter_an_email_please)
                    )
                )
            isValid = false
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), userEmail.get()!!)) {
            _state.value =
                ForgotPasswordDialogState(
                    error = ForgotPasswordDialogError.InvalidEmailError(
                        message = UiText.StringResource(R.string.enter_a_valid_email_please)
                    )
                )
            isValid = false
        }

        return isValid
    }
}