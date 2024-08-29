package com.example.phonesynth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonesynth.ui.theme.PhoneSynthTheme

//.cppと共有したいけど決め打ち
enum class Waveform {
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
    private external fun setFrequency(frequency: Double)
    private external fun setAmplitude(amplitude: Float)
    private external fun setWaveform(waveform: Waveform)
    private external fun setSampleRate(sampleRate: Double)
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
                        onAmplitudeChange = { setAmplitude(it) }
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
    onFrequencyChange: (Double) -> Unit,
    onAmplitudeChange: (Float) -> Unit
) {
    Column(
//        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "X: ${sensorViewModel.x.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Y: ${sensorViewModel.y.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Z: ${sensorViewModel.z.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
//            Column(
//                modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "Roll: ${sensorViewModel.roll.value}°",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Text(
//                    text = "Pitch: ${sensorViewModel.pitch.value}°",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Text(
//                    text = "Yaw: ${sensorViewModel.yaw.value}°",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
        }
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { sensorViewModel.resetInitPressure() },
                modifier = Modifier
//                    .background(Color.Blue)
                    .size(250.dp, 150.dp)
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "音程初期化",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "気圧: ${sensorViewModel.pressure.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
//    Column(
//        verticalArrangement = Arrangement.SpaceEvenly
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text(
//                text = "音量: ${sensorViewModel.volume.value}",
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Text(
//                text = "音程: ${sensorViewModel.tone.value}",
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Text(
//                text = "音程: ${sensorViewModel.tone.value}",
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//        Column(
//            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Button(
//                onClick = { sensorViewModel.resetInitPressure() },
//                modifier = Modifier.size(250.dp, 150.dp)
//            ) {
//                Text(
//                    text = "音程初期化",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//            }
//        }
//    }

    // 動的にサイン波の周波数と音量を調整
    onFrequencyChange(sensorViewModel.tone.value.toDouble())
    onAmplitudeChange(sensorViewModel.volume.value)
}