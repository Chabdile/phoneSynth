package com.example.phonesynth.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorViewModel : ViewModel() {
    private val _state = MutableStateFlow("Hello from SensorViewModel")
    val state: StateFlow<String> = _state
}