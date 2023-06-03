package com.kuro.facewise.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

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
        }
    }
}