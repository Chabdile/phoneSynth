package com.example.phonesynth.screen

import android.widget.Spinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import com.example.phonesynth.ui.theme.Gray
import com.example.phonesynth.ui.theme.LightGray
import com.example.phonesynth.ui.theme.Silver
import com.example.phonesynth.viewModel.InstBellViewModel

@Composable
fun InstBellScreen(instBellViewModel: InstBellViewModel, sensor: Sensors) {
//    LaunchedEffect(Unit) {
//        sensor.volume = mutableFloatStateOf(0.0f)
//        instViewModel.startStream() // 初期化時にストリーム開始
//        //    val state = instViewModel.state.collectAsState()
//    }
//
//    //破棄時の処理
//    DisposableEffect(Unit) {
//        onDispose {
//            sensor.volume = mutableFloatStateOf(0.0f)
//        }
//    }

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
            Box(
                modifier = Modifier
                    .size(width, height / 4)
                    .background(Silver)
            )
            //margin top 20px
            HorizontalDivider(modifier = Modifier.padding(20.dp, 10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.Absolute.Left
            ) {
                Column(
                    modifier = Modifier
//                            .padding(end = 30.dp)
                        .size(width * 2 / 3, height / 3)
                        .background(Silver),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "left")
                }
            }
            //margin top 20px
            HorizontalDivider(modifier = Modifier.padding(20.dp, 10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.Absolute.Right
            ) {
                Column(
                    modifier = Modifier
//                            .padding(start = 30.dp)
                        .size(width * 2 / 3, height / 3)
                        .background(Silver),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "right")
//                    val spinner: Spinner = findViewById(R.id.planets_spinner)
                }
            }
        }
    }
}
