package com.example.phonesynth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonesynth.component.Sensor
import kotlin.math.roundToInt
import com.example.phonesynth.ui.theme.*

@Composable
fun MakePadButton(
    AssignedFunBeforeRelease: @Composable () -> Unit,
    AssignedFunAfterRelease: @Composable () -> Unit,
    textInput: String = "",
    width: Float = 1.0f,
    height: Dp = 200.dp,
) {
    var isPressed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // 押してる
                        isPressed = true

                        tryAwaitRelease()
                        // 離した
                        isPressed = false
                    }
                )
            }
            .fillMaxWidth(width)
            .height(height)
            .background(color = Purple200)
    ) {
        if (isPressed) {
            AssignedFunBeforeRelease()
        } else {
            AssignedFunAfterRelease()
        }

        Text(text = textInput)
    }
}

@Composable
private fun MakeDraggable(
    sensorViewModel: Sensor = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 300.dp)
    ) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Purple500)
                .size(40.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        )
        sensorViewModel.articulation.value = 0.5f + (offsetX / 1000)
        sensorViewModel.angle.x.value = (offsetY / 10)
    }
}

@Composable
fun MakeTransposeSlider(
    sensorViewModel: Sensor = viewModel()
) {
    var sliderPosition by remember { mutableStateOf(0f) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                sensorViewModel.transpose = sliderPosition.toInt()
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 11,
            valueRange = 0f..12f
        )
        Text(text = sliderPosition.toString())
    }
}
@Composable
fun MakeSlider(
    sensorViewModel: Sensor = viewModel()
) {
    var sliderPosition by remember { mutableStateOf(0f) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
//                sensorViewModel.transpose = sliderPosition.toInt()
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 11,
            valueRange = 0f..12f
        )
        Text(text = sliderPosition.toString())
    }
}

@Composable
fun MakeSwitch(
    AssignedFun: @Composable () -> Unit,
    textInput: String = "",
) {
    var isChecked by remember { mutableStateOf(true) }

    Row {
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
            }
        )
        if (isChecked) {
            AssignedFun()
        }
        Text(text = textInput)
    }
}