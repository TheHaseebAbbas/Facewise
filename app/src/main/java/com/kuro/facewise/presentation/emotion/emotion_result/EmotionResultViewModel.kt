package com.kuro.facewise.presentation.emotion.emotion_result

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
class EmotionResultViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmotionResultState())
    val state = _state.asStateFlow()

    fun onEvent(event: EmotionResultEvent) {
        when (event) {
            is EmotionResultEvent.GetRelevantEmotionData -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isLoading = true
                    )
                    repository.getRelevantEmotionData(event.emotion)
                        .collectLatest { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _state.value = _state.value.copy(
                                        islamicData = result.data,
                                        isLoading = false
                                    )
                                }

                                is Resource.Error -> {
                                    _state.value = _state.value.copy(
                                        error = UiText.DynamicString(result.message!!),
                                        isLoading = false
                                    )
                                }

                                is Resource.Loading -> Unit
                            }
                        }
                }
            }

            is EmotionResultEvent.PutEmotionResult -> {
                viewModelScope.launch {
                    repository.putEmotionResult(event.emotionResult)
                        .collectLatest { result ->
                            when (result) {
                                is Resource.Error -> {
                                    _state.emit(
                                        EmotionResultState(
                                            error = UiText.DynamicString(result.message!!)
                                        )
                                    )
                                }
                                is Resource.Success -> {
                                    _state.emit(
                                        EmotionResultState(
                                            isSuccess = true
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