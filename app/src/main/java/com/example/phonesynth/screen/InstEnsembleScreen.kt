package com.example.phonesynth.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.phonesynth.component.AudioParam
import com.example.phonesynth.ui.DropdownForEnsemble
import com.example.phonesynth.ui.InstrumentPanelForEnsemble
import com.example.phonesynth.ui.theme.Purple200
import com.example.phonesynth.viewModel.InstEnsembleViewModel
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes

@Composable
fun InstEnsembleScreen(viewModel: InstEnsembleViewModel) {
    LaunchedEffect(Unit) {
        viewModel.volume.floatValue = 0f
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopConnection()
            viewModel.stopStream()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (!viewModel.isRunning.value) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    Text( if(viewModel.isHost.value) "Host Mode" else "Guest Mode" )
                    Switch(
                        checked = viewModel.isHost.value,
                        onCheckedChange = { viewModel.switchMode() }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(onClick = {
                        if (viewModel.isHost.value) {
                            viewModel.startConnection()
                            viewModel.startStream()
                        }
                    },
                        enabled = viewModel.isHost.value
                    ) {
                        Text("Start")
                    }
                }
            }
            if (viewModel.isHost.value) {
                OutlinedTextField(
                    value = viewModel.myroomName.value,
                    onValueChange = { viewModel.myroomName.value = it },
                    label = { Text("Room Name") }
                )
            } else {
                LazyColumn(modifier = Modifier
//                    .height(150.dp)
                ) {
                    items(viewModel.roomList.value) { room ->
                        Text(room.info.endpointName, modifier = Modifier
                            .padding(30.dp)
                            .border(3.dp, Purple200, RectangleShape)
                            .clickable {
                                viewModel.connectToRoom(room.endpointId)
                            })
                    }
                }
            }
            Text("Status: ${viewModel.connectionStatus.value}")

        } else {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    Text("Status: ${viewModel.connectionStatus.value}")
//                    Text("Members: ${viewModel.connectedMembers.value}")
                    if (viewModel.isHost.value) {
                        Text("receiveParam: ${viewModel.receivedParam.value}")
                        Text("receiveValue: ${viewModel.receivedValue.floatValue}")
                    } else {
                        Text("sendParam: ${viewModel.selectedKey.value}")
                        Text("sendValue: ${viewModel.parameterProcessor.paramRemap[viewModel.selectedKey.value]!!.value}")
                    }

//                    Text("paramKeys: ${viewModel.paramKeys}")
//                    Text("selectedKey: ${viewModel.selectedKey.value}")
//                    Text("tone: ${viewModel.sensors.repo.tone.floatValue}")
//                    Text("volume: ${viewModel.sensors.repo.volume.floatValue}")
//                    Text("viewModel.paramRemap[viewModel.selectedKey.value]!!: ${viewModel.paramRemap[viewModel.selectedKey.value]!!}")

                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = {
                            viewModel.stopConnection()
                            viewModel.stopStream()
                            viewModel.sensors.unregisterSensors()
                            viewModel.initValues()
                        },
                        enabled = viewModel.isRunning.value
                    ) {
                        Text("Stop")
                    }
                }
            }

            // paramKeys 初期値
            LaunchedEffect(ConnectionsStatusCodes.STATUS_OK) {
                // Host: param.VOLUME,
                if (viewModel.isHost.value) {
                    viewModel.selectedKey.value = AudioParam.VOLUME
                } else {
                // 1st Guest: paramKeys[2], 2nd Guest: paramKeys[3], other: none
                    viewModel.selectedKey.value = AudioParam.TONE
                }
            }
            DropdownForEnsemble(viewModel.selectedKey.value) { viewModel.selectedKey.value = it }

            /* Error when tap the dropdown menu
            * Parcel            Expecting binder but got null!
            * OpenGLRenderer    Unable to match the desired swap behavior.
            * mple.phonesynth   Cleared Reference was only reachable from finalizer (only reported once)
            * */

            HorizontalDivider(Modifier.padding(20.dp, 10.dp))

            // 接続後に音を鳴らし始める

            InstrumentPanelForEnsemble(viewModel.sensors.repo, viewModel.audioController, viewModel.selectedKey.value)
            viewModel.updateAudio()
            viewModel.mapping()




            // selectedKeyやtestsendPayload、作りかけsetVolumeの読み込みでDropdownForEnsembleが再読み込みされる。値を更新したいけどDropdownの邪魔になる
            // selectedKeyのトリガーに加えupdateAudio()も画面操作不能の要因ぽい
        }
    }
}
