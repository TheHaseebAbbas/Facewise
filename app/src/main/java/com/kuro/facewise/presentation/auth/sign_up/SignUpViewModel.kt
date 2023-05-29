package com.kuro.facewise.presentation.auth.sign_up

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
class SignUpViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    var userName = ObservableField("")
    var userEmail = ObservableField("")
    var userPassword = ObservableField("")
    var userConfirmPassword = ObservableField("")
    var isTermsAndConditionsChecked = ObservableField(false)

    fun onEvent(event: SignUpEvent) {
        when (event) {
            SignUpEvent.OnSignUp -> requestSignUp()
        }
    }

    private fun requestSignUp() {
        if (validateUserInfo()) {
            if (!isTermsAndConditionsChecked.get()!!) {
                _state.value =
                    SignUpState(
                        error = SignUpError.TermsAndConditionsError(
                            message = UiText.StringResource(
                                R.string.agree_to_our_terms_conditions_and_privacy_policy
                            )
                        )
                    )
                return
            }
            viewModelScope.launch {
                _state.emit(SignUpState(isLoading = true))
                repository.signUp(
                    userName.get()!!.trim(),
                    userEmail.get()!!.trim(),
                    userPassword.get()!!.trim()
                )
                    .collectLatest { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.emit(
                                    SignUpState(
                                        isSuccess = true,
                                    )
                                )
                            }
                            is Resource.Error -> {
                                _state.emit(
                                    SignUpState(
                                        error = SignUpError.ServerError(
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
        if (userName.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = SignUpError.InvalidNameError(message = UiText.StringResource(R.string.enter_a_name_please)))
            isValid = false
        }
        if (userEmail.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = SignUpError.InvalidEmailError(message = UiText.StringResource(R.string.enter_an_email_please)))
            isValid = false
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), userEmail.get()!!)) {
            _state.value =
                SignUpState(error = SignUpError.InvalidEmailError(message = UiText.StringResource(R.string.enter_a_valid_email_please)))
            isValid = false
        }
        if (!userConfirmPassword.get().equals(userPassword.get())) {
            _state.value =
                SignUpState(error = SignUpError.MatchPasswordError(message = UiText.StringResource(R.string.password_and_confirm_password_should_be_same)))
            isValid = false
        } else {
            _state.value =
                SignUpState(error = SignUpError.MatchPasswordError(message = null))
        }
        if (userPassword.get().isNullOrBlank()) {
            _state.value =
                SignUpState(
                    error = SignUpError.InvalidPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_a_password_please
                        )
                    )
                )
            isValid = false
        } else if (userPassword.get()!!.length < 8) {
            _state.value =
                SignUpState(
                    error = SignUpError.InvalidPasswordError(
                        message = UiText.StringResource(
                            R.string.password_length_must_be_of_8_characters
                        )
                    )
                )
            isValid = false
        }
        if (userConfirmPassword.get().isNullOrBlank()) {
            _state.value =
                SignUpState(
                    error = SignUpError.InvalidConfirmPasswordError(
                        message = UiText.StringResource(
                            R.string.enter_password_again_please
                        )
                    )
                )
            isValid = false
        }

        return isValid
    }
}