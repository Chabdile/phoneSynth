package com.example.phonesynth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.phonesynth.component.Sensors.Companion.accel
import com.example.phonesynth.component.Sensors.Companion.linearAccel
import com.example.phonesynth.component.Sensors.Companion.velocity
import com.example.phonesynth.component.Sensors.Companion.position
import com.example.phonesynth.component.Sensors.Companion.gyro
import com.example.phonesynth.component.Sensors.Companion.angle
import com.example.phonesynth.component.*
import kotlin.math.roundToInt
import com.example.phonesynth.ui.theme.*

@Composable
fun MakePadButton(
    onPressedFun: @Composable () -> Unit,
    onReleasedFun: @Composable () -> Unit,
    icon: Int = 0,
    textInput: String = "",
    width: Dp = 100.dp,
    height: Dp = 200.dp,
) {
    var isPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(width, height)
//            .border(
//                width = 0.dp,
//                color = Silver,
//                shape = RoundedCornerShape(10.dp)
//            )
            .background(
                color = LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // 押してる
                        isPress = true

                        tryAwaitRelease()
                        // 離した
                        isPress = false
                    }
                )
            }
    ) {
        if (isPress) {
            onPressedFun()
        } else {
            onReleasedFun()
        }

        Box(
            modifier = Modifier
                .size(width, height),
            contentAlignment = Alignment.Center
        ) {
            Row {
                if (icon != 0) {
                    Image(
                        painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp, 30.dp)
                    )
                }
                Column {
                    Text(text = textInput)
                }
            }
        }
    }
}

//@Composable
//fun MakeDropShadow {
//
//}

@Composable
fun MakeChangeWFButton(
    onPressedFun: @Composable () -> Unit,
    textInput: String = "",
    width: Dp = 80.dp,
    height: Dp = 80.dp,
) {
    var isPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(width, height)
            .border(
                width = 1.dp,
                color = WhiteSmoke,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = { isPress = true })
            .background(
                color = DarkGray,
                shape = RoundedCornerShape(10.dp)
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        if (isPress) {
            onPressedFun()
            isPress = false
        }

        Text(
            text = textInput,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MakeDraggable(sensors: Sensors) {
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
        sensors.repo.articulation.value = 0.5f + (offsetX / 1000)
        angle.x.value = (offsetY / 10)
    }
}

@Composable
fun MakeTransposeSlider(sensors: Sensors) {
    var sliderPosition by remember { mutableStateOf(0f) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                sensors.repo.transpose = sliderPosition.toInt()
            },
//            onValueChangeFinished = {
//                sensor.transpose = sliderPosition.toInt()
//            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 11,
            valueRange = 0f..12f
        )
//        Text(text = sliderPosition.toString())
    }
}
@Composable
fun MakeSlider() {
    var sliderPosition by remember { mutableStateOf(0f) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
//                sensor.transpose = sliderPosition.toInt()
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 11,
            valueRange = 0f..12f
        )
//        Text(text = sliderPosition.toString())
    }
}

@Composable
fun MakeSwitch(
    assignedFun: @Composable () -> Unit,
    textInput: String = "",
) {
    var isChecked by remember { mutableStateOf(true) }

    Row {
        Switch(
            onCheckedChange = {
                isChecked = it
            },
            checked = isChecked
        )
        if (isChecked) {
            assignedFun()
        }
        Text(text = textInput)
    }
}