package com.kuro.facewise.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MainViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnMainFabClick -> {
                _state.value = _state.value.copy(
                    areAllFabVisible = _state.value.areAllFabVisible.not()
                )
            }

            is MainEvent.OnImageResult -> {
                _state.value = _state.value.copy(
                    imageUri = event.uri,
                )
            }

            MainEvent.OnGetLastEmotionResult -> {
                viewModelScope.launch {
                    repository.getLastEmotionResult()
                        .collectLatest { result ->
                            when (result) {
                                is Resource.Error -> {
                                    _state.value = _state.value.copy(
                                        error = UiText.DynamicString(result.message!!)
                                    )
                                }

                                is Resource.Success -> {
                                    _state.value = _state.value.copy(
                                        lastEmotionResult = result.data
                                    )
                                }

                                is Resource.Loading -> Unit
                            }
                        }
                }
            }

            MainEvent.OnGetRandomIslamicData -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isLoading = true
                    )
                    repository.getRandomIslamicData()
                        .collectLatest { result ->
                            when (result) {
                                is Resource.Error -> {
                                    _state.value = _state.value.copy(
                                        error = UiText.DynamicString(result.message!!),
                                        isLoading = false
                                    )
                                }

                                is Resource.Success -> {
                                    _state.value = _state.value.copy(
                                        islamicData = result.data,
                                        isLoading = false
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
