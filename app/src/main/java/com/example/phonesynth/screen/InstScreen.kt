package com.example.phonesynth.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.phonesynth.R
import com.example.phonesynth.component.*
import com.example.phonesynth.ui.MakeChangeWFButton
import com.example.phonesynth.ui.MakePadButton
import com.example.phonesynth.ui.MakeTransposeSlider
import com.example.phonesynth.ui.theme.LightGray
import com.example.phonesynth.viewModel.InstViewModel

@Composable
fun InstScreen(instViewModel: InstViewModel, sensor: Sensors) {
    
    LaunchedEffect(Unit) {
        sensor.repo.volume = mutableFloatStateOf(0.0f)
        instViewModel.startStream() // 初期化時にストリーム開始
        //    val state = instViewModel.state.collectAsState()
    }

    //破棄時の処理
    DisposableEffect(Unit) {
        onDispose {
            sensor.repo.volume = mutableFloatStateOf(0.0f)
            instViewModel.stopStream()
        }
    }
    //    //beta
    //    MakeDraggable()

    // body
    BoxWithConstraints {
        val paddingValue = 10.dp
        val width = maxWidth - paddingValue * 2
        val height = maxHeight - paddingValue * 2

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                MakePadButton(
                    { sensor.repo.pressStopButton(true) },
                    { sensor.repo.pressStopButton(false) },
                    icon = R.drawable.volume_mute,
                    textInput = "音を切る\n",
                    width / 2
                )
                Spacer(modifier = Modifier.width(2.dp))
                MakePadButton(
                    { sensor.repo.isSnap = false },
                    { sensor.repo.isSnap = true },
                    icon = 0,
                    textInput = "長音階の適用\n" +
                            "Pitch Snap: ${sensor.repo.isSnap}\n",
                    width / 2
                )
            }

            MakeTransposeSlider(sensor)
            //            MakeSlider()  // i want assign plugins

            //margin top 20px
            HorizontalDivider(modifier = Modifier.padding(20.dp, 10.dp))

            // change waveform
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                MakeChangeWFButton(
                    { currentWaveform = 0; instViewModel.setWaveform(currentWaveform) },
                    "Sin",
                    (width / 4),
                    100.dp
                )
                MakeChangeWFButton(
                    { currentWaveform = 1; instViewModel.setWaveform(currentWaveform) },
                    "Tri",
                    (width / 4),
                    100.dp
                )
                MakeChangeWFButton(
                    { currentWaveform = 2; instViewModel.setWaveform(currentWaveform) },
                    "Saw",
                    (width / 4),
                    100.dp
                )
                MakeChangeWFButton(
                    { currentWaveform = 3; instViewModel.setWaveform(currentWaveform) },
                    "Squ",
                    (width / 4),
                    100.dp
                )
            }

            // reset sensors
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable(
                        onClick = { sensor.repo.setInitValues() }
                    )
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(LightGray),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Image(
                        painterResource(id = R.drawable.refresh),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp, 30.dp)
                    )
                    Column {
                        Text(
                            text = "音程初期化\n気圧:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${sensor.repo.pressure.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // 動的にサイン波の周波数と音量を調整
    instViewModel.setFrequency(sensor.repo.tone.value)
    instViewModel.setAmplitude(sensor.repo.volume.value)
    instViewModel.setPulseWidth(sensor.repo.articulation.value)
}
