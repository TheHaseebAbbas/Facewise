package com.kuro.facewise.presentation.emotion.emotion_recognition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuro.facewise.domain.repository.FaceWiseRepository
import com.kuro.facewise.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EmotionRecognitionViewModel @Inject constructor(
    private val repository: FaceWiseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmotionRecognitionState())
    val state = _state.asStateFlow()

    fun onEvent(event: EmotionRecognitionEvent) {
        when (event) {
            is EmotionRecognitionEvent.OnEmotionRecognition -> {
                repository.getEmotions(event.imageUri).onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _state.value = EmotionRecognitionState(
                                error = result.message!!
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = EmotionRecognitionState(
                                isLoading = true
                            )
                        }

                        is Resource.Success -> {
                            _state.value = EmotionRecognitionState(
                                emotion = result.data
                            )
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

}