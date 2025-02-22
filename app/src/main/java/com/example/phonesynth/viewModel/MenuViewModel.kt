package com.example.phonesynth.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MenuViewModel : ViewModel() {
    private val _state = MutableStateFlow("Hello from InstViewModel")
    val state: StateFlow<String> = _state
}