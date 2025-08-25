package com.example.phonesynth.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InstViewModel::class.java) -> InstViewModel(application) as T
            modelClass.isAssignableFrom(InstEnsembleViewModel::class.java) -> InstEnsembleViewModel(application) as T
            modelClass.isAssignableFrom(SensorViewModel::class.java) -> SensorViewModel(application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
