package com.example.phonesynth.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import com.example.phonesynth.ui.InstrumentPanel
import com.example.phonesynth.viewModel.InstViewModel

@Composable
fun InstScreen(viewModel: InstViewModel) {
    LaunchedEffect(Unit) {
        viewModel.volume.floatValue = 0f
        viewModel.startStream()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopStream()
        }
    }

    InstrumentPanel(viewModel.sensors.repo) { viewModel.setWaveform(it) }
    viewModel.updateAudio()
    viewModel.mapping()
}
