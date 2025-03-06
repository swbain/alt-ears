package com.pavlovsfrog.altears

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(State(text = "hello world"))
    val state = _state.asStateFlow()
}

data class State(val text: String)