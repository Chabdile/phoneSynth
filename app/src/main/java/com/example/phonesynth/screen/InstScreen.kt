package com.example.phonesynth.screen

import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.phonesynth.component.*
import com.example.phonesynth.ui.MakePadButton
import com.example.phonesynth.ui.MakeSlider
import com.example.phonesynth.ui.MakeTransposeSlider
import com.example.phonesynth.viewModel.InstViewModel

@Composable
fun InstScreen(
    instViewModel: InstViewModel, // InstViewModelを取得
    sensor: Sensor,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        instViewModel.startStream() // 初期化時にストリーム開始
        sensor.resetValues()
        //    val state = instViewModel.state.collectAsState()
    }

    //破棄時の処理
    DisposableEffect(Unit) {
        onDispose {
            instViewModel.stopStream()
//            sensor.destroy()
        }
    }

    Column {
//        Text("Screen 1: ${state.value}")
        Button(onClick = { navController.navigate("menuViewModel") }) {
            Text("Go to menuViewModel")
        }
        //    Button(onClick = { instViewModel.startMusic() }) {
        //
        //    }
        //    //beta
        //    MakeDraggable()

        Column(
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                MakePadButton(
                    { sensor.isButtonPress(true) },
                    { sensor.isButtonPress() },
                    textInput = "音を切る\n",
                    0.5f
                )

                MakePadButton(
                    { sensor.isSnap = false },
                    { sensor.isSnap = true },
                    textInput = "音階変更\n" +
                            "Pitch Snap: ${sensor.isSnap}\n" +
                            "インタラクティブに色を変えたり階名出したい\n",
                    1.0f
                )
            }

            MakeTransposeSlider()
//            MakeSlider()  // i want assign plugins

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
                        onClick = { sensor.resetValues() },
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
                                text = "${sensor.pressure.value}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Row(
                        //
                    ) {
                        Button(
                            onClick = {
                                currentWaveform = 0; instViewModel.setWaveform(currentWaveform)
                            },
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
                            onClick = {
                                currentWaveform = 1; instViewModel.setWaveform(currentWaveform)
                            },
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
                            onClick = {
                                currentWaveform = 2; instViewModel.setWaveform(currentWaveform)
                            },
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
                            onClick = {
                                currentWaveform = 3; instViewModel.setWaveform(currentWaveform)
                            },
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
                            text = "angle-x: ${sensor.angle.x.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "angle-y: ${sensor.angle.y.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "angle-z: ${sensor.angle.z.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        //                horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "velo.x: ${sensor.velocity.x.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "velo.y: ${sensor.velocity.y.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "velo.z: ${sensor.velocity.z.value}",
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
                            text = "音量: ${sensor.volume.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "音程: ${sensor.tone.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "発音: ${sensor.articulation.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = "l-Accel.z: ${sensor.linearAccel.z.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "velo.z: ${sensor.velocity.z.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "pos.z: ${sensor.position.z.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // 動的にサイン波の周波数と音量を調整
        instViewModel.setFrequency(sensor.tone.value)
        instViewModel.setAmplitude(sensor.volume.value)
        instViewModel.setPulseWidth(sensor.articulation.value)
    }
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