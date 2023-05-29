package com.kuro.facewise.presentation.auth.sign_in

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
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    var userEmail = ObservableField("")
    var userPassword = ObservableField("")

    fun onEvent(event: SignInEvent) {
        when (event) {
            SignInEvent.OnSignIn -> requestSignIn()
        }
    }

    private fun requestSignIn() {
        if (validateUserInfo()) {
            viewModelScope.launch {
                _state.emit(SignInState(isLoading = true))
                repository.signIn(userEmail.get()!!.trim(), userPassword.get()!!.trim())
                    .collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.emit(
                                    SignInState(
                                        isSuccess = true,
                                    )
                                )
                            }

                            is Resource.Error -> {
                                _state.emit(
                                    SignInState(
                                        error = SignInError.ServerError(
                                            message = UiText.DynamicString(
                                                result.message!!
                                            )
                                        ),
                                    )
                                )
                            }

                            is Resource.Loading -> Unit
                        }
                    }
            }
        }
    }

    private fun validateUserInfo(): Boolean {
        var isValid = true
        if (userEmail.get().isNullOrBlank()) {
            _state.value =
                SignInState(error = SignInError.InvalidEmailError(message = UiText.StringResource(R.string.enter_an_email_please)))
            isValid = false
        }
        if (userPassword.get().isNullOrBlank()) {
            _state.value =
                SignInState(
                    error = SignInError.InvalidPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_a_password_please
                        )
                    )
                )
            isValid = false
        }

        return isValid
    }
}