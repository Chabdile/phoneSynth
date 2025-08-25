package com.example.phonesynth.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.phonesynth.component.Sensors

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state

    val sensors = Sensors(application)

    override fun onCleared() {
        super.onCleared()
        sensors.unregisterSensors()
    }
}