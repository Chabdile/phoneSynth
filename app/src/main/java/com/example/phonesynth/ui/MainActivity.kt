package com.example.phonesynth

import android.os.Bundle
//import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import com.example.phonesynth.ui.theme.PhoneSynthTheme
import com.example.phonesynth.ui.theme.*
import kotlin.math.log
import kotlin.math.roundToInt
import kotlin.math.sqrt

//.cppと共有したいけど決め打ち
//val waveform = arrayOf(
//    "Sine",
//    "Triangle",
//    "Sawtooth",
//    "Square"
//)
var currentWaveform: Int = 0
//---------------------------------------------------------------------
class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }



    private external fun startStream()
    private external fun stopStream()
    private external fun setFrequency(frequency: Float)
    private external fun setAmplitude(amplitude: Float)
    private external fun setWaveform(shape: Int)
    private external fun setSampleRate(sampleRate: Float)
    private external fun setPulseWidth(pulseWidth: Float)
    private external fun oscDestroy(instance: Long)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startStream()

        setContent {
            PhoneSynthTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SensorScreen(
                        onFrequencyChange = { setFrequency(it) },
                        onAmplitudeChange = { setAmplitude(it) },
                        onWaveformChange = { setWaveform(it) },
                        onPulseWidthChange = { setPulseWidth(it) }

                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStream()
    }
}

@Composable
fun SoundStop(
    tf: Boolean = false,
    sensorViewModel: SensorViewModel = viewModel()
) {
    sensorViewModel.isClick = tf
    sensorViewModel.isButtonPress(sensorViewModel.isClick)
}

@Composable
fun PitchSnap(
    tf: Boolean = true,
    sensorViewModel: SensorViewModel = viewModel()
) {
    sensorViewModel.isSnap = tf
}

//@Composable
//fun changeWaveform(
//    tf: Boolean = false
//): Int {
//    val wave1: Int = 1     //default
//    val wave2: Int = 3     //changed
//
//    return if (tf) wave2 else wave1
//}

@Composable
fun MakePadButton(
    AssignedFunBeforeRelease: @Composable () -> Unit,
    AssignedFunAfterRelease: @Composable () -> Unit,
    textInput: String = "",
    width: Float = 1.0f,
    height: Dp = 200.dp
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
//            verticalArrangement = Arrangement.SpaceAround
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
    sensorViewModel: SensorViewModel = viewModel()
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
fun MakeSlider(
    sensorViewModel: SensorViewModel = viewModel()
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
        Text(text = textInput)
        if (isChecked) {
            AssignedFun()
        }
    }
}

@Composable
fun SensorScreen(
    sensorViewModel: SensorViewModel = viewModel(),
    onFrequencyChange: (Float) -> Unit,
    onAmplitudeChange: (Float) -> Unit,
    onWaveformChange: (Int) -> Unit,
    onPulseWidthChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row {
            MakePadButton(
                { SoundStop(true) },
                { SoundStop() },
                textInput = "音を切る\n",
                0.5f
            )

            MakePadButton(
                { PitchSnap(false) },
                { PitchSnap() },
                textInput = "音階変更\n" +
                        "Pitch Snap: ${sensorViewModel.isSnap}\n" +
                    "インタラクティブに色を変えたり階名出したい\n",
                1.0f
            )
        }

        MakeSlider()

        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { sensorViewModel.resetValues() },
                    modifier = Modifier
    //                    .background(Color.Blue)
                        .size(200.dp, 100.dp)
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "音程初期化\n気圧:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${sensorViewModel.pressure.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Row(
                    //
                ) {
                    Button(
                        onClick = { currentWaveform = 0; onWaveformChange(currentWaveform) },
                        modifier = Modifier
            //                    .background(Color.Blue)
                            .size(80.dp, 50.dp)
                    ) {
                        Text(
                            text = "Sin",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        onClick = { currentWaveform = 1; onWaveformChange(currentWaveform) },
                        modifier = Modifier
            //                    .background(Color.Blue)
                            .size(80.dp, 50.dp)
                    ) {
                        Text(
                            text = "Tri",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        onClick = { currentWaveform = 2; onWaveformChange(currentWaveform) },
                        modifier = Modifier
            //                    .background(Color.Blue)
                            .size(80.dp, 50.dp)
                    ) {
                        Text(
                            text = "Saw",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        onClick = { currentWaveform = 3; onWaveformChange(currentWaveform) },
                        modifier = Modifier
            //                    .background(Color.Blue)
                            .size(80.dp, 50.dp)
                    ) {
                        Text(
                            text = "Squ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 16.dp)
                        .fillMaxWidth(0.5f),
                ) {
                    Text(
                        text = "angle-x: ${sensorViewModel.angle.x.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "angle-y: ${sensorViewModel.angle.y.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "angle-z: ${sensorViewModel.angle.z.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "accel.x: ${sensorViewModel.accel.x.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "accel.y: ${sensorViewModel.accel.y.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "accel.z: ${sensorViewModel.accel.z.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 16.dp)
                        .fillMaxWidth(0.5f),
                ) {
                    Text(
                        text = "音量: ${sensorViewModel.volume.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "音程: ${sensorViewModel.tone.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "発音: ${sensorViewModel.articulation.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "l-Accel.z: ${sensorViewModel.linearAccel.z.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "velo.z: ${sensorViewModel.velocity.z.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "pos.z: ${sensorViewModel.position.z.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    MakeDraggable()

    // 動的にサイン波の周波数と音量を調整
    onFrequencyChange(sensorViewModel.tone.value)
    onAmplitudeChange(sensorViewModel.volume.value)
    onPulseWidthChange(sensorViewModel.articulation.value)
}