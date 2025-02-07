package com.example.phonesynth.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.phonesynth.R
import com.example.phonesynth.component.*
import com.example.phonesynth.ui.theme.LightGray
import com.example.phonesynth.viewModel.SensorViewModel
import com.example.phonesynth.component.Sensors.Companion.accel
import com.example.phonesynth.component.Sensors.Companion.linearAccel
import com.example.phonesynth.component.Sensors.Companion.velocity
import com.example.phonesynth.component.Sensors.Companion.position
import com.example.phonesynth.component.Sensors.Companion.gyro
import com.example.phonesynth.component.Sensors.Companion.angle

@Composable
fun SensorViewScreen(sensorViewModel: SensorViewModel, sensor: Sensors) {
    val repo = Repository()

    Column {
        Row {
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 16.dp)
                    .fillMaxWidth(0.5f),
            ) {
                Text(
                    text = "angle-x: ${angle.x.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "angle-y: ${angle.y.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "angle-z: ${angle.z.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                //                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "velo.x: ${velocity.x.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "velo.y: ${velocity.y.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "velo.z: ${velocity.z.value}",
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
                    text = "音量: ${sensor.repo.volume.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "音程: ${sensor.repo.tone.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "発音: ${sensor.repo.articulation.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "l-Accel.z: ${linearAccel.z.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "velo.z: ${velocity.z.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "pos.z: ${position.z.value}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // reset sensors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(50.dp , 0.dp)
                .background(LightGray)
                .clickable(
//                    onClick = { setInitValues() }
                    onClick = {
                        linearAccel.z.value = 0f
                        velocity.z.value = 0f
                        position.z.value = 0f

                        linearAccel.prevZ.value = 0f
                        velocity.prevZ.value = 0f
                        position.prevZ.value = 0f

                    }
                )
            ,
            contentAlignment = Alignment.Center
        ) {
            Row {
                Image(
                    painterResource(id = R.drawable.refresh),
                    contentDescription = null
                )
                Column {
                    Text(
                        text = "accel初期化\n気圧:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${sensor.repo.pressure.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        // reset sensors
        Box(
            modifier = Modifier
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
                    contentDescription = null
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