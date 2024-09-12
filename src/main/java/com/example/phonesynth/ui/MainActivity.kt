package com.example.phonesynth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonesynth.ui.theme.PhoneSynthTheme
import com.example.phonesynth.ui.theme.Purple200

//.cppと共有したいけど決め打ち
enum class Wf {
    Sine,
    Triangle,
    Sawtooth,
    Square
}
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
//    private external fun setWaveform(ordinal: Wf)
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
//                                onWaveformChange = { setWaveform(it) }
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
fun SensorScreen(
    sensorViewModel: SensorViewModel = viewModel(),
    onFrequencyChange: (Float) -> Unit,
    onAmplitudeChange: (Float) -> Unit,
//    onWaveformChange: (Wf) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
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
//            Row(
//                //
//            ) {
//                Button(
//                    onClick = { onWaveformChange(Wf.Sine) },
//                    modifier = Modifier
//        //                    .background(Color.Blue)
//                        .size(200.dp, 100.dp)
//                ) {
//                    Text(
//                        text = "Sin",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//                Button(
//                    onClick = { onWaveformChange(Wf.Triangle) },
//                    modifier = Modifier
//        //                    .background(Color.Blue)
//                        .size(200.dp, 100.dp)
//                ) {
//                    Text(
//                        text = "Tri",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//                Button(
//                    onClick = { onWaveformChange(Wf.Sawtooth) },
//                    modifier = Modifier
//        //                    .background(Color.Blue)
//                        .size(200.dp, 100.dp)
//                ) {
//                    Text(
//                        text = "Saw",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//                Button(
//                    onClick = { onWaveformChange(Wf.Square) },
//                    modifier = Modifier
//        //                    .background(Color.Blue)
//                        .size(200.dp, 100.dp)
//                ) {
//                    Text(
//                        text = "Squ",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//            }
        }
    }
    // 動的にサイン波の周波数と音量を調整
    onFrequencyChange(sensorViewModel.tone.value)
    onAmplitudeChange(sensorViewModel.volume.value)
}