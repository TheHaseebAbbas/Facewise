package com.kuro.facewise.presentation.auth.sign_up

import android.util.Log
import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuro.facewise.R
import com.kuro.facewise.domain.repository.FirebaseRepository
import com.kuro.facewise.util.Resource
import com.kuro.facewise.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    companion object {
        const val TAG = "SignUpViewModel"
    }

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    var userName = ObservableField("")
    var userEmail = ObservableField("")
    var userPassword = ObservableField("")
    var userConfirmPassword = ObservableField("")

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnSignUp -> {
                if (validateUserInfo()) {
                    viewModelScope.launch {
                        _state.emit(SignUpState(isLoading = true))
                        repository.signUp(userEmail.get()!!, userPassword.get()!!)
                            .collectLatest { result ->
                                when (result) {
                                    is Resource.Error -> {
                                        _state.emit(
                                            SignUpState(
                                                error = LoginError.ServerError(message = UiText.DynamicString(result.message!!))
                                            )
                                        )
                                    }

                                    is Resource.Success -> {
                                        _state.emit(
                                            SignUpState(
                                                isSuccess = true,
                                                user = result.data,
                                            )
                                        )
                                    }

                                    is Resource.Loading -> Unit
                                }
                            }
                    }
                }
            }

            SignUpEvent.OnEdittextChanged -> TODO()
        }
    }

    private fun validateUserInfo(): Boolean {
        var isValid = true
        if (userName.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = LoginError.InvalidName(message = UiText.StringResource(R.string.enter_a_name_please)))
            isValid = false
        }
        if (userEmail.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = LoginError.InvalidEmail(message = UiText.StringResource(R.string.enter_an_email_please)))
            isValid = false
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), userEmail.get()!!)) {
            _state.value =
                SignUpState(error = LoginError.InvalidEmail(message = UiText.StringResource(R.string.enter_a_valid_email_please)))
            isValid = false
        }
        if (!userConfirmPassword.get().equals(userPassword.get())) {
            _state.value =
                SignUpState(error = LoginError.NotMatchPassword(message = UiText.StringResource(R.string.password_and_confirm_password_should_be_same)))
            isValid = false
        } else {
            _state.value =
                SignUpState(error = LoginError.NotMatchPassword(message = null))
        }
        if (userPassword.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = LoginError.InvalidPassword(message = UiText.StringResource(R.string.enter_a_password_please)))
            isValid = false
        } else if (userPassword.get()!!.length < 8) {
            _state.value =
                SignUpState(error = LoginError.InvalidPassword(message = UiText.StringResource(R.string.password_length_must_be_of_8_characters)))
            isValid = false
        }
        if (userConfirmPassword.get().isNullOrBlank()) {
            _state.value =
                SignUpState(error = LoginError.InvalidConfirmPassword(message = UiText.StringResource(R.string.enter_password_again_please)))
            isValid = false
        }

        return isValid
    }


}